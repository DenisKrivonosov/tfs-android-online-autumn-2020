package ru.krivonosovdenis.fintechapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.krivonosovdenis.fintechapp.dataclasses.PostFullData

@Database(entities = [PostFullData::class], version = 2)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun feedPostsDao(): FeedPostsDao

    companion object {
        private var INSTANCE: ApplicationDatabase? = null

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