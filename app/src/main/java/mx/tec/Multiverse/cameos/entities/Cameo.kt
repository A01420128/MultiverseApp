package mx.tec.Multiverse.cameos.entities

import java.io.Serializable

data class Cameo (
    val id: String? = "",
    val name: String? = "",
    val universe: String? = "",
    val gender: String? = "",
    val imageUrl: String? = "",
): Serializable
