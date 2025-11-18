package com.example.magfind1.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fPlantilla(
    title: String,
    navController: NavController,
    themeViewModel: ThemeViewModel,
    drawerItems: List<Pair<String, () -> Unit>> = emptyList(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // 🎨 Colores dinámicos según el tema global
    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val drawerBackground = if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDark) Color.White else Color.Black
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp),
                drawerContainerColor = drawerBackground
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "Menú",
                        style = MaterialTheme.typography.titleLarge.copy(color = accentColor)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    drawerItems.forEach { (itemTitle, onClick) ->
                        fDrawerItem(title = itemTitle, textColor = textColor) {
                            onClick()
                            scope.launch { drawerState.close() }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        TextButton(
                            onClick = {
                                val sessionManager = SessionManager(navController.context)
                                sessionManager.clearSession()

                                navController.navigate("Login") {
                                    popUpTo(0)
                                }
                                scope.launch { drawerState.close() }
                            }
                        ) {
                            Text(
                                text = "Cerrar Sesión",
                                style = MaterialTheme.typography.titleLarge.copy(color = accentColor)
                            )
                        }

                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(title, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = accentColor
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(innerPadding)
            }
        }
    }
}

@Composable
fun fDrawerItem(title: String, textColor: Color, onClick: () -> Unit) {
    Text(
        text = title,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        color = textColor
    )
}
