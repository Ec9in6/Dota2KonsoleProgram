package api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import model.PlayerResponse
import model.WinLossResponse

class OpenDotaClient(private val client: HttpClient) {
    private val baseUrl = "https://api.opendota.com/api"

    suspend fun getPlayer(id: String): PlayerResponse =
        client.get("$baseUrl/players/$id").body()

    suspend fun getWinLoss(id: String): WinLossResponse =
        client.get("$baseUrl/players/$id/wl").body()
}