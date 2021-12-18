package com.amt.doineedit_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener{
            val email = findViewById<TextInputLayout>(R.id.textLayout_email)
                .editText?.text.toString()

            val password = findViewById<TextInputLayout>(R.id.textLayout_password)
                .editText?.text.toString()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show()
            }
            else{
                accountLogin(email, password)
            }
        }
    }

    private fun accountLogin(email:String, password:String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful){
                //val intent = Intent(this, )
            }
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_LONG).show()
            }
    }
}