package com.example

import com.example.Db.dbQuery
import com.example.Db.queryList
import com.github.andrewoma.kwery.core.builder.query
import com.google.inject.Inject
import com.mgabbi.encryption.lib.Algorithm
import com.mgabbi.encryption.lib.key.KeyUtils
import io.ktor.application.ApplicationCall
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import pl.treksoft.kvision.remote.RemoteOption
import java.sql.ResultSet
import java.time.ZoneId

suspend fun <RESP> ApplicationCall.withProfile(block: suspend (Profile) -> RESP): RESP {
    val profile = this.sessions.get<Profile>()
    return profile?.let {
        block(profile)
    } ?: throw IllegalStateException("Profile not set!")
}

actual class ApiKeysService : IApiKeysService {

    @Inject
    lateinit var call: ApplicationCall

    override suspend fun getApiKeysList(search: String?, types: String, sort: Sort) =
        call.withProfile { profile ->
            dbQuery {
                val query = query {
                    select("SELECT * FROM apiKeys")
                    whereGroup {
                        where("user_id = :user_id")
                        parameter("user_id", profile.id)
                        search?.let {
                            where(
                                """(lower(created_at) like :search""".trimMargin()
                            )
                            parameter("search", "%${it.toLowerCase()}%")
                        }
                        if (types == "fav") {
                            where("favourite")
                        }
                    }
                    when (sort) {
                        Sort.CA -> orderBy("lower(created_at)")
                    }
                }
                queryList(query.sql, query.parameters) {
                    toApiKey(it)
                }
            }
        }

    override suspend fun getApiKey(apiKey: ApiKey) = call.withProfile { profile ->
        val key = dbQuery {
            (ApiKeysDao.insert {
                it[key] = KeyUtils.createAPIKey(Algorithm.valueOf(apiKey.type))
                it[type] = apiKey.type
                it[name] = apiKey.name
                it[favourite] = apiKey.favourite ?: false
                it[createdAt] = DateTime()
                it[userId] = profile.id!!

            } get ApiKeysDao.id)
        }
        getApiKey(key)!!
    }

    override suspend fun updateApiKey(apiKey: ApiKey) = call.withProfile { profile ->
        apiKey.id?.let { keyID ->
            getApiKey(keyID)?.let { oldApiKey ->
                dbQuery {
                    ApiKeysDao.update({ ApiKeysDao.id eq keyID }) {
                        it[key] = KeyUtils.createAPIKey(Algorithm.valueOf(apiKey.type))
                        it[type] = apiKey.type
                        it[name] = apiKey.name
                        it[favourite] = apiKey.favourite ?: false
                        it[createdAt] = oldApiKey.createdAt
                            ?.let { DateTime(java.util.Date.from(it.atZone(ZoneId.systemDefault()).toInstant())) }
                        it[userId] = profile.id!!
                    }
                }
            }
            getApiKey(keyID)
        } ?: throw IllegalArgumentException("The ID of this API key is not set")
    }

    override suspend fun deleteApiKey(id: Int): Boolean = call.withProfile { profile ->
        dbQuery {
            ApiKeysDao.deleteWhere { (ApiKeysDao.userId eq profile.id!!) and (ApiKeysDao.id eq id) } > 0
        }
    }

    override suspend fun getEncryptionTypes(search: String?, initial: String?, s: String?): List<RemoteOption> =
        call.withProfile {
            Algorithm.values().map { RemoteOption(it.toString()) }
        }

    private suspend fun getApiKey(id: Int): ApiKey? = dbQuery {
        ApiKeysDao.select {
            ApiKeysDao.id eq id
        }.mapNotNull { toApiKey(it) }.singleOrNull()
    }

    private fun toApiKey(row: ResultRow): ApiKey =
        ApiKey(
            id = row[ApiKeysDao.id],
            key = row[ApiKeysDao.key],
            type = row[ApiKeysDao.type],
            name = row[ApiKeysDao.name],
            favourite = row[ApiKeysDao.favourite],
            createdAt = row[ApiKeysDao.createdAt]?.millis?.let { java.util.Date(it) }?.toInstant()
                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            userId = row[ApiKeysDao.userId]
        )

    private fun toApiKey(rs: ResultSet): ApiKey =
        ApiKey(
            id = rs.getInt(ApiKeysDao.id.name),
            key = rs.getString(ApiKeysDao.key.name),
            type = rs.getString(ApiKeysDao.type.name),
            name = rs.getString(ApiKeysDao.name.name),
            favourite = rs.getBoolean(ApiKeysDao.favourite.name),
            createdAt = rs.getTimestamp(ApiKeysDao.createdAt.name)?.toInstant()
                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            userId = rs.getInt(ApiKeysDao.userId.name)
        )
}

actual class ProfileService : IProfileService {

    @Inject
    lateinit var call: ApplicationCall

    override suspend fun getProfile() = call.withProfile { it }

}

actual class RegisterProfileService : IRegisterProfileService {

    override suspend fun registerProfile(profile: Profile, password: String): Boolean {
        try {
            dbQuery {
                UserDao.insert {
                    it[this.name] = profile.name!!
                    it[this.username] = profile.username!!
                    it[this.password] = DigestUtils.sha256Hex(password)
                }
            }
        } catch (e: Exception) {
            throw Exception("Register operation failed!")
        }
        return true
    }

}
