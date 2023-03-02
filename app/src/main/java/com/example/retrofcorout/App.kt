package com.example.retrofcorout

import android.app.Application
import com.example.retrofcorout.data.database.UserRoomDatabase

class App :Application() {
    val userDao by lazy { UserRoomDatabase.getDatabase(this).userDao() }
}