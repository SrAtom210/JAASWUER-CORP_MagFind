package com.example.magfind1.pnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.magfind1.views.CategoriasView
import com.example.magfind1.views.VerifyCodeView
import com.example.magfind1.SessionManager
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.views.*
@Composable
fun NavManager(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    //  Cargar SessionManager
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Detectar si hay sesión guardada
    val startRoute = if (sessionManager.isLoggedIn()) "Home" else "Login"

    NavHost(
        navController = navController,
        startDestination = startRoute   // ESTA ES LA SOLUCIÓN
    ) {

        composable("Login") {
            LoginView(navController, themeViewModel)
        }

        composable("Home") {
            flHomeView(navController, themeViewModel)
        }

        composable("Ajustes") {
            fAjustesView(navController, themeViewModel)
        }

        composable("Categorias") {
            CategoriasView(navController, themeViewModel)
        }

        composable("CorreosCat") {
            fCorreosCategorizadosView(navController, themeViewModel)
        }

        composable("MiCuenta") {
            fCuentaView(navController, themeViewModel)
        }

        composable("Suscripcion") {
            fSuscripcionView(navController, themeViewModel)
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

        composable(
            route = "StripeSetup/{clientSecret}/{customerId}/{plan}",
            arguments = listOf(
                navArgument("clientSecret") { type = NavType.StringType },
                navArgument("customerId") { type = NavType.StringType },
                navArgument("plan") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val secret = backStackEntry.arguments?.getString("clientSecret")!!
            val customerId = backStackEntry.arguments?.getString("customerId")!!
            val plan = backStackEntry.arguments?.getString("plan")!!

            StripeSetupView(
                navController = navController,
                clientSecret = secret,
                customerId = customerId,
                plan = plan
            )
        }


    }
}
