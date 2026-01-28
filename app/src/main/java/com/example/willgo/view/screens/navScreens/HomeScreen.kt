package com.example.willgo.view.screens.navScreens

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.willgo.data.Category
import com.example.willgo.data.CategorySectionData
import com.example.willgo.data.Event
import com.example.willgo.view.screens.normalizeText
import com.example.willgo.view.sections.FiltersPreview

@Composable
fun HomeScreen(paddingValues: PaddingValues, events: List<Event>, navController: NavHostController, name: String){
    //var filteredEvents by remember { mutableStateOf(events) }
    var query by remember { mutableStateOf("") }

    // Duplicamos las categorías para simular un carrusel circular
    val circularCategories = CategorySectionData.categories

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })

    // Número real de categorías
    val realPageCount = circularCategories.size

    // Ajustar automáticamente el índice del pager si se acerca a los límites
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage <= realPageCount || pagerState.currentPage >= Int.MAX_VALUE - realPageCount) {
            pagerState.scrollToPage(Int.MAX_VALUE / 2 + (pagerState.currentPage % realPageCount))
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding())
        .background(Color.White)){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TopBar(
                navController ={
                    Image(
                        imageVector =  Icons.Default.CalendarMonth, contentDescription = "Calendario",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                navController.navigate("calendar")
                            }
                    )
                }
            )
            SearchBar(
                text = query,
                events = events,
                onQueryChange = { newQuery ->
                    query = normalizeText(newQuery)
                },
                onSearch = {
                    navController.navigate("searchResults/${normalizeText(query)}")
                },
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Texto principal
                Text(
                    text = "Hola ${name},",
                    style = TextStyle(color = Color.Black, fontSize = 32.sp),
                    modifier = Modifier
                        .padding(start = 24.dp, end = 16.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = "¿qué toca hoy?",
                    style = TextStyle(color = Color.Black, fontSize = 32.sp),
                    modifier = Modifier
                        .padding(start = 24.dp, end = 16.dp, bottom = 32.dp)
                        .fillMaxWidth()
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 36.dp), // Ajuste del padding para mostrar páginas adyacentes
                    //pageSpacing = 16.dp, // Espacio entre las páginas
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    val adjustedPage = page % realPageCount
                    val validIndex = if (adjustedPage >= 0) adjustedPage else adjustedPage + realPageCount

                    CategoryCard(category = circularCategories[validIndex], navigationTo = {navController.navigate("Category_Section/${circularCategories[validIndex].category.name}")})
            }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    circularCategories.forEachIndexed { index, _ ->
                        IndicatorDot(isSelected = index == pagerState.currentPage % circularCategories.size)                    }
                }

                // Simulación de espacio extra abajo
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(isSelected: Boolean) {
    val color = if (isSelected) Color.Black else Color.Gray
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun TopBar(navigationIcon: @Composable () -> Unit = {}, navController: @Composable () -> Unit = {}){
    Box(
        modifier = Modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.TopStart)){
            navigationIcon()
        }
        Text(
            text = "WILLGO",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .align(Alignment.TopEnd)
        ) {
            navController()
        }
    }
}

@Composable
fun CategoryCard(category: CategorySectionData, navigationTo: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(284.dp)
        )
        {
            Box(
                modifier = Modifier
                .fillMaxSize()
            ){
                Image(
                    painter = painterResource(id = category.imageId),
                    contentDescription = "Card de ${category.title}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { navigationTo() }
                    ,
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                ) {
                    IconButton(
                        onClick = {navigationTo()},
                        modifier = Modifier
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            tint = Color.Black,
                            contentDescription = ">"
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = category.title,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(text:String, events: List<Event>, onQueryChange: (String) -> Unit, onSearch: () -> Unit, navController: NavController){
    var query by remember { mutableStateOf(text) }
    var active by remember { mutableStateOf(false) }

    Log.d("Search", "Función de búsqueda inicializada")

    val searchBarPadding by animateDpAsState(targetValue = if (active) 0.dp else 16.dp, label = "")

    SearchBar(
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            onQueryChange(normalizeText(newQuery))
            Log.d("SearchBar", "Texto cambiado: $text")
        },
        onSearch = {
            active = false
            onSearch() // Navegar a la pantalla de resultados
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = {
            Text("Buscar evento")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
        },
        trailingIcon = {
            if (active && text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close, contentDescription = "Close icon",
                    modifier = Modifier.clickable {
                        query = ""
                        onQueryChange("")
                    }
                )
            }
        },
        modifier = Modifier.padding(horizontal = searchBarPadding),
        windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
    ) {
        FiltersPreview(navController, null, events)
    }
}

@Composable
fun SectionTitle(title: Category, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 12.dp)

    ) {
        Text(
            text = title.name.replace("_", " "),
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
                imageVector =  Icons.Default.ChevronRight, contentDescription = null
            )
        }
    }
}



fun getEventsByCategory(events: List<Event>, category: Category): List<Event> {
    return events.filter { it.category == category }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen(PaddingValues(0.dp), listOf(), NavHostController(LocalContext.current), "Carlitos")
}