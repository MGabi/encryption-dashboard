@file:ContextualSerialization(LocalDateTime::class)

package com.example

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.types.LocalDateTime

@Serializable
data class Profile(
    val id: Int? = null,
    val name: String? = null,
    val username: String? = null,
    val password: String? = null,
    val password2: String? = null
)

@Serializable
data class ApiKey(
    val id: Int? = 0,
    val key: String? = null,
    val favourite: Boolean? = false,
    val createdAt: LocalDateTime? = null,
    val userId: Int? = null
)
