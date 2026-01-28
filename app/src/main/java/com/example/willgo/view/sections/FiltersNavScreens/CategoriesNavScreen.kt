package com.example.willgo.view.sections.FiltersNavScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.willgo.data.Category
import com.example.willgo.view.sections.FilterValueRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesNavScreen(
    onBack: () -> Unit,
    modifier: Modifier,
    onCategorySelected: (Category) -> Unit,
    navController: NavController,
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros") },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)}
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text("Categoria")
            Spacer(modifier = Modifier.height(8.dp))
            for (category: Category in Category.entries) {
                FilterValueRow(
                    modifier = modifier,
                    value = category.name.replace("_"," "),
                    onClick = {
                        //onCategorySelected(category)
                        navController.previousBackStackEntry?.savedStateHandle?.set("selectedCategory", category)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}