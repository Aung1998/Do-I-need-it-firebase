package com.amt.doineedit_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnNavRegister = findViewById<Button>(R.id.btn_to_register)

        btnNavRegister.setOnClickListener { navigateToRegister() }

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

    //
    private fun accountLogin(email:String, password:String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, ItemsActivity::class.java)
                startActivity(intent)
            }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun navigateToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}