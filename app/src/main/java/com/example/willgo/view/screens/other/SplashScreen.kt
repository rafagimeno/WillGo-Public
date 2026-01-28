package com.example.willgo.view.screens.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.willgo.R
import com.example.willgo.graphs.Graph
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController){
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Graph.MAIN){
            popUpTo(Graph.MAIN){
                inclusive = true
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.White)){
        Image(painter = painterResource(id = R.drawable.willgo_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
            )
    }
}