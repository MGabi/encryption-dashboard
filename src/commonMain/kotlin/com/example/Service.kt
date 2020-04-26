package com.example

import pl.treksoft.kvision.annotations.KVService
import pl.treksoft.kvision.remote.RemoteOption

enum class Sort {
    CA
}

@KVService
interface IApiKeysService {
    suspend fun getApiKeysList(search: String?, types: String, sort: Sort): List<ApiKey>
    suspend fun getApiKey(apiKey: ApiKey): ApiKey
    suspend fun updateApiKey(apiKey: ApiKey): ApiKey
    suspend fun deleteApiKey(id: Int): Boolean
    suspend fun getEncryptionTypes(search: String? = "", initial: String? = "AES", s: String? = ""): List<RemoteOption>
}

@KVService
interface IProfileService {
    suspend fun getProfile(): Profile
}

@KVService
interface IRegisterProfileService {
    suspend fun registerProfile(profile: Profile, password: String): Boolean
}
