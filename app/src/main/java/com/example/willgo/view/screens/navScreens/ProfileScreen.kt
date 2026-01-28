package com.example.willgo.view.screens.navScreens

import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.willgo.data.Event
import com.example.willgo.data.User.User
import com.example.willgo.graphs.BottomBarScreen
import com.example.willgo.view.screens.getClient
import com.example.willgo.view.screens.getCommentsForUser
import com.example.willgo.view.screens.getFollowersForUser
import com.example.willgo.view.screens.getFollowingForUser
import com.example.willgo.view.sections.CommonEventCard
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//@Preview
@Composable
fun ProfileScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    user: User,
    showBackArrow: Boolean
){
    val willGoevents = remember { mutableStateOf(listOf<Event>()) }
    val favevents = remember { mutableStateOf(listOf<Event>()) }
    val totalfollowers = remember { mutableStateOf(0)}
    val totalfollowing = remember  { mutableStateOf(0)}
    LaunchedEffect(Unit) {
        willGoevents.value = getWillgoForUser(user.nickname)
        favevents.value = getFavForUser(user.nickname)
        totalfollowers.value = getTotalfollowers(user.nickname)
        totalfollowing.value = getTotalfollowing(user.nickname)
    }
    var coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(paddingValues)
    )
    {
        TopBar2(showBackArrow = showBackArrow, onBackClick = { navController.popBackStack() }, navController)

        LazyColumn(
            modifier = Modifier

                .fillMaxSize()
                .background(Color.White)
        )
        {
            items(1){

                ProfilePic2()
                DataSection(name = user.name)
                Spacer(Modifier.height(16.dp))
//                ButtonsSection(
//                    onSeguirClick = {
//                        coroutineScope.launch {
//                            val userFollowing = getFollowingForUser(user.nickname)
//                            navController.navigate("following/${user.nickname}")
//                        }
//                    },
//                    onSeguidoresClik = {
//                        coroutineScope.launch {
//                            val userFollowing = getFollowersForUser(user.nickname)
//                            navController.navigate("follower/${user.nickname}")
//                        }
//                    },
//                    onComentariosClick = {
//                        coroutineScope.launch {
//                            val userComments = getCommentsForUser(user.nickname)
//                            navController.navigate("comments/${user.nickname}")
//                        }
//                    }
//                )
                Spacer(modifier = Modifier.height(16.dp))
                FollowsSection(
                    totalfollowers.value,
                    totalfollowing.value,
                    onSeguirClick = {
                        coroutineScope.launch {
                            val userFollowing = getFollowingForUser(user.nickname)
                            navController.navigate("following/${user.nickname}")
                        }
                    },
                    onSeguidoresClik = {
                        coroutineScope.launch {
                            val userFollowing = getFollowersForUser(user.nickname)
                            navController.navigate("follower/${user.nickname}")
                        }
                    })
                Spacer(Modifier.height(16.dp))
                SectionTitle2(title = "Eventos favoritos")
                Spacer(Modifier.height(16.dp))
                ConcertsSection2(favevents,navController)
                Spacer(Modifier.height(16.dp))
                SectionTitle2(title = "Asistirá próximamente")
                Spacer(Modifier.height(16.dp))
                PopularSection2(willGoevents,navController)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DataSection(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        //  modifier = Modifier.padding(horizontal = 12.dp)
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(

            text = name,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("username")
        )


    }
}

@Composable
private fun ButtonsSection(onSeguirClick: () -> Unit, onComentariosClick: () -> Unit, onSeguidoresClik:()->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button (onClick = onSeguidoresClik) {
            Text(text = "Seguidores")
        }
        Button(onClick = onSeguirClick) {
            Text(text = "Seguidos")
        }
        Button(onClick = onComentariosClick) {
            Text(text = "Comentarios")
        }
    }
}

@Composable
private fun FollowsSection(number1: Int, number2: Int, onSeguirClick: () -> Unit, onSeguidoresClik:()->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

/*
        Column(modifier = Modifier
            //  .padding(paddingValues)
            .background(Color.White)
        )
        {
        }
*/
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                onSeguidoresClik()
            }
        ) {
            Text(text = number1.toString())
            Text(text = "Seguidores", modifier = Modifier.testTag("followerText"))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                onSeguirClick()
            }
        ) {
            Text(text = number2.toString())
            Text(text = "Seguidos", modifier = Modifier.testTag("followedText"))
        }

    }
}


@Composable
fun SectionTitle2(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Image(
                imageVector =  Icons.Default.ChevronRight, contentDescription = null, Modifier.size(50.dp)

            )
        }
    }
}

@Composable
fun TopBar2(
    showBackArrow: Boolean,
    onBackClick: () -> Unit = {},
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        if (showBackArrow) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
        }
        Text(
            text = "Perfil",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        LaunchedEffect(Unit) {

        }
        if(navController.currentDestination?.route == BottomBarScreen.Profile.route)
            Button(
                onClick = { navController.navigate("WillGoManager") },
                shape = RoundedCornerShape(50), // Forma completamente redondeada
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), // Ajuste del tamaño interno
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Espacio entre ícono y texto
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp) // Ícono más pequeño
                    )
                    Text(
                        text = "Solicitudes",
                        color = Color.White,
                        fontSize = 14.sp // Tamaño de texto ajustado
                    )
                }
            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePic2(){
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(12.dp)
            .fillMaxWidth()
    ) {


        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .align(Alignment.Center)
                .size(150.dp)

        ) {
            Image(
                imageVector =  Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(150.dp)
            )
        }
    }



}


@Composable
fun PopularSection2(events:MutableState<List<Event>>,navController: NavHostController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{VerticalSeparator2()}
        val willgo = events.value
        for (event in willgo) {
            item { CommonEventCard(
                event = event,
                modifier = Modifier
                    .clickable { navController.navigate("eventDetail/${event.id}")}
                )
            }
        }
        item{VerticalSeparator2()}

    }
}

@Composable
fun ConcertsSection2(events:MutableState<List<Event>>,navController: NavHostController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{VerticalSeparator2()}
        val willgo = events.value
        for (event in willgo) {
            item { CommonEventCard(
                event = event,
                modifier = Modifier
                    .clickable { navController.navigate("eventDetail/${event.id}")}
            )
            }
        }
        item{VerticalSeparator2()}

    }
}

@Composable
fun VerticalSeparator2(){
    Box(modifier = Modifier
        .height(164.dp)
        .width(4.dp))
}

suspend fun getWillgoForUser(nickname:String):List<Event> {
    val client = getClient()
    val supabaseResponseEvents = client.postgrest["WillGo"].select {
        filter { eq("user", nickname) }
    }
    val willGoevents = supabaseResponseEvents.decodeList<EventosResponse>()
    val result:MutableList<Event> = mutableListOf()
    for (event in willGoevents) {
        val eventsupabaseResponse = client.postgrest["Evento"].select{
            filter { eq("id", event.id_event)}
        }
        val goEvent = eventsupabaseResponse.decodeList<Event>()
        result += goEvent
    }

    return result
}

suspend fun getFavForUser(nickname:String):List<Event> {
    val client = getClient()
    val supabaseResponseEvents = client.postgrest["Eventos_favoritos"].select {
        filter { eq("user_nickname", nickname) }
    }
    val favevents = supabaseResponseEvents.decodeList<EventosfavResponse>()
    val result:MutableList<Event> = mutableListOf()
    for (event in favevents) {
        val eventsupabaseResponse = client.postgrest["Evento"].select{
            filter { eq("id", event.event_id)}
        }
        val goEvent = eventsupabaseResponse.decodeList<Event>()
        result += goEvent
    }

    return result
}

suspend fun getTotalfollowers(nickname: String) : Int {
    val client = getClient()
    val supabaseResponseEvents = client.postgrest["seguidores"].select {
        filter { eq("following", nickname) }
    }
    return supabaseResponseEvents.decodeList<Seg>().size
}

suspend fun getTotalfollowing(nickname: String) : Int {
    val client = getClient()
    val supabaseResponseEvents = client.postgrest["seguidores"].select {
        filter { eq("follower", nickname) }
    }
    return supabaseResponseEvents.decodeList<Seg>().size
}

@kotlinx.serialization.Serializable
data class EventosResponse(
    val id_event: Long,
    val user: String
)

@kotlinx.serialization.Serializable
data class EventosfavResponse(
    val event_id: Long,
    val user_nickname: String
)

@kotlinx.serialization.Serializable
data class Seg(
    val following : String,
    val follower : String
)

@Preview
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    val paddingValues = PaddingValues(16.dp)
    val user = User(
        name = "John Doe",
        nickname = "JohnMH",
        password = "123456",
        email = "william.henry.harrison@example-pet-store.com",
        followed = 5,
        followers = 10)
    ProfileScreen(navController, paddingValues, user, true)
}