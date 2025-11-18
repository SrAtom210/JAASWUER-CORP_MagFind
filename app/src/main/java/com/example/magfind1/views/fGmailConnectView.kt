import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.magfind1.SessionManager
import com.example.magfind1.components.fPlantilla
import com.example.magfind1.ui.theme.ThemeViewModel
import com.example.magfind1.viewmodels.GmailViewModel

@Composable
fun fGmailConnectView(navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val tokenUser = session.getToken() ?: ""

    val gmailVM = remember { GmailViewModel() }
    val connected by gmailVM.connected.collectAsState()
    val emailConnected by gmailVM.connectedEmail.collectAsState()
    val loading by gmailVM.loading.collectAsState()

    LaunchedEffect(Unit) {
        if (tokenUser.isNotEmpty()) {
            gmailVM.checkStatus(tokenUser)
        }
    }

    fPlantilla(
        title = "Conectar Gmail",
        navController = navController,
        themeViewModel = themeViewModel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (loading) {
                CircularProgressIndicator()
                return@Column
            }

            if (connected) {
                Text("Cuenta vinculada: $emailConnected", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        gmailVM.sync(tokenUser)
                        navController.navigate("CorreosCat")
                    }
                ) {
                    Text("Sincronizar correos")
                }
            } else {

                Button(
                    onClick = {
                        openBrowser(
                            context,
                            "https://api.magfind.xyz/gmail/connect?token=$tokenUser"
                        )
                    }
                ) {
                    Text("Conectar con Gmail")
                }
            }
        }
    }
}

fun openBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


