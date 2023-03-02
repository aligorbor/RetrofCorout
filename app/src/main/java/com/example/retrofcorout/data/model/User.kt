package com.example.retrofcorout.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val avatar: String,
    var email: String,
    var name: String
) {
    override fun equals(other: Any?): Boolean {
       return (other as? User)?.let {
            it.id == this.id
        } ?: return false
    }

    override fun hashCode(): Int {
        val result = id.hashCode()
//        result = 31 * result + avatar.hashCode()
//        result = 31 * result + email.hashCode()
//        result = 31 * result + name.hashCode()
        return result
    }
}
