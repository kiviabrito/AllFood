package com.kiviabrito.allfood.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlaceDao

@Database(
    entities = [FavoritePlace::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritePlaceDao(): FavoritePlaceDao

    companion object {

        const val NAME = "AllFood_Database"
    }
}