package com.example.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.databinding.ActivityUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private val userList = ArrayList<User>()

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        getUserList()

    }

    private fun getUserList() {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val currentUserUid = auth.currentUser!!.uid

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue((User::class.java))

                    user?.let {
                        if (it.userId != currentUserUid) {
                            userList.add(it)
                        }
                    }
                }
                binding.rvUsers.adapter = UserAdapter(userList) {
                    onItemCLick(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun onItemCLick(user: User) {
        Toast.makeText(this, "Clicked: ${user.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("userId", user.userId)
        intent.putExtra("name", user.name)
        intent.putExtra("phoneNumber", user.phoneNumber)
        startActivity(intent)
    }
}