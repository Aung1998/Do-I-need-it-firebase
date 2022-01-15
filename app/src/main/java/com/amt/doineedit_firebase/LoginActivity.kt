package com.amt.doineedit_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amt.doineedit_firebase.Users.UserRepository
import com.amt.doineedit_firebase.Users.UserRepositoryImplement
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnNavRegister = findViewById<Button>(R.id.btn_to_register)

        btnNavRegister.setOnClickListener { navigateToRegister() }

        btnLogin.setOnClickListener {
            val email = findViewById<TextInputLayout>(R.id.textLayout_email)
                .editText?.text.toString()

            val password = findViewById<TextInputLayout>(R.id.textLayout_password)
                .editText?.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show()
            } else {
                accountLogin(email, password)
            }
        }
    }

    private fun accountLogin(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        userRepository = UserRepositoryImplement(auth, baseContext)
        userRepository.login(email, password)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}