package com.example.willgo.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.willgo.data.User.User
import com.example.willgo.graphs.BottomBarScreen
import io.github.jan.supabase.postgrest.postgrest

private var nick =""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowerScreen(navController: NavHostController, nickname: String, paddingValues: PaddingValues, onBack: () -> Unit){
    val following = remember { mutableStateOf(listOf<User>()) }

    nick = nickname

    LaunchedEffect(Unit) {
        following.value = getFollowersForUser(nickname)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        TopAppBar(
            title = { Text("Seguidores") },
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
            items(following.value) { follow ->
                FollowingItem(
                    follow
                )
            }
        }
    }

}

@Composable
fun FollowingItem(user: User){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ){
        Row(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "\"${user.nickname}\"",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(40.dp))

        }
    }
}

suspend fun getFollowersForUser(nickname: String): List<User> {
    val client = getClient()
    val supabaseResponse = client.postgrest["seguidores"].select{
        filter { eq("following", nickname)}
    }
    val followingUsers = supabaseResponse.decodeList<SeguidosResponse>()

    val result:MutableList<User> = mutableListOf()

    for (user in followingUsers) {
        val usersupabaseResponse = client.postgrest["Usuario"].select{
            filter { eq("nickname", user.follower)}
        }
        val followingUser = usersupabaseResponse.decodeList<User>()
        result += followingUser
    }

    return result
}

@kotlinx.serialization.Serializable
data class SeguidosResponse(
    val following: String,
    val follower: String
)