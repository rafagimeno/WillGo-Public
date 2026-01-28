package com.example.willgo.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.willgo.data.Event
import com.example.willgo.graphs.BottomBarScreen
import com.example.willgo.graphs.MainNavGraph
import com.example.willgo.data.User.User
import com.example.willgo.view.screens.navScreens.HomeScreen
import com.example.willgo.view.screens.navScreens.getWillgoForUser
import com.example.willgo.view.screens.other.SplashScreen

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()){

    val user = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        user.value = getUser()
    }

    if (user.value == null) {
        SplashScreen(navController)
    } else {
        Scaffold(
            bottomBar = {
                NavBar(navController)
            }
        )
        {
            MainNavGraph(navController = navController, paddingValues = PaddingValues(top = it.calculateTopPadding(), bottom = BottomAppBarDefaults.windowInsets.asPaddingValues().calculateBottomPadding()), user = user.value!!)
        }
    }
}

@Composable
fun RowScope.AddItems(screen: BottomBarScreen, currentDestination: NavDestination?, navController: NavController) {
    val isSearchResults = currentDestination?.route?.contains("searchResults") == true
    val isCategory = currentDestination?.route?.contains("Category_Section/{categoryName}") == true
    val isSelected = if (isSearchResults || isCategory) {
        screen == BottomBarScreen.Home // Seleccionar Home cuando estamos en searchResults
    } else {
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    NavigationBarItem(

        selected =  isSelected,
        onClick = {
            navController.navigate(screen.route){
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        },
        icon = { Icon(
            imageVector = screen.icon,
            contentDescription = "image",
            tint = if (isSelected) Color.Gray else Color.White,
            modifier = Modifier.size(36.dp)

        ) },
        modifier = Modifier.wrapContentSize().clip(CircleShape),
    )
}

@Composable
fun NavBar(navController: NavController){
    val screens = listOf(
        BottomBarScreen.Location,
        BottomBarScreen.Home,
        BottomBarScreen.Profile)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = screens.any{it.route == currentDestination?.route} ||
            currentDestination?.route?.contains("searchResults" ) == true
            ||
            currentDestination?.route?.contains("Category_Section/{categoryName}") == true
    if(bottomBarDestination) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .background(Color.Transparent)
                .padding(PaddingValues(bottom = BottomAppBarDefaults.windowInsets.asPaddingValues().calculateBottomPadding() + 16.dp))
                .height(54.dp)
        ){
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(CircleShape),
                contentAlignment = Alignment.Center

            ){
                Row(
                    modifier = Modifier
                        .background(Color.Gray)
                        .clip(CircleShape)
                        .width(280.dp)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    screens.forEach { screen ->
                        AddItems(
                            screen = screen,
                            currentDestination = currentDestination,
                            navController = navController
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
