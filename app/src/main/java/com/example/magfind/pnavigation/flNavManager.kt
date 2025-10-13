package com.example.magfind.pnavigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magfind.views.CategoriasView
import com.example.magfind.views.LoginView
import com.example.magfind.views.flHomeView

@Composable
fun NavManager(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login")
    {
        composable ("Home"){
            flHomeView(navController)
        }
        composable ("Login"){
            LoginView(navController)
        }
        composable ("Categorias"){
            CategoriasView(navController)
        }
    }
}