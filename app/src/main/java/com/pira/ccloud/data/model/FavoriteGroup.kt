package com.pira.ccloud.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteGroup(
    val id: String, // UUID or unique identifier
    val name: String,
    val isDefault: Boolean = false,
    val movieIds: MutableList<Int> = mutableListOf(),
    val seriesIds: MutableList<Int> = mutableListOf()
)