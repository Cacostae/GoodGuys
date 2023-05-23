package com.example.goodguys

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.goodguys.databinding.ActivityFristPetBinding
import com.example.goodguys.ui.theme.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FirstPet : AppCompatActivity() {


    private lateinit var binding: ActivityFristPetBinding
    private val dataBase = FirebaseFirestore.getInstance()
    private var storageReference: StorageReference? = null
    private var urlImagen: String? = null
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (ActivityFristPetBinding.inflate(layoutInflater).also { binding = it}.root)

        val email: String? = getEmail()

        storageReference = FirebaseStorage.getInstance().reference


        binding.btnVolver.setOnClickListener { v ->
            val intent = Intent(this, SingIn::class.java)
            startActivity(intent)
        }

        binding.btnImgGallery.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, FirstPet.GALLERY_INTENT)
        }
        binding.btnImgCamera.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }



        binding.btnFinalizar.setOnClickListener { v ->
            if (camposVacios()) {
                dataBase.collection("usuarios").document(email!!).collection("mascotas")
                    .document(binding.etNombreMascota.text.toString()).set(
                        hashMapOf(
                            "nombre" to binding.etNombreMascota.text.toString(),
                            "tipo" to binding.etTipoMascota.text.toString(),
                            "raza" to binding.etRaza.text.toString(),
                            "tamaño" to binding.etTamanyo.text.toString(),
                            "fechaNacimientoMasc" to binding.etFechaNacimientoMascota.text.toString(),
                            "genero" to binding.etGenero.text.toString(),
                            "imagen" to urlImagen
                        )
                    )

                val intent = Intent(this, Login::class.java)
                //intent.putExtra("email", email)

                startActivity(intent)
            }
        }

    }
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap

                binding.imgFotoMascota.setImageBitmap(imageBitmap)

                // Obtener una referencia a la instancia de Firebase Storage


                // Convertir el Bitmap a un ByteArrayOutputSteam
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Crear una referencia al archivo en Firebase Storage
                val imagesRef = storageReference!!.child(getEmail()!!).child(data.toString())

                // Subir el archivo a Firebase Storage
                val uploadTask = imagesRef.putBytes(data)
                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }

                    // Continue with the task to get the download URL
                    imagesRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        urlImagen = downloadUri.toString()
                        Glide.with(this)
                            .load(downloadUri)
                            .fitCenter()
                            .centerCrop()
                            .circleCrop()
                            .into(binding.imgFotoMascota)
                    } else {
                        // Handle failures
                        // ...
                    }
                }

            }
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FirstPet.GALLERY_INTENT && resultCode == RESULT_OK) {
            val uri = data!!.data
            val ref = storageReference!!.child(getEmail()!!).child(uri!!.lastPathSegment!!)
            val uploadTask = ref.putFile(uri)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                // Continue with the task to get the download URL
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    urlImagen = downloadUri.toString()
                    Glide.with(this)
                        .load(downloadUri)
                        .fitCenter()
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imgFotoMascota)
                } else {
                    // Handle failures
                    // ...
                }
            }
        }
        if (requestCode == FirstPet.CAMERA_INTENT && resultCode == RESULT_OK) {


            val imageFile = File(photoPath)
            val imageUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", imageFile)

            // Asignamos la imagen al ImageView
            binding.imgFotoMascota.setImageURI(imageUri)


//            val uri = data!!.data
//            val ref = storageReference!!.child(getEmail()!!).child(uri!!.lastPathSegment!!)
//            val uploadTask = ref.putFile(uri)
//            val urlTask = uploadTask.continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    throw task.exception!!
//                }
//
//                // Continue with the task to get the download URL
//                ref.downloadUrl
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val downloadUri = task.result
//                    urlImagen = downloadUri.toString()
//                    Glide.with(this)
//                        .load(downloadUri)
//                        .fitCenter()
//                        .centerCrop()
//                        .circleCrop()
//                        .into(binding.imgFotoMascota)
//                } else {
//                    // Handle failures
//                    // ...
//                }
            //}
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Crea un nombre de archivo único basado en la fecha y hora actual
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    fun getEmail(): String? {
        val intentFromSingin = intent
        val extras = intentFromSingin.extras
        return extras!!.getString("email")
    }

    fun camposVacios():Boolean{
        var ok = true

        if (binding.etNombreMascota.text.toString().isEmpty()){
            binding.etNombreMascota.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.etTipoMascota.text.toString().isEmpty()){
            binding.etTipoMascota.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.etTamanyo.text.toString().isEmpty()){
            binding.etTamanyo.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.etRaza.text.toString().isEmpty()){
            binding.etRaza.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.etFechaNacimientoMascota.text.toString().isEmpty()){
            binding.etFechaNacimientoMascota.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.etGenero.text.toString().isEmpty()){
            binding.etGenero.error = "Este campo es obligatorio"
            ok = false
        }
        if (binding.imgFotoMascota.drawable == null){
            Toast.makeText(applicationContext,"Debes cargar o hacer una foto",Toast.LENGTH_SHORT).show()
            ok = false
        }
        return ok
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Comprueba si hay una app de cámara para manejar la solicitud
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Crea el archivo donde se guardará la imagen
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error al crear el archivo
                    null
                }
                // Si se ha creado el archivo, lanza la cámara para tomar la foto
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.myapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, FirstPet.CAMERA_INTENT)
                }
            }
        }
    }

    companion object {
        private const val CAMERA_INTENT = 2
        private const val GALLERY_INTENT = 1
    }
}