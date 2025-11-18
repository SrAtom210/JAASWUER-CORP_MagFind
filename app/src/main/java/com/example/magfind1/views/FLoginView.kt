package com.example.magfind1.views

// Credential Manager + Google ID
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavHostController
import com.example.magfind1.SessionManager
import com.example.magfind1.apis.AuthRepository
import com.example.magfind1.ui.theme.ThemeViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch
import com.example.magfind1.R
import com.example.magfind1.google.GoogleAuthManager

@Composable
fun LoginView(navController: NavHostController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    val repo = remember { AuthRepository() }
    val session = remember { SessionManager(context) }


    val GOOGLE_CLIENT_ID =
        "76794028126-h85vt3eva11286jjob5leq038mr61q6c.apps.googleusercontent.com"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.magfind),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )

            Text(
                text = "MagFind",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Iniciar Sesión") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Registrarse") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =============== LOGIN ===============
            if (selectedTab == 0) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // --- Login normal ---
                Button(
                    onClick = {
                        scope.launch {
                            val res = repo.login(email, password)
                            if (res != null) {
                                session.saveSession(res.id_usuario, res.token)
                                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                                navController.navigate("Home") { popUpTo(0) }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF1976D2))
                ) {
                    Text("Iniciar sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(15.dp))

                // --- Login con Google ---
                Button(
                    onClick = {
                        scope.launch {
                            val token = GoogleAuthManager.signIn(activity)

                            android.util.Log.d("GOOGLE_DATA", "Token recibido en LoginView: $token")

                            if (token != null) {
                                val response = repo.loginGoogle(token)
                                if (response != null) {
                                    session.saveSession(
                                        response.id_usuario,
                                        response.token
                                    )
                                    Toast.makeText(
                                        context,
                                        "Inicio con Google exitoso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("Home") { popUpTo(0) }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al iniciar sesión con Google",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Token de Google vacío",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.White)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.google_logo),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Continuar con Google", color = Color.Black)
                }


                // =============== REGISTRO ===============
                if (selectedTab == 1) {

                    var regEmail by remember { mutableStateOf("") }
                    var regPass by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regPass,
                        onValueChange = { regPass = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val nombre = regEmail.substringBefore('@')
                                val ok = repo.register(nombre, regEmail, regPass)
                                if (ok) {
                                    Toast.makeText(
                                        context,
                                        "Registro exitoso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("VerifyCode/$regEmail")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al registrar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFF1976D2))
                    ) {
                        Text("Registrarse", color = Color.White)
                    }
                }
            }
        }
    }
}
