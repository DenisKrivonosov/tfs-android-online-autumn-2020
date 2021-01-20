package ru.krivonosovdenis.fintechapp.data.db

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun longToJodaDateTime(value: Long?): DateTime? {
        return value?.let { DateTime(it) }
    }

    @TypeConverter
    fun jodaDateTimeToLong(date: DateTime?): Long? {
        return date?.millis
    }
}
