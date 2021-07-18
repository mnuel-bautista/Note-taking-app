package com.mnuel.dev.notes.ui.screens.category

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*

class BottomDialogState @OptIn(ExperimentalMaterialApi::class) constructor(
    val bottomSheetState: ModalBottomSheetState
) {

    var text by mutableStateOf("")

    var collectionId by mutableStateOf(NEW_COLLECTION)

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun show(defaultText: String = "") {
        bottomSheetState.show()
        text = defaultText
    }

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun hide() {
        bottomSheetState.hide()
    }

    companion object {
        /**
         * When the value of [collectionId] is [NEW_COLLECTION], then it means that the bottom
         * dialog is being used for creating a new collection, rather than editing an existing collection.
         * */
        const val NEW_COLLECTION = -1
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberBottomDialogState(
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
): BottomDialogState {
    return remember { BottomDialogState(bottomSheetState) }
}