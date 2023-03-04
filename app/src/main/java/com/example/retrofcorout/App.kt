package com.example.retrofcorout

import android.app.Application
import com.example.retrofcorout.data.database.UserRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App :Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val userDao by lazy { UserRoomDatabase.getDatabase(this, applicationScope).userDao() }
}