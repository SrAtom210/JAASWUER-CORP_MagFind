package com.example.magfind.pnavigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magfind.ui.theme.ThemeViewModel
import com.example.magfind.views.*

@Composable
fun NavManager(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login") {

        composable("Login") {
            LoginView(navController,themeViewModel)
        }
        composable("Home") {
            flHomeView(navController,themeViewModel)
        }
        //  Aquí pasamos el viewModel del tema para controlar el modo oscuro global
        composable("Ajustes") {
            fAjustesView(navController, themeViewModel)
        }
        composable("Categorias") {
            CategoriasView(navController,themeViewModel)
        }
        composable("CorreosCat") {
            fCorreosCategorizadosView(navController,themeViewModel)
        }
        composable("MiCuenta") {
            fCuentaView(navController,themeViewModel)
        }
        composable("Suscripcion") {
            fSuscripcionView(navController,themeViewModel)
        }
    }
}
