package uz.raytel.raytel.data.remote.realtime

import kotlin.random.Random
import kotlin.random.nextInt

data class OnlineCount(
    val onlineCount: Int = Random.nextInt(2000..3000)
)
