import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.OpenDotaClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.UnknownHostException
import org.jetbrains.skia.Image as SkiaImage

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 10000
    }
}

suspend fun loadNetworkImage(url: String): ImageBitmap? {
    return try {
        val bytes = httpClient.get(url).readBytes()
        SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

fun main() = application {
    val api = OpenDotaClient(httpClient)
    val scope = rememberCoroutineScope()

    var accountId by remember { mutableStateOf("105248644") }
    var playerName by remember { mutableStateOf("Введите ID") }
    var statsText by remember { mutableStateOf("") }
    var avatarBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Window(onCloseRequest = ::exitApplication, title = "Dota 2 Stats") {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                avatarBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(128.dp).padding(bottom = 16.dp)
                    )
                }

                TextField(
                    value = accountId,
                    onValueChange = { accountId = it },
                    label = { Text("Account ID") },
                    isError = isError,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    enabled = !isLoading,
                    onClick = {
                        if (accountId.toLongOrNull() == null) {
                            playerName = "Ошибка: ID должен быть числом!"
                            isError = true
                            return@Button
                        }

                        isLoading = true
                        isError = false
                        scope.launch {
                            try {
                                val player = api.getPlayer(accountId)
                                val wl = api.getWinLoss(accountId)

                                val profile = player.profile
                                playerName = profile?.personaname ?: "Скрытый профиль"

                                val country = profile?.loccountrycode ?: "Unknown"
                                val leaderboard = player.leaderboard_rank?.let { "#$it" } ?: "No Rank"

                                statsText = "Регион: $country | Лидерборд: $leaderboard\n" +
                                        "Победы: ${wl.win} | Лузы: ${wl.lose}\n" +
                                        "Ранг: ${player.rank_tier ?: "N/A"}"

                                profile?.avatarfull?.let {
                                    avatarBitmap = loadNetworkImage(it)
                                }

                            } catch (e: UnknownHostException) {
                                playerName = "Нет подключения к сети"
                                isError = true
                            } catch (e: Exception) {
                                playerName = "Ошибка: ${e.message}"
                                isError = true
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text(if (isLoading) "Загрузка..." else "Поиск")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = playerName,
                    style = MaterialTheme.typography.h5,
                    color = if (isError) Color.Red else Color.Unspecified
                )

                if (statsText.isNotEmpty() && !isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = statsText, style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}