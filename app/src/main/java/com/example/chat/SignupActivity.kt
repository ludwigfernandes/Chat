package com.example.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat.databinding.ActivitySignupBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var storedVerificationId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = FirebaseAuth.getInstance()

        binding.btnGetOTP.setOnClickListener {
            val etPhoneNumber = binding.etPhoneNumber.editText?.text.toString()
            val etName = binding.etName.editText?.text.toString()
            if (etPhoneNumber.length == 10 && etName.length > 1) {
                val phoneNumber = "+91 $etPhoneNumber"
                sendCode(phoneNumber)
                binding.etPhoneNumber.error = null
                binding.etName.error = null
            } else {
                binding.etName.error = null
                binding.etPhoneNumber.error = "Invalid Phone no."
                if (etName.length == 0) {
                    binding.etName.error = "Enter your name"
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            val OTP = binding.etOTP.editText?.text.toString()
            if (OTP.length == 6) {
                verifyCode(OTP)
                binding.etOTP.error = null
            } else {
                binding.etOTP.error = "Invalid OTP"
                Toast.makeText(this, "Enter valid OTP!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid
                    val name = binding.etName.editText?.text.toString()
                    val phoneNumber = binding.etPhoneNumber.editText?.text.toString()

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["profileImage"] = ""
                    hashMap["name"] = name
                    hashMap["phoneNumber"] = phoneNumber

                    databaseReference.setValue(hashMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, UsersActivity::class.java))
                            finish()
                            Toast.makeText(this, "Data entered", Toast.LENGTH_SHORT).show()

                        }
                    }

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    //val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.etOTP.error = "Invalid OTP"
                    }
                    // Update UI
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")

            storedVerificationId = verificationId

            binding.etPhoneNumber.editText?.isEnabled = false
            binding.etName.editText?.isEnabled = false
            binding.btnGetOTP.visibility = View.GONE
            binding.etOTP.visibility = View.VISIBLE
            binding.btnSubmit.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val TAG = "MyApp"
    }
}