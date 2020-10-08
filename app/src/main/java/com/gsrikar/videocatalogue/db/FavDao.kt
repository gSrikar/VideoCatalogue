package com.gsrikar.videocatalogue.db

import androidx.room.*

@Dao
interface FavDao {

    /**
     * Insert the favorite video
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favEntity: FavEntity)

    /**
     * Query all the favorites
     */
    @Query("SELECT * FROM favorite")
    suspend fun query(): List<FavEntity>

    /**
     * Update the favorite video
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(favEntity: FavEntity)

    /**
     * Delete the favorite video
     */
    @Delete
    suspend fun delete(favEntity: FavEntity)
}