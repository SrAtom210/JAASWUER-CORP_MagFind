package com.example.magfind1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.magfind1.SessionManager
import com.example.magfind1.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

// Componentes adicionales
import com.example.magfind1.components.AdMobBanner
import com.example.magfind1.components.GoogleStyleProfileMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fPlantilla(
    title: String,
    navController: NavController,
    themeViewModel: ThemeViewModel,
    showProfileMenu: Boolean = true,
    searchEnabled: Boolean = false,
    onSearchQueryChange: (String) -> Unit = {},
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
    val currentEmail = session.getEmail()
    val currentPlan = session.getPlan()?.replaceFirstChar { it.uppercase() }

    // Estado del buscador
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = drawerBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    // Drawer header...
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(accentColor)
                            .padding(horizontal = 20.dp, vertical = 35.dp)
                    ) {
                        val photo = session.getProfilePhoto()

                        if (!photo.isNullOrEmpty()) {
                            AsyncImage(
                                model = photo,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentUsername.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Text(
                            text = currentEmail.toString(),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )

                        if (!currentPlan.isNullOrEmpty()) {
                            Text(
                                text = "Plan: $currentPlan",
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Menú",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(30.dp))
                    // --- ANUNCIO REAL ---
                    val userPlan = session.getPlan()?.trim()?.lowercase() ?: ""

                    val isPaidUser = userPlan == "admin" || userPlan == "business" || userPlan == "plus" || userPlan == "premium"

                    // 2. Condicional: Solo mostrar si es 'essential'
                    if (!isPaidUser) {
                        Spacer(modifier = Modifier.height(30.dp))

                        // --- ANUNCIO REAL ---
                        AdMobBanner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    } else {
                        // Si paga (Plus/Business), solo dejamos un espacio pequeño estético
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                        Text(
                            "Cerrar Sesión",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) {

        // 🆕 TOPBAR CON BUSCADOR INTEGRADO
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (isSearching && searchEnabled) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    onSearchQueryChange(it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar…", color = Color.White) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White
                                ),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        isSearching = false
                                        onSearchQueryChange("")
                                    }) {
                                        Icon(Icons.Default.Close, tint = Color.White, contentDescription = "Cerrar")
                                    }
                                }
                            )
                        } else {
                            Text(title, color = Color.White)
                        }
                    },
                    navigationIcon = {
                        if (!isSearching) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        if (searchEnabled) {
                            if (!isSearching) {
                                IconButton(onClick = { isSearching = true }) {
                                    Icon(Icons.Default.Search, tint = Color.White, contentDescription = "Buscar")
                                }
                            }
                        }

                        if (showProfileMenu) {
                            GoogleStyleProfileMenu(
                                navController = navController,
                                userName = currentUsername.toString(),
                                email = currentEmail.toString()
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(title, color = textColor, fontSize = 16.sp)
    }
}
