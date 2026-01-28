package com.example.willgo.view.screens.other

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.willgo.data.Request
import com.example.willgo.view.screens.getClient
import com.example.willgo.view.screens.getUser
import com.example.willgo.view.screens.normalizeText
import com.example.willgo.view.sections.WillGo.ReceivedRequestsItem
import com.example.willgo.view.sections.WillGo.SentRequestsItem
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillGoManagerScreen(
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    navHostController: NavHostController
) {
    // Listas mutables
    var receivedRequests = remember { mutableStateListOf<Request>() }
    var sentRequests = remember { mutableStateListOf<Request>() }

    // Listas filtradas para búsqueda
    val filteredReceived = remember { mutableStateListOf<Request>() }
    val filteredSent = remember { mutableStateListOf<Request>() }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Usuarios", "Ya solicitados")

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchBarPadding by animateDpAsState(targetValue = if (active) 0.dp else 16.dp)
    // Función para filtrar usuarios
    fun filterUsers(query: String) {
        filteredReceived.clear()
        filteredSent.clear()

        if (query.isNotEmpty()) {
            filteredReceived.addAll(receivedRequests.filter { it.userRequesting.contains(query, ignoreCase = true) })
            filteredSent.addAll(sentRequests.filter { it.nickRequested.contains(query, ignoreCase = true) })
        } else {
            filteredReceived.addAll(receivedRequests)
            filteredSent.addAll(sentRequests)
        }
    }

    // Obtener datos iniciales
    LaunchedEffect(Unit) {
        val logedIn = getUser()
        val result = getReceivedRequests(logedIn.nickname)
        receivedRequests.clear()
        receivedRequests.addAll(result)
        sentRequests.clear()
        sentRequests.addAll(getSentRequests(logedIn.nickname))

        // Aplicar el filtro inicial
        filterUsers(query)
    }


    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        topBar = {
            TopAppBar(
                title = { Text("WillGo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(56.dp)){

                // Barra de búsqueda
                DockedSearchBar(
                    query = query,
                    onQueryChange = { newQuery ->
                        query = normalizeText(newQuery)
                        filterUsers(query) // Filtra cuando cambia el texto
                    },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Buscar usuario") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (active && query.isNotEmpty()) {
                            Icon(imageVector = Icons.Default.Close,
                                contentDescription = "Close icon",
                                modifier = Modifier.clickable { query = "" })
                        }
                    },
                    modifier = Modifier.padding(horizontal = searchBarPadding),
                ){}
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Contenido de acuerdo a la pestaña seleccionada
            when (selectedTab) {
                0 -> Received(filteredReceived, navHostController)
                1 -> Sent(filteredSent)
            }
        }
    }
}


// Función para mostrar la lista de usuarios recientes
@Composable
fun Received(filteredUsers: SnapshotStateList<Request>, navHostController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredUsers) { item ->
            ReceivedRequestsItem(
                idEvent = item.id_Event,
                nickname = item.userRequesting,
                state = item.state,
                onClick = { navHostController.navigate("profile/${item.userRequesting}") },
                accept = {
                    acceptRequest(item.id!!)
                },
                decline = {
                    cancelRequest(item.id!!)
                }
            )
        }
    }
}

// Función para mostrar la lista de usuarios ya solicitados
@Composable
fun Sent(
    filteredUsers: SnapshotStateList<Request>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredUsers) { item ->
            SentRequestsItem(
                idEvent = item.id_Event,
                nickname = item.nickRequested,
                state = item.state,
                onClick = {
                    cancelRequest(item.id!!)
                    filteredUsers.remove(item)
                }
            )
        }
    }
}

fun cancelRequest(id: Long){
    val client = getClient()
    CoroutineScope(Dispatchers.IO).launch {
        client.postgrest["Solicitudes"].update(
            {set("state", "Cancelada")}
        ) {
            filter {
                eq("id", id)
            }
        }
    }
}

fun acceptRequest(id: Long) {
    val client = getClient()
    CoroutineScope(Dispatchers.IO).launch {
        client.postgrest["Solicitudes"].update(
            {set("state", "Aceptada")}
        ){
            filter {
                eq("id", id)
            }
        }
    }
}

suspend fun getReceivedRequests(nickRequested: String):SnapshotStateList<Request>{
    val response = getClient().postgrest["Solicitudes"]
        .select{
            filter {
                eq("nickRequested", nickRequested)
            }
        }
    val requests = response.decodeList<Request>()
    return requests.toMutableStateList()
}

suspend fun getSentRequests(userRequesting: String):SnapshotStateList<Request>{
    val response = getClient().postgrest["Solicitudes"]
        .select{
            filter {
                eq("userRequesting", userRequesting)
            }
        }
    val requests = response.decodeList<Request>()
    return requests.toMutableStateList()
}



@Preview
@Composable
fun WillGoManagerPreview() {
    WillGoManagerScreen( PaddingValues(0.dp), {}, navHostController = NavHostController(LocalContext.current))
}

