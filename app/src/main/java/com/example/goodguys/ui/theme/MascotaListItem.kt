package com.example.goodguys.ui.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.goodguys.data.Mascota

@Composable
fun MascotaListItem(mascota: Mascota){
    Row {
        Text(text = mascota.nombre)
        
    }
}