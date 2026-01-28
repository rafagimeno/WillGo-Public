package com.example.willgo.view.screens.other

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ElectricCar
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.willgo.data.Request
import com.example.willgo.data.StateResponse
import com.example.willgo.data.User.User
import com.example.willgo.data.WillGo.WillGo
import com.example.willgo.data.WillGo.WillGoItem
import com.example.willgo.view.screens.getClient
import com.example.willgo.view.screens.getUser
import com.example.willgo.view.screens.normalizeText
import com.example.willgo.view.sections.WillGo.WillGoRequestedUserItem
import com.example.willgo.view.sections.WillGo.WillGoUserItem
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillGoScreen(
    idEvent: Long,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    navHostController: NavHostController,
    goCar: () -> Unit
) {
    // Listas mutables
    val user = remember { mutableStateListOf<WillGoItem>() }
    val requestedUsers = remember { mutableStateListOf<WillGoItem>() }

    // Listas filtradas para búsqueda
    val filteredUsers = remember { mutableStateListOf<WillGoItem>() }
    val filteredRequestedUsers = remember { mutableStateListOf<WillGoItem>() }

    val selectedUsers = remember { mutableStateListOf<WillGoItem>() }
    val selectedRequestedUsers = remember { mutableStateListOf<WillGoItem>() }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Usuarios", "Ya solicitados")

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchBarPadding by animateDpAsState(targetValue = if (active) 0.dp else 16.dp)
    // Función para filtrar usuarios
    fun filterUsers(query: String) {
        filteredUsers.clear()
        filteredRequestedUsers.clear()

        if (query.isNotEmpty()) {
            filteredUsers.addAll(user.filter { it.willGo.user.contains(query, ignoreCase = true) })
            filteredRequestedUsers.addAll(requestedUsers.filter { it.willGo.user.contains(query, ignoreCase = true) })
        } else {
            filteredUsers.addAll(user)
            filteredRequestedUsers.addAll(requestedUsers)
        }
    }

    // Obtener datos iniciales
    LaunchedEffect(Unit) {
        val result = getUsersNotRequested(idEvent)
        user.addAll(result.value)
        requestedUsers.addAll(getAloneUsersRequested(idEvent).value)

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
        bottomBar = {
            Box(modifier = Modifier.padding(8.dp)) {
                val selectedList = if (selectedTab == 0) selectedUsers else selectedRequestedUsers
                Button(
                    onClick = {
                        if (selectedTab == 0) {
                            sendWillGoRequests(selectedList) {
                                requestedUsers.addAll(selectedUsers)
                                requestedUsers.map { it.isSelected = false }
                                user.removeAll(selectedUsers)
                                selectedUsers.clear()
                                filterUsers(query)
                            }
                        } else {
                            cancelWillGoRequests(selectedList) {
                                user.addAll(selectedRequestedUsers)
                                user.map { it.isSelected = false }
                                requestedUsers.removeAll(selectedRequestedUsers)
                                selectedRequestedUsers.clear()
                                filterUsers(query)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    enabled = selectedList.isNotEmpty()
                ) {
                    Text(text = if (selectedTab == 0) "Enviar solicitud" else "Cancelar solicitud")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { goCar() }) {
                Icon(imageVector = Icons.Rounded.DirectionsCar, contentDescription = null)
            }
        }
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
                            if (selectedTab == 1) {
                                clearSelectedUsers(selectedUsers, user)
                            } else {
                                clearSelectedUsers(selectedRequestedUsers, requestedUsers)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Contenido de acuerdo a la pestaña seleccionada
            when (selectedTab) {
                0 -> RecienteContent(filteredUsers, selectedUsers, navHostController)
                1 -> YaSolicitados(filteredRequestedUsers, selectedRequestedUsers)
            }
        }
    }
}


// Función para mostrar la lista de usuarios recientes
@Composable
fun RecienteContent(user: SnapshotStateList<WillGoItem>, selectedUsers: SnapshotStateList<WillGoItem>, navHostController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(user) { item ->
            WillGoUserItem(
                nickname = item.willGo.user,
                followers = item.followers!!,
                onToggleSelect = {
                    item.isSelected = !(item.isSelected)!!
                    if (item.isSelected == true) {
                        selectedUsers.add(item)
                        println(selectedUsers)
                    } else {
                        selectedUsers.remove(item)
                    }
                    item.isSelected!!
                },
                modifier = Modifier,
                onClick = { navHostController.navigate("profile/${item.willGo.user}") }
            )
        }
    }
}

// Función para mostrar la lista de usuarios ya solicitados
@Composable
fun YaSolicitados(
    requestedUsers: SnapshotStateList<WillGoItem>,
    selectedRequestedUsers: SnapshotStateList<WillGoItem>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(requestedUsers) { item ->
            val state = remember { mutableStateOf<String?>(null) }
            LaunchedEffect(item) {
                state.value = getStateRequest(item.willGo.id!!, getUser().nickname, item.willGo.user)
            }
            if (state.value != null) {
                WillGoRequestedUserItem(
                    nickname = item.willGo.user,
                    followers = item.followers!!,
                    state = state.value!!,
                    onToggleSelect = {
                        item.isSelected = !(item.isSelected)!!
                        if (item.isSelected == true) {
                            selectedRequestedUsers.add(item)
                            println(selectedRequestedUsers)
                        } else {
                            selectedRequestedUsers.remove(item)
                        }
                        item.isSelected!!
                    },
                    modifier = Modifier,
                    onClick = {
                        cancelWillGoRequests(listOf(item)) {}
                        requestedUsers.remove(item)
                        selectedRequestedUsers.remove(item)
                    }
                )
            }
        }
    }
}





suspend fun getStateRequest(idEvent: Long, userRequesting: String, nickRequested: String): String {
    val response = getClient()
        .postgrest["Solicitudes"]
        .select(Columns.list("state")){
            filter {
                and {
                    eq("userRequested", idEvent)
                    eq("userRequesting", userRequesting)
                    eq("nickRequested", nickRequested)
                }
            }
        }

    val responseBody = response.decodeList<StateResponse>()

    // Devuelve el estado del primer elemento, o un valor por defecto si la lista está vacía
    return responseBody.firstOrNull()?.state ?: "Desconocido"
}
fun clearSelectedUsers(selectedUsers: SnapshotStateList<WillGoItem>, users: SnapshotStateList<WillGoItem>) {
    users.map { it.isSelected = false }
    selectedUsers.clear()
}


@Preview
@Composable
fun WillGoScreenPreview() {
    WillGoScreen(3, PaddingValues(0.dp), {}, navHostController = NavHostController(LocalContext.current), goCar = {})
}


fun cancelWillGoRequests(selectedUsers: List<WillGoItem>, onSuccess: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val userRequesting = getUser().nickname // El usuario que envía las solicitudes

            selectedUsers.forEach { selectedUser ->
                getClient().postgrest["Solicitudes"].delete {
                    filter {
                        eq("userRequesting", userRequesting) // Coincide con el usuario solicitante
                        eq("userRequested", selectedUser.willGo.id!!) // Coincide con el ID solicitado
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onSuccess() // Notificar éxito
            }
        } catch (e: Exception) {
            Log.e("sendWillGoRequests", "Error al enviar solicitudes: ${e.message}")
        }
    }
}

fun sendWillGoRequests(selectedUsers: List<WillGoItem>, onSuccess: () -> Unit){
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val userRequesting = getUser().nickname // El usuario que envía las solicitudes

            // Crear solicitudes para cada usuario seleccionado
            val requests = selectedUsers.map { selectedUser ->
                Request(
                    id = null,
                    userRequesting = userRequesting,
                    userRequested = selectedUser.willGo.id!!, // Usar el ID de la tabla WillGo
                    state = "Pendiente",
                    nickRequested = selectedUser.willGo.user,
                    id_Event = selectedUser.willGo.id_event
                )
            }
            // Insertar las solicitudes en la tabla "Solicitudes"
            getClient().postgrest["Solicitudes"].insert(requests)

            withContext(Dispatchers.Main) {
                onSuccess() // Notificar éxito
            }
        } catch (e: Exception) {
            Log.e("sendWillGoRequests", "Error al enviar solicitudes: ${e.message}")
        }
    }
}

   suspend fun getAloneUsers(idEvent: Long): MutableState<List<WillGo>> {
       val user = getUser()
       val response = getClient()
           .postgrest["WillGo"]
           .select {
               filter {
                   and {
                       eq("id_event", idEvent)
                       neq("user", user.nickname)
                       eq("alone", true)
                   }
               }
           }
       return mutableStateOf(response.decodeList<WillGo>())
   }

suspend fun getUsersNotRequested(idEvent: Long): MutableState<List<WillGoItem>> {
    val aloneUsersID = getAloneUsers(idEvent)
    val aloneUsersRequested = mutableListOf<WillGoItem>()  // Lista que usaremos para cargar los datos
    val aloneUsersNotRequested = mutableListOf<WillGoItem>()

    withContext(Dispatchers.IO) {
        aloneUsersID.value.forEach {
            val request = getRequest(it.id!!, getUser().nickname,it.user).value
            val user = getUser(it.user)
            val willGoItem = WillGoItem(WillGo(it.id, idEvent, user.nickname, true), user.name, user.followed)
            if (request != null) {
                // Si el usuario ya está en "solicitados"
                aloneUsersRequested.add(
                    willGoItem
                )
            } else {
                // Si el usuario NO está solicitado
                aloneUsersNotRequested.add(
                   willGoItem
                )
            }
        }
    }
    Log.e("getUsersNotRequested", aloneUsersNotRequested.toString())
    return mutableStateOf(aloneUsersNotRequested)
}

suspend fun getAloneUsersItem(idEvent: Long): MutableState<List<WillGoItem>> {
    val aloneUsersID = getAloneUsers(idEvent)
    val aloneUsers = mutableListOf<WillGoItem>()
    withContext(Dispatchers.IO) {
        aloneUsersID.value.map {
            val user = getUser(it.user)
            aloneUsers.add(
                WillGoItem(
                    it,
                    name = user.name,
                    followers = user.followers,
                )
            )
        }
    }
    return mutableStateOf(aloneUsers)
}



suspend fun getAloneUsersRequested(idEvent: Long): MutableState<List<WillGoItem>> { // MODIFICADO
    val aloneUsersID = getAloneUsers(idEvent)
    val aloneUsersRequested = mutableListOf<WillGoItem>()  // Lista que usaremos para cargar los datos

    withContext(Dispatchers.IO) {
        aloneUsersID.value.forEach {
            val request = getRequest(it.id!!, getUser().nickname,it.user).value
            val user = getUser(it.user)
            val willGoItem = WillGoItem(WillGo(it.id, idEvent, user.nickname, true), user.name, user.followed)
            if (request != null) {
                // Si el usuario ya está en "solicitados"
                aloneUsersRequested.add(
                    willGoItem
                )
            }
        }
    }
    Log.e("getUsersRequested", aloneUsersRequested.toString())
    return mutableStateOf(aloneUsersRequested)
}

suspend fun getUserFromWillGoID(idWillGo: Long): User {
    val user = getUser()
    val response = getClient()
        .postgrest["WillGo"]
        .select(Columns.list("user")) {
            filter {
                and {
                    eq("id_event", idWillGo)
                }
            }
        }
    val userRequested = getUser(response.decodeSingle<String>())
    return userRequested
}

suspend fun getRequest(idEvent: Long, userRequesting: String, nickRequested: String): MutableState<Request?> { // MODIFICADO
    val response = getClient()
        .postgrest["Solicitudes"]
        .select {
            filter {
                and {
                    eq("userRequested", idEvent);
                    eq("userRequesting", userRequesting)
                    eq("nickRequested", nickRequested)
                }
            }
        }
    val request = response.decodeSingleOrNull<Request>() // Devuelve null si no hay resultados
    return mutableStateOf(request)
}