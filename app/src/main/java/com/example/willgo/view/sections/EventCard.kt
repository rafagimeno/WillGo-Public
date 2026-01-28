package com.example.willgo.view.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.willgo.data.Event
import coil.compose.AsyncImage

@Composable
fun CommonEventCard(event: Event, modifier: Modifier){
    Card(
        modifier = modifier
            .background(Color.Transparent)
            .height(242.dp)
            .width(284.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(3/2f)
                .background(Color.Transparent)
        ){
            // Usa Coil para cargar la imagen desde la URL
            AsyncImage(
                model = event.image, // Cargar la imagen desde la URL de tu base de datos
                contentDescription = "Imagen del evento",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color.White)
        ){
            Text(
                text = event.date?:"",
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(start = 8.dp)
            )

            Text(
                text = event.name_event,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(72.dp)
                    .align(alignment = Alignment.BottomEnd)
                    .padding(4.dp))
            {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Fav",
                    modifier = Modifier.size(32.dp)
                        .align(alignment = Alignment.CenterEnd)
                        .padding(start = 4.dp),
                    tint = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Black
                )
            }


        }

    }
}

