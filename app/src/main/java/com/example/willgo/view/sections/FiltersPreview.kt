package com.example.willgo.view.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.willgo.data.Category
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.willgo.data.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersPreview(navController: NavController, selectedCategory: Category?, events: List<Event>){
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    var selectedCategoryState by remember { mutableStateOf(selectedCategory) } // Estado inicial de categoría seleccionada

    if (bottomSheetState.isVisible) {
        MyModalBottomSheet(
            onDismiss = { coroutineScope.launch { bottomSheetState.hide() } },
            sheetState = bottomSheetState,
            events = events,
            navControllerMain = navController
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        FilterButton(onClick = {
            keyboard?.hide()
            coroutineScope.launch { bottomSheetState.expand() }}, modifier = Modifier.padding(start = 8.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Categorías",
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold)

        FilterGrid(navController, selectedCategoryState) { newCategory ->
            selectedCategoryState = newCategory  // Actualiza la categoría seleccionada
        }
    }
}

@Composable
fun FilterGrid(navController: NavController, selectedCategory: Category?, onCategorySelected: (Category) -> Unit){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(Category.entries.toTypedArray()) { category ->
            CategoryItem(
                category = category,
                isSelected = category == selectedCategory,
                navController = navController,
                onClick = { onCategorySelected(category) }  // Llama a la selección de categoría
            )
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, navController: NavController, onClick: () -> Unit) {

    val buttonColor = if (isSelected) Color.Blue else Color.Gray  // Cambia el color si está seleccionada

    ElevatedButton(
        onClick = {
            onClick()
            navController.navigate("searchResults?query=&category=${category.name}")
        },
        colors = ButtonDefaults.elevatedButtonColors(containerColor = buttonColor)
    ) {
        Text(text = category.name.replace("_"," "))
    }
}

