package com.amt.doineedit_firebase.Users

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.amt.doineedit_firebase.ItemsActivity
import com.amt.doineedit_firebase.LoginActivity
import com.google.firebase.auth.FirebaseAuth

/*
 User Account management class
 Can login and register user
* */
class UserRepositoryImplement constructor(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
): UserRepository {

    override fun login(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(context, ItemsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // To start activity on this class
                context.startActivity(intent)
            }
        }
            .addOnFailureListener {
                // if login failed, show the error message that user can understand
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // go back to login screen if successful
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else {
                    // if registration failed, show the error message that user can understand
                    Toast.makeText(
                        context, task.exception!!.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}