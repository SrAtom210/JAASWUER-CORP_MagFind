package com.example.magfind.pnavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

        composable(
            route = "VerifyCode/{email}?isReset={isReset}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("isReset") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val isReset = backStackEntry.arguments?.getBoolean("isReset") ?: false
            VerifyCodeView(navController, themeViewModel, email, isReset)
        }

        /**
         * Ruta para la pantalla final de reseteo de contraseña.
         * Recibe email y código como argumentos.
         */
        composable(
            route = "SubmitNewPassword/{email}/{code}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val code = backStackEntry.arguments?.getString("code") ?: ""
            SubmitNewPasswordView(navController, themeViewModel, email, code)
        }
    }
}
