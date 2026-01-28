package com.example.willgo.view.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.willgo.data.Category
import com.example.willgo.data.Event
import com.example.willgo.graphs.FiltersNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FiltersTagView(
    selectedCategory: Category?,
    selectedPrice: String,
    selectedType: String,
    selectedDate: String,
    onRemoveCategory: () -> Unit,
    onRemovePrice: () -> Unit,
    onRemoveType: () -> Unit,
    onRemoveDate: () -> Unit,
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Muestra el valor de cada filtro en un botón separado con opción de eliminación
        if (selectedCategory != null) {
            item {
                FilterAddedCard(filter = selectedCategory.name.replace("_"," "), onRemove = onRemoveCategory)
            }
        }
        if (selectedPrice.isNotEmpty() && selectedPrice != "Todos") {
            item {
                FilterAddedCard(filter = selectedPrice, onRemove = onRemovePrice)
            }
        }
        if (selectedType.isNotEmpty() && selectedType != "Todos") {
            item {
                FilterAddedCard(filter = selectedType, onRemove = onRemoveType)
            }
        }
        if (selectedDate.isNotEmpty() && selectedDate != "Todos") {
            item {
                FilterAddedCard(filter = selectedDate, onRemove = onRemoveDate)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersTagViewSearchScreen(
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    events: List<Event>,
    navControllerMain: NavController,
    selectedCategory: Category?,
    selectedPrice: String,
    selectedType: String,
    selectedDate: String,
    onRemoveCategory: () -> Unit,
    onRemovePrice: () -> Unit,
    onRemoveType: () -> Unit,
    onRemoveDate: () -> Unit
){
    val keyboard = LocalSoftwareKeyboardController.current
    if (sheetState.isVisible) {
        MyModalBottomSheet(
            onDismiss = { coroutineScope.launch { sheetState.hide() } },
            sheetState = sheetState,
            events = events,
            navControllerMain = navControllerMain
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterButton(
            onClick = {
                keyboard?.hide()
                coroutineScope.launch { sheetState.expand() }
            },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )

        FiltersTagView(
            selectedCategory = selectedCategory,
            selectedPrice = selectedPrice,
            selectedType = selectedType,
            selectedDate = selectedDate,
            onRemoveCategory = onRemoveCategory,
            onRemovePrice = onRemovePrice,
            onRemoveType = onRemoveType,
            onRemoveDate = onRemoveDate
        )
    }
}

@Composable
fun FilterButton(onClick: () -> Unit, modifier: Modifier){
    Button(onClick = onClick, modifier = modifier) {
        Text("Filtros")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyModalBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    events: List<Event>,
    navControllerMain: NavController
) {
    val navHostController = rememberNavController()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(bottom = 0.dp),
    ) {
        FilterPanel(navHostController = navHostController, events = events, paddingValues = PaddingValues(0.dp), navControllerMain)
    }
}

@Composable
fun FilterAddedCard(filter: String, onRemove: () -> Unit){
    ElevatedButton(
        onClick = onRemove,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Gray)
    ) {
        Text(filter)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.clickable { onRemove() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(navHostController: NavHostController, events: List<Event>,paddingValues: PaddingValues, navControllerMain: NavController){
        FiltersNavGraph(navHostController, events,paddingValues, navControllerMain)
}

@Composable
fun FilterRow(modifier: Modifier, filterName: String, value: String){
    Box(modifier = modifier
        .drawBehind {
            drawLine(
                color = Color.Gray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    ){
        Text(text = filterName, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp))
        Text(text = value, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp))
    }
}

@Composable
fun FilterValueRow(modifier: Modifier, value: String, onClick: () -> Unit){
    Box(modifier = modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .drawBehind {
            drawLine(
                color = Color.Gray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
        .padding(8.dp)
    ){
        Text(text = value, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp))
    }
}