package ru.krivonosovdenis.fintechapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.krivonosovdenis.fintechapp.dataclasses.CommentData
import ru.krivonosovdenis.fintechapp.dataclasses.PostData
import ru.krivonosovdenis.fintechapp.dataclasses.UserProfileData

@Database(entities = [PostData::class, UserProfileData::class, CommentData::class], version = 9)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun commonDao(): CommonDao

    companion object {
        private var INSTANCE: ApplicationDatabase? = null

        const val POST_SOURCE_FEED = 0
        const val POST_SOURCE_PROFILE = 1

        fun getInstance(context: Context): ApplicationDatabase {
            if (INSTANCE == null) {
                synchronized(ApplicationDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ApplicationDatabase::class.java, "applicationDatabase.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
