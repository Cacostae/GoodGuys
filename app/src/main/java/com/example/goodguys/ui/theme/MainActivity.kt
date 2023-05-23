package com.example.goodguys.ui.theme

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.goodguys.data.Mascota
import com.example.goodguys.data.Publicacion
import com.example.goodguys.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalGlideComposeApi::class)
class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dataBase = FirebaseFirestore.getInstance()
    private var mascotasListaTotal = mutableStateOf(emptyList<Mascota>())
    private var publicacionesListaTotal = mutableStateOf(emptyList<Publicacion>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (ActivityMainBinding.inflate(layoutInflater).also { binding = it}.root)
//        returnMascotasList { mascotas ->
//            mascotasListaTotal.value = mascotas
//        }
        returnPublicationsList { publicaciones ->
            publicacionesListaTotal.value = publicaciones
        }
        setContent {

//            MascotasList(mascotasListaTotal.value)
//            publicacionList(publicacionesListaTotal.value)

            //TwoRecyclerViews()
            OptionMenuWithFloatingButton()
        }


    }
    @Composable
    fun OptionMenuWithFloatingButton() {

        val navController = rememberNavController()

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(

                    backgroundColor = Color.Red,
                    onClick = { /* Acción del botón flotante */ }
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Add")
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
            bottomBar = {
                BottomAppBar(cutoutShape = CircleShape,
                            modifier = Modifier.wrapContentWidth(),
                            backgroundColor = Color.Transparent
                    ) {

                    Row(modifier = Modifier.fillMaxWidth()) {

                        IconButton(modifier = Modifier.weight(1f),
                            onClick = { /* Acción del botón 1 */ }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorite")
                        }
                        IconButton(modifier = Modifier.weight(1f),
                            onClick = { /* Acción del botón 2 */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(modifier = Modifier.weight(1f),
                            onClick = { /* Acción del botón 3 */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(modifier = Modifier.weight(1f),
                            onClick = { /* Acción del botón 4 */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }

                    }

                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                TwoRecyclerViews()
            }
        }
    }

    @Composable
    fun TwoRecyclerViews() {



        returnMascotasList { mascotas ->
            mascotasListaTotal.value = mascotas
        }




        Column() {
            Row(Modifier.wrapContentHeight()) {

                MascotasList(mascotasListaTotal.value)

            }
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(1f)) {

                publicacionList(publicacionesListaTotal.value)

            }
//            Row(Modifier
//                .wrapContentHeight()) {
//                OptionMenuWithFloatingButton()
//            }
        }
    }

    @Composable
    fun MascotasList(mascotas: List<Mascota>) {
        var bitmap: Bitmap
        var img:ImageBitmap? = null
        val rainbowColorsBrush = remember {
            Brush.sweepGradient(
                listOf(
                    Color(0xFF9575CD),
                    Color(0xFFBA68C8),
                    Color(0xFFE57373),
                    Color(0xFFFFB74D),
                    Color(0xFFFFF176),
                    Color(0xFFAED581),
                    Color(0xFF4DD0E1),
                    Color(0xFF9575CD)
                )
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                
        ) {

            items(mascotas) { mascota ->

                Column(modifier = Modifier.padding(4.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(mascota.imagen),
                        contentDescription = null,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(4.dp, rainbowColorsBrush),
                                CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = mascota.nombre,
                        modifier = Modifier
                            .padding(16.dp)

                    )
                }
            }
        }


    }

    @Composable
    fun publicacionList(publicaciones: List<Publicacion>) {
        var bitmap: Bitmap
        var img:ImageBitmap? = null

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()

        ) {

            items(publicaciones) { publicacion ->

                Column(modifier = Modifier.padding(8.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()){
                        AsyncImage(
                            model = publicacion.imagen,
                            //painter = rememberAsyncImagePainter(publicacion.imagen),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop

                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = publicacion.publicaUsuario,
                                style = TextStyle(color = Color.White),
                                modifier = Modifier
                                    .background(Color.Gray.copy(alpha = 0.5f))
                                    .padding(6.dp)

                            )
                        }

                    }



                    Text(
                        text = publicacion.contenido,
                        modifier = Modifier
                            .padding(16.dp)
                            )


                }
            }
        }
    }


    fun returnMascotasList(onMascotasLoaded: (MutableList<Mascota>) -> Unit) {

        val mascotasList = mutableListOf<Mascota>()

        val db = Firebase.firestore
        db.collection("usuarios").document(getEmail()!!)
            .collection("mascotas").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var nombre = document.getString("nombre")
                    var tipo = document.getString("tipo")
                    var raza = document.getString("raza")
                    var tamanyo = document.getString("tamaño")
                    var fechaNacimientoMascota = document.getString("fechaNacimientoMasc")
                    var genero = document.getString("genero")
                    var imagen = document.getString("imagen")

                    var mascota = Mascota(nombre!!, tipo!!, raza!!, tamanyo!!, fechaNacimientoMascota!!, genero!!, imagen!!)

                    mascotasList.add(mascota)
                }
                onMascotasLoaded(mascotasList)
            }
    }
    fun returnPublicationsList(onPublicacionesLoaded: (MutableList<Publicacion>) -> Unit) {
        val db = Firebase.firestore
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { usuariosResult ->
                val publicacionesList = mutableListOf<Publicacion>()

                for (usuarioDocument in usuariosResult) {
                    val publicacionesRef = usuarioDocument.reference.collection("publicaciones")
                    publicacionesRef.get()
                        .addOnSuccessListener { publicacionesResult ->
                            for (publicacionDocument in publicacionesResult) {
                                val imagen = publicacionDocument.getString("imagen")
                                val contenido = publicacionDocument.getString("contenido")
                                val comentario = publicacionDocument.getString("comentarios")
                                val publicaUsuario = publicacionDocument.getString("publicaUsuario")

                                val publicacion = Publicacion(imagen!!, contenido!!, comentario!!, publicaUsuario!!)
                                publicacionesList.add(publicacion)
                            }

                            onPublicacionesLoaded(publicacionesList)
                        }
                        .addOnFailureListener { exception ->
                            // Manejar la falla en la obtención de publicaciones
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar la falla en la obtención de usuarios
            }

    }


    fun getEmail(): String? {
        val intentFromSingin = intent
        val extras = intentFromSingin.extras
        return extras!!.getString("email")
    }



}





