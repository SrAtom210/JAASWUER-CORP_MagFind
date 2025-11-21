package com.example.magfind1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

// Import del menú estilo Google
import com.example.magfind1.components.GoogleStyleProfileMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fPlantilla(
    title: String,
    navController: NavController,
    themeViewModel: ThemeViewModel,
    showProfileMenu: Boolean = true,
    drawerItems: List<Pair<String, () -> Unit>> = emptyList(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isDark = themeViewModel.isDarkMode.collectAsState().value
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    val drawerBackground = if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDark) Color.White else Color.Black
    val accentColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF1976D2)

    val currentUsername = session.getDisplayName()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = drawerBackground
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accentColor)
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentUsername.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = session.getEmail().toString(),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Título
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Items del Drawer
                drawerItems.forEach { (itemTitle, onClick) ->
                    val icon = when (itemTitle.lowercase()) {
                        "home" -> Icons.Default.Home
                        "categorías" -> Icons.Default.Category
                        "correos" -> Icons.Default.Email
                        "ajustes" -> Icons.Default.Settings
                        "mi cuenta" -> Icons.Default.Person
                        "suscripción" -> Icons.Default.Star
                        else -> Icons.Default.ChevronRight
                    }

                    fDrawerItem(
                        title = itemTitle,
                        textColor = textColor,
                        icon = icon
                    ) {
                        onClick()
                        scope.launch { drawerState.close() }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ad", fontSize = 18.sp, color = Color.DarkGray)
                }
            }

            // Logout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextButton(
                    onClick = {
                        session.clearSession()
                        navController.navigate("Login") { popUpTo(0) }
                        scope.launch { drawerState.close() }
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Color.Red
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión", color = Color.Red)
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
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        if (showProfileMenu) {
                            GoogleStyleProfileMenu(
                                navController = navController,
                                userName = currentUsername.toString(),
                                email = session.getEmail().toString()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = accentColor
                    )
                )
            }
        ) { inner ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(inner)
            }
        }
    }
}

@Composable
fun fDrawerItem(
    title: String,
    textColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = title,
                tint = textColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(title, color = textColor)
    }
}
