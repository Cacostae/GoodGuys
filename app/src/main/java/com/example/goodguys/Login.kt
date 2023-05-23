package com.example.goodguys


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodguys.databinding.ActivityLoginBinding
import com.example.goodguys.ui.theme.MainActivity

import com.google.firebase.auth.FirebaseAuth




class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    //private val usuarios: ArrayList<Usuario> = java.util.ArrayList<Usuario>()
    private val user = "email"
    private val pass = "password"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (ActivityLoginBinding.inflate(layoutInflater).also { binding = it}.root)

        binding.usernameEditText.text =  Editable.Factory.getInstance().newEditable("cristian@mail.com")
        binding.passwordEditText.text =  Editable.Factory.getInstance().newEditable("123456")

        binding.loginButton.setOnClickListener { v ->
            if (!binding.usernameEditText.getText().toString()
                    .isEmpty() && !binding.passwordEditText.getText().toString().isEmpty()
            ) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        binding.usernameEditText.getText().toString(),
                        binding.passwordEditText.getText().toString()
                    )
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(user, binding.usernameEditText.getText().toString())
                intent.putExtra(pass, binding.passwordEditText.getText().toString())
                startActivity(intent)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Usuario o contraseña erroneos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.registerButton.setOnClickListener { v ->
            val intent = Intent(this, SingIn::class.java)
            startActivity(intent)
        }
    }
}