package com.example.prago.formatters

fun formatTime(time: Long): String {
    return if(time >= 60 * 60 * 24){
        val days = time / (60*60*24)
        "$days days"
    } else if (time >= 60 * 60){
        val hours = time / (60 * 60)
        val minutes = (time % (60 * 60)) / 60
        val formattedMinutes = String.format("%02d", minutes)
        "$hours:$formattedMinutes h"
    } else if (time >= 60) {
        val minutes = time / 60
        val seconds = time % 60
        val formattedSeconds = String.format("%02d", seconds)
        "$minutes:$formattedSeconds min"
    } else {
        "$time s"
    }
}

fun formatDurationTime(time: Long): String {
    return if(time >= 60 * 60){
        val hours = time / (60 * 60)
        val minutes = (time % (60 * 60)) / 60
        val formattedMinutes = String.format("%02d", minutes)
        "$hours:$formattedMinutes h"
    } else{
        val minutes = time / 60
        "$minutes min"
    }
}

fun formatDistance(distance: Int): String {
    return if (distance < 1000) {
        "$distance m"
    } else {
        val km = distance / 1000.0
        "%.1f km".format(km)
    }
}