package com.example

import com.mgabbi.encryption.lib.Algorithm
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ApiKeysDao : Table("apiKeys") {
    val id = integer("id").primaryKey().autoIncrement()
    val key = varchar("key", 255).nullable()
    val favourite = bool("favourite")
    val createdAt = datetime("created_at").nullable()
    val userId = reference("user_id", UserDao.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
}

object UserDao : Table("users") {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}
