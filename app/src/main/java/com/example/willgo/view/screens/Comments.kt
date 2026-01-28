package com.example.willgo.view.screens

import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items  // Asegúrate de usar este items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.willgo.data.Event
import com.example.willgo.data.Comment
import com.example.willgo.graphs.BottomBarScreen
import com.example.willgo.graphs.loadEventsFromSupabase
import io.github.jan.supabase.postgrest.postgrest
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsOnEvents(navController: NavController, nickname: String, paddingValues: PaddingValues, onBack: () -> Unit) {
    val comments = remember {
        mutableStateOf(listOf<Comment>())
    }
    val coroutineScope = rememberCoroutineScope()




    LaunchedEffect(nickname) {
        comments.value = getCommentsForUser(nickname)
    }

    val events = remember { mutableStateOf(listOf<Event>()) }

    LaunchedEffect(Unit) {
        loadEventsFromSupabase(events)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        TopAppBar(
            title = { Text("Comentarios") },
            navigationIcon = {
                IconButton(
                    onClick = {
                        //navController.navigate(BottomBarScreen.Home.route)
                        navController.navigate(BottomBarScreen.Profile.route) {
                            // Establece `launchSingleTop` para evitar duplicados
                            launchSingleTop = true
                            // Establece `popUpTo` para limpiar el historial hasta `HomeScreen`
                            popUpTo(BottomBarScreen.Profile.route) { inclusive = true }
                        }
                    })
                {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ArrowBack"
                    )
                }
            }
        )
        LazyColumn(modifier = Modifier) {
            items(comments.value) { comment ->
                val event = events.value.firstOrNull { it.id == comment.event_id }

                event?.let {
                    CommentItem(comment, it, navController)
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, event: Event, navController: NavController){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("eventDetail/${comment.event_id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Mostrar el comentario entre comillas
                Text(
                    text = "\"${comment.comment}\"",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mostrar el nombre del evento
                Text(
                    text = "en ${event.name_event}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Mostrar la fecha del comentario en formato reducido
                comment.created_at?.let {
                    val oldDate =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it)
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(oldDate)
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(oldDate)
                    Text(
                        text = "Publicado el: $date a las $time",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
            // Mostrar la imagen del evento, si existe
            event.image?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

            }
        }
    }
}


suspend fun getCommentsForUser(nickname: String): List<Comment> {
    val client = getClient()
    val supabaseResponse = client.postgrest["comentario"].select{
        filter { eq("user_nickname", nickname)}
    }
    return supabaseResponse.decodeList<Comment>()
}
