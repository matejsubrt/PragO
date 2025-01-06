package com.example.prago.model.dataClasses.requests

import com.example.prago.utils.serialization.JsonName

data class SearchSettings(
    @JsonName("WalkingPace") val walkingPace: Int,
    @JsonName("CyclingPace") val cyclingPace: Int,
    @JsonName("BikeUnlockTime") val bikeUnlockTime: Int,
    @JsonName("BikeLockTime") val bikeLockTime: Int,
    @JsonName("UseSharedBikes") val useSharedBikes: Boolean,
    @JsonName("BikeMax15Minutes") val bikeMax15Minutes: Boolean,
    @JsonName("TransferBuffer") val transferBuffer: Int,
    @JsonName("ComfortBalance") val comfortBalance: Int,
    @JsonName("WalkingPreference") val walkingPreference: Int,
    @JsonName("BikeTripBuffer") val bikeTripBuffer: Int
)