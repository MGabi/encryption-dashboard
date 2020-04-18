package com.example

import pl.treksoft.kvision.annotations.KVService

enum class Sort {
    CA
}

@KVService
interface IApiKeysService {
    suspend fun getApiKeysList(search: String?, types: String, sort: Sort): List<ApiKey>
    suspend fun getApiKey(apiKey: ApiKey): ApiKey
    suspend fun updateApiKey(apiKey: ApiKey): ApiKey
    suspend fun deleteApiKey(id: Int): Boolean
}

@KVService
interface IProfileService {
    suspend fun getProfile(): Profile
}

@KVService
interface IRegisterProfileService {
    suspend fun registerProfile(profile: Profile, password: String): Boolean
}
