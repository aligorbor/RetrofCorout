package com.example.retrofcorout.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.retrofcorout.data.database.migration.Migration_1_2
import com.example.retrofcorout.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(User::class), version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)

abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): UserRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserRoomDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration() // Wipes and rebuilds instead of migrating if no Migration object.
                 //   .allowMainThreadQueries() //Only for examples and testing
                    .addMigrations(Migration_1_2())
                    .addCallback(UserDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class UserDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
               //         populateDatabase(database.userDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
          //              populateDatabase(database.userDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabase(userDao: UserDao) {
            // Start the app with a clean database every time.
            userDao.deleteAllUsers()

        }
    }
}