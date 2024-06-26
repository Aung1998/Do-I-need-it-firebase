package com.amt.doineedit_firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.amt.doineedit_firebase.Users.UserRepository
import com.amt.doineedit_firebase.Users.UserRepositoryImplement
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val tvPasswordNotMatched = findViewById<TextView>(R.id.textViewPassword)
        val etConfirmPassword =
            findViewById<TextInputLayout>(R.id.textLayoutConfirmPassword).editText
        val etPassword = findViewById<TextInputLayout>(R.id.textLayoutPassword).editText
        etConfirmPassword?.doAfterTextChanged {
            if (etConfirmPassword.text.toString() == etPassword?.text.toString()) {
                tvPasswordNotMatched.visibility = View.INVISIBLE
            } else {
                tvPasswordNotMatched.visibility = View.VISIBLE
            }
        }

        val btnRegister = findViewById<Button>(R.id.btn_register)

        val btnNavLogin = findViewById<Button>(R.id.btnNavLoogin)

        // log in screen navigate
        btnNavLogin.setOnClickListener {
            navigateLogin()
        }

        btnRegister.setOnClickListener {
            val email = findViewById<TextInputLayout>(R.id.textLayoutEmail)
                .editText?.text.toString()

            val password = etPassword?.text.toString()

            val pass = etConfirmPassword?.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show()
            } else if (password != pass) {
                // do nothing since error message was shown as user type
            } else {
                register(email, password)
            }
        }
    }

    // register user account to firebase
    private fun register(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        userRepository = UserRepositoryImplement(auth, baseContext)
        userRepository.register(email, password)
    }

    // navigate to login screen
    private fun navigateLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}