package com.rhs.githubprofiles.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rhs.githubprofiles.data.model.Associations
import com.rhs.githubprofiles.data.model.QueriesModel
import com.rhs.githubprofiles.data.model.UserResponse


@Database(
    entities = [UserResponse::class, Associations::class, QueriesModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class
)
/**
 * The Room database for this app.
 */
abstract class AppDatabase : RoomDatabase() {
    abstract fun associationsDao(): AssociationsDao?

    abstract fun queriesDao(): QueriesDao?

    abstract fun userDao(): UserDao?

    companion object {
        private val LOG_TAG = AppDatabase::class.java.simpleName
        private val LOCK = Any()
        private const val DATABASE_NAME = "Github Profiles"
        private var sInstance: AppDatabase? = null

        /**
         * Get the database instance
         *
         * @param context the context
         * @return the database instance
         */
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    )
                        .build()
                }
            }
            Log.d(LOG_TAG, "Getting the database instance")
            return sInstance!!
        }
    }
}