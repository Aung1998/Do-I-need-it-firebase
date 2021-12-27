package com.amt.doineedit_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val tvPasswordNotMatched = findViewById<TextView>(R.id.textViewPassword)
        val etConfirmPassword = findViewById<TextInputLayout>(R.id.textLayoutConfirmPassword).editText
        val etPassword = findViewById<TextInputLayout>(R.id.textLayoutPassword).editText
        etConfirmPassword?.doAfterTextChanged {
            if(etConfirmPassword.text.toString() == etPassword?.text.toString()){
                tvPasswordNotMatched.visibility = View.INVISIBLE
            }
            else{
                tvPasswordNotMatched.visibility = View.VISIBLE
            }
        }

        val btnRegister = findViewById<Button>(R.id.btn_register)

        val btnNavLogin = findViewById<Button>(R.id.btnNavLoogin)

        btnNavLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener{
            val email = findViewById<TextInputLayout>(R.id.textLayoutEmail)
                .editText?.text.toString()

            val password = etPassword?.text.toString()

            val pass = etConfirmPassword?.text.toString()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show()
            }
            else if(password != pass){
                // do nothing since error message was shown as user type
            }
            else{
                register(email, password)
            }
        }
    }

    private fun register(email:String,  password:String){
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
            } else {
                // if registration failed, show the error message that user can understand
                Toast.makeText(baseContext, task.exception!!.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
            }
    }
}