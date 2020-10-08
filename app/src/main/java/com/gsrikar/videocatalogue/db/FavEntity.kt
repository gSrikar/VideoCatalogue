package com.gsrikar.videocatalogue.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite")
data class FavEntity(

    @PrimaryKey
    @ColumnInfo(name = "uri")
    val uri: String
)
