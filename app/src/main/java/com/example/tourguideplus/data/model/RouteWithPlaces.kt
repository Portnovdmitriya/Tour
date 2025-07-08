package com.example.tourguideplus.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RouteWithPlaces(
    @Embedded
    val route: RouteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RoutePlaceCrossRef::class,
            parentColumn = "routeId",
            entityColumn = "placeId"
        )
    )
    val places: List<PlaceEntity>
)
