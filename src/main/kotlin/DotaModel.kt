package model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerResponse(
    val profile: Profile? = null,
    val rank_tier: Int? = null,
    val leaderboard_rank: Int? = null
)

@Serializable
data class Profile(
    val personaname: String? = null,
    val avatarfull: String? = null,
    val loccountrycode: String? = null
)

@Serializable
data class WinLossResponse(
    val win: Int = 0,
    val lose: Int = 0
)