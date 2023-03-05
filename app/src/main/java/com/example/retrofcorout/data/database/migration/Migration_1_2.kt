package com.example.retrofcorout.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration_1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE User " +
                "ADD COLUMN dateBirth INTEGER DEFAULT 0")
    }
}