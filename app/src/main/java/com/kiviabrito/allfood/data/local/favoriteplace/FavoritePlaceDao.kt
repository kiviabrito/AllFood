package com.kiviabrito.allfood.data.local.favoriteplace

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoritePlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(favoritePlace: FavoritePlace)

    @Delete
    suspend fun deletePlace(favoritePlace: FavoritePlace)

    @Query("SELECT * FROM favorite_place_items")
    fun observeAllPlaces(): LiveData<List<FavoritePlace>>

}