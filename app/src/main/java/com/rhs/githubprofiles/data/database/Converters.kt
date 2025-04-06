package com.rhs.githubprofiles.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * The Converters for the Room database.
 */
object Converters {

    /**
     * Convert a list of strings to a string.
     *
     * @param value string to convert
     * @return list of strings
     */
    @TypeConverter
    fun fromStringToArray(value: String?): List<String> {
        val listType =
            object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    /**
     * Convert a list of strings to a string.
     *
     * @param list list of strings
     * @return string
     */
    @TypeConverter
    fun fromListToString(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}