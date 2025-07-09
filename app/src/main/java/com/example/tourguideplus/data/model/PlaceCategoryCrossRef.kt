package com.example.tourguideplus.data.model

import androidx.room.Entity

@Entity(
    tableName = "place_category_crossref",
    primaryKeys = ["placeId","categoryId"]
)
data class PlaceCategoryCrossRef(
    val placeId: Long,
    val categoryId: Long
)
