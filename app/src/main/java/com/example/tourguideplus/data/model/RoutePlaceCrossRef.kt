package com.example.tourguideplus.data.model

import androidx.room.Entity

@Entity(primaryKeys = ["routeId","placeId"], tableName = "route_place_crossref")
data class RoutePlaceCrossRef(
    val routeId: Long,
    val placeId: Long
)
