package com.kiviabrito.allfood.data.local.favoriteplace

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_place_items")
data class FavoritePlace(
    @PrimaryKey
    var id: String,
    var name: String
)