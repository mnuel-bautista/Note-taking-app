package com.mnuel.dev.notes.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerValue
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnuel.dev.notes.ui.navigation.NotesNavHost
import com.mnuel.dev.notes.ui.components.DrawerContent
import com.mnuel.dev.notes.ui.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun NotesApp() {

    val uiController = rememberSystemUiController()

    val appViewModel = hiltViewModel<NotesAppViewModel>()

    val sections by appViewModel.drawerSections.collectAsState()

    val navController = rememberNavController()

    val scope = rememberCoroutineScope()

    val currentBackstackEntry = navController.currentBackStackEntryAsState().value

    val selectedSection = appViewModel.getDrawerSectionFromBackstackEntry(currentBackstackEntry)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        uiController.setStatusBarColor(
            color = Color(0xFF3700B3),
            darkIcons = false
        )

        // setStatusBarsColor() and setNavigationBarsColor() also exist
    }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            CompositionLocalProvider(LocalContentAlpha provides .54f) {
                DrawerContent(
                    title = "Notas",
                    selected = selectedSection,
                    sections = sections,
                    onNavigation = {
                        scope.launch {
                            navController.navigate(it.route) {
                                popUpTo(route = Routes.HOME) { inclusive = false }
                            }
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        NotesNavHost(
            navController = navController,
            onNavigationIconClick = { scope.launch { drawerState.open() } },
            modifier = Modifier.fillMaxSize()
        )
    }
}