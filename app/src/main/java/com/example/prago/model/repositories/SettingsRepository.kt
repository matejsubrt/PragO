package com.example.prago.model.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val transferBufferDefault: Float = 2f
const val transferLengthDefault: Float = 1f
const val comfortPreferenceDefault: Float = 2f
const val bikeTripBufferDefault: Float = 2f
const val useSharedBikesDefault: Boolean = false
const val bikeMax15MinutesDefault: Boolean = true


const val walkingPaceDefault: Int = 12
const val cyclingPaceDefault: Int = 5
const val bikeUnlockTimeDefault: Int = 30
const val bikeLockTimeDefault: Int = 15



class SettingsRepository(private val preferencesDataStore: DataStore<Preferences>) {
    private companion object{
        val TRANSFER_BUFFER = floatPreferencesKey("transferBuffer")
        val TRANSFER_LENGTH = floatPreferencesKey("transferLength")
        val COMFORT_PREFERENCE = floatPreferencesKey("comfortPreference")
        val BIKE_TRIP_BUFFER = floatPreferencesKey("bikeTripBuffer")
        val USE_SHARED_BIKES = stringPreferencesKey("useSharedBikes")

        val WALKING_PACE = intPreferencesKey("walkingPace")
        val CYCLING_PACE = intPreferencesKey("cyclingPace")
        val BIKE_UNLOCK_TIME = intPreferencesKey("bikeUnlockTime")
        val BIKE_LOCK_TIME = intPreferencesKey("bikeLockTime")
        val BIKE_MAX_15_MINUTES = stringPreferencesKey("bikeMax15Minutes")
    }

    private fun <T> preferenceFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return preferencesDataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    private fun floatPreferenceFlow(key: Preferences.Key<Float>, defaultValue: Float): Flow<Float> {
        return preferencesDataStore.data.map { preferences ->
            preferences[key]?.toFloat() ?: defaultValue
        }
    }

    //TODO: change string to boolean
    private fun booleanPreferenceFlow(key: Preferences.Key<String>, defaultValue: Boolean): Flow<Boolean> {
        return preferencesDataStore.data.map { preferences ->
            preferences[key].toBoolean() ?: defaultValue
        }
    }

    private fun intPreferenceFlow(key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> {
        return preferencesDataStore.data.map { preferences ->
            preferences[key]?.toInt() ?: defaultValue
        }
    }

    suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        preferencesDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getIntSettingValue(preferencesKey: String, defaultValue: Int): Flow<Int> {
        return preferencesDataStore.data.map { preferences ->
            preferences[intPreferencesKey(preferencesKey)] ?: defaultValue
        }
    }

    val transferBuffer : Flow<Float> = preferenceFlow(TRANSFER_BUFFER, transferBufferDefault)
    val transferLength : Flow<Float> = preferenceFlow(TRANSFER_LENGTH, transferLengthDefault)
    val timeComfortBalance : Flow<Float> = preferenceFlow(COMFORT_PREFERENCE, comfortPreferenceDefault)

    val bikeTripBuffer : Flow<Float> = preferenceFlow(BIKE_TRIP_BUFFER, bikeTripBufferDefault)
    val useSharedBikes : Flow<Boolean> = booleanPreferenceFlow(USE_SHARED_BIKES, useSharedBikesDefault)
    val bikeMax15Minutes : Flow<Boolean> = booleanPreferenceFlow(BIKE_MAX_15_MINUTES, bikeMax15MinutesDefault)

    fun getBoolSetting(key: String, defaultValue: Boolean){
        val dataStoreKey = booleanPreferencesKey(key)
    }


    val walkingPace : Flow<Int> = preferenceFlow(WALKING_PACE, walkingPaceDefault)
    val cyclingPace : Flow<Int> = preferenceFlow(CYCLING_PACE, cyclingPaceDefault)
    val bikeUnlockTime : Flow<Int> = preferenceFlow(BIKE_UNLOCK_TIME, bikeUnlockTimeDefault)
    val bikeLockTime : Flow<Int> = preferenceFlow(BIKE_LOCK_TIME, bikeLockTimeDefault)


    suspend fun saveTransferBuffer(value: Float) = savePreference(TRANSFER_BUFFER, value)
    suspend fun saveTransferLength(value: Float) = savePreference(TRANSFER_LENGTH, value)
    suspend fun saveComfortPreference(value: Float) = savePreference(COMFORT_PREFERENCE, value)
    suspend fun saveBikeTripBuffer(value: Float) = savePreference(BIKE_TRIP_BUFFER, value)
    suspend fun saveUseSharedBikes(value: Boolean) = savePreference(USE_SHARED_BIKES, value.toString())
    suspend fun saveBikeMax15Minutes(value: Boolean) = savePreference(BIKE_MAX_15_MINUTES, value.toString())

    suspend fun saveWalkingPace(value: Int) = savePreference(WALKING_PACE, value)
    suspend fun saveCyclingPace(value: Int) = savePreference(CYCLING_PACE, value)
    suspend fun saveBikeUnlockTime(value: Int) = savePreference(BIKE_UNLOCK_TIME, value)
    suspend fun saveBikeLockTime(value: Int) = savePreference(BIKE_LOCK_TIME, value)
}