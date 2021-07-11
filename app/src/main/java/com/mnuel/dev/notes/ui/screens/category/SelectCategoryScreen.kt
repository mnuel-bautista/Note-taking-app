package com.mnuel.dev.notes.ui.screens.category

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun SelectCategoryScreen(
    selectedCategoryId: Int,
    navController: NavController,
    onNavigateUp: () -> Unit = {},
) {
    val viewModel = hiltViewModel<SelectCategoryViewModel>()

    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories by viewModel.categories.collectAsState()

    DisposableEffect(key1 = selectedCategoryId) {
        viewModel.selectCategory(selectedCategoryId)
        onDispose {  }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "")
                    }
                },
                title = {
                    Text(text = "Categories")
                },
                actions = {}
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(categories, key = { "$it:${it.id == selectedCategory?.id}"}) {
                CategoryElement(
                    selected = it.id == selectedCategory?.id,
                    description = it.description,
                    onSelect = {
                        viewModel.selectCategory(it.id)
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "Selected Category",
                            it.id
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun CategoryElement(
    selected: Boolean = false,
    description: String,
    onSelect: () -> Unit,
) {

    val color by animateColorAsState(if(selected) Color.Green.copy(alpha = 0.12f) else Color.Transparent)

    Box(
        Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(color)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = onSelect
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = description
        )
    }
}

@Preview
@Composable
fun CategoryElementPreview() {
    CategoryElement(description = "School", onSelect = {})
}