package com.example.tourguideplus.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithPlace(
    @Embedded val note: NoteEntity,
    @Relation(parentColumn = "placeId", entityColumn = "id")
    val place: PlaceEntity
)
