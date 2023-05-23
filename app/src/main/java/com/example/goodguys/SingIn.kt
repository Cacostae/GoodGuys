package com.example.goodguys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodguys.databinding.ActivitySingInBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class SingIn : AppCompatActivity() {

    private lateinit var binding: ActivitySingInBinding
    private val dataBase = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (ActivitySingInBinding.inflate(layoutInflater).also { binding = it}.root)


        val randomInt = (Math.random() * 500).toInt()

        val nu = "$randomInt@gmail.com"
        val pa = randomInt.toString() + "11111111"
        val usu ="$randomInt"

        binding.emailEditText.setText(nu)
        binding.passwordEditText.setText(pa)
        binding.usernameEditText.setText(usu)


        binding.btnVolver.setOnClickListener { v ->
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.btnSiguiente.setOnClickListener { v ->

            if (camposVacios()) {
                if (!isValidDate(binding.bornDateEditText.text.toString())){
                    binding.bornDateEditText.error = "Fecha no válida"
                    return@setOnClickListener
                }
                if (emailValido(binding.emailEditText.text.toString())) {
                    doesEmailExist(binding.emailEditText.text.toString(),
                        onSuccess = { emailExiste ->
                            if (emailExiste) {
                                binding.emailEditText.error = "El email introducido ya está registrrado"
                            } else {
                                searchUsersByUserName(binding.usernameEditText.text.toString(),
                                    onSuccess = { matchingDocuments ->
                                        // hacer algo con los documentos que coinciden con la búsqueda
                                        if (matchingDocuments.isNotEmpty()) binding.usernameEditText.error = "El usuario introducido ya está registrrado"
                                        else{

                                            firebaseAuth.createUserWithEmailAndPassword(
                                                binding.emailEditText.text.toString(),
                                                binding.passwordEditText.text.toString()
                                            ).addOnCompleteListener { c: Task<AuthResult?> ->
                                                if (c.isSuccessful) {
                                                    Toast.makeText(applicationContext, "Agregados", Toast.LENGTH_SHORT).show()
                                                    dataBase.collection("usuarios")
                                                        .document(binding.emailEditText.text.toString())
                                                        .set(
                                                            hashMapOf("nombre" to binding.nameEditText.text.toString(),
                                                                "primerApellido" to binding.firstSurnameEditText.text.toString(),
                                                                "segundoApellido" to binding.secondSurnameEditText.text.toString(),
                                                                "email" to binding.emailEditText.text.toString(),
                                                                "usuario" to binding.usernameEditText.text.toString(),
                                                                "contraseña" to binding.passwordEditText.text.toString(),
                                                                "direccion" to binding.directionEditText.text.toString(),
                                                                "fechaNacimiento" to binding.bornDateEditText.text.toString())
                                                        )

                                                    val intent = Intent(this, FirstPet::class.java)
                                                    intent.putExtra("email", binding.emailEditText.text.toString())
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(applicationContext, "No va", Toast.LENGTH_SHORT)
                                                        .show()
                                                }
                                            }
                                        }
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(applicationContext, "Error inesperado", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                        }
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(applicationContext, "Error inesperado", Toast.LENGTH_SHORT).show()
                                    }
                                )

                } else {
                    binding.emailEditText.error = "El email introducido no es correcto"
                }

            } else {
                Toast.makeText(applicationContext,
                    "Usuario o contraseña erroneos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
    fun camposVacios():Boolean{
        var ok = true

        if (binding.nameEditText.text.toString().isEmpty()){
            binding.nameEditText.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.emailEditText.text.toString().isEmpty()){
            binding.emailEditText.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.usernameEditText.text.toString().isEmpty()){
            binding.usernameEditText.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.passwordEditText.text.toString().isEmpty()){
            binding.passwordEditText.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.directionEditText.text.toString().isEmpty()){
            binding.directionEditText.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.bornDateEditText.text.toString().isEmpty()){
            binding.bornDateEditText.error = "Este campo es obligatorio"
            ok = false
        }
        return ok
    }
    fun emailValido(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }
    fun doesEmailExist(email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val collectionRef = FirebaseFirestore.getInstance().collection("usuarios")
        collectionRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val emailExists = !querySnapshot.isEmpty
                onSuccess(emailExists)
            }
            .addOnFailureListener(onFailure)
    }
    fun searchUsersByUserName(userName: String, onSuccess: (List<DocumentSnapshot>) -> Unit, onFailure: (Exception) -> Unit) {
        val collectionRef = FirebaseFirestore.getInstance().collection("usuarios")
        collectionRef.whereEqualTo("usuario", userName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val matchingDocuments = querySnapshot.documents
                onSuccess(matchingDocuments)
            }
            .addOnFailureListener(onFailure)
    }
    fun isValidDate(dateString: String): Boolean {
        val regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d)\$".toRegex()
        val matchResult = regex.find(dateString)

        if (matchResult != null) {
            val day = matchResult.groupValues[1].toInt()
            val month = matchResult.groupValues[2].toInt()
            val year = matchResult.groupValues[3].toInt()

            if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                return false // abril, junio, septiembre, noviembre no tienen 31 días
            } else if (day >= 30 && month == 2) {
                return false // febrero no tiene más de 29 días
            } else if (day == 29 && month == 2 && (year % 4 != 0 || (year % 100 == 0 && year % 400 != 0))) {
                return false // febrero no tiene 29 días en años no bisiestos
            }

            return true // la fecha es válida
        }

        return false // no coincide con el formato de fecha esperado
    }



}