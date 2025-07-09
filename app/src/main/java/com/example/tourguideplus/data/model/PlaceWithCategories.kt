package com.example.tourguideplus.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaceWithCategories(
    @Embedded val place: PlaceEntity,
    @Relation(
        parentColumn  = "id",
        entityColumn  = "id",
        associateBy   = Junction(
            value        = PlaceCategoryCrossRef::class,
            parentColumn = "placeId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<CategoryEntity>
)
