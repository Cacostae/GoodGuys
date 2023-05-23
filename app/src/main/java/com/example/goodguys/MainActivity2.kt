package com.example.goodguys

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold

import androidx.compose.runtime.Composable
import com.example.goodguys.ui.theme.GoodGuysTheme


class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodGuysTheme {
                HorizontalRecyclerView()
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HorizontalRecyclerView() {

    Scaffold(
        content = { }
    )

}