package com.mnuel.dev.notes.ui.screens.notebooks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OrderState {

    val orderProperty by mutableStateOf(OrderProperty.CreationDate)

    val orderBy by mutableStateOf(OrderBy.Descending)

    var dialogContent by mutableStateOf(DialogContent.SelectedOptions)

    val visible by mutableStateOf(false)
}

enum class DialogContent {
    /**
     * Dialog that displays the properties used for sorting
     * */
    OrderingProperties,
    OrderBy,

    /**
     * Dialog that displays the selected options
     * */
    SelectedOptions,
}