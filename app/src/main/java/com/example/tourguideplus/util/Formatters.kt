package com.example.tourguideplus.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toPrettyDateTime(): String {
    val locale = Locale.getDefault()
    val fmt = SimpleDateFormat("d MMM yyyy, HH:mm", locale)
    return fmt.format(Date(this))
}
