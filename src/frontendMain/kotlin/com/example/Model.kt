package com.example

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.remote.RemoteOption
import pl.treksoft.kvision.state.ObservableList
import pl.treksoft.kvision.state.observableListOf
import pl.treksoft.kvision.utils.syncWithList

object Model {

    private val apiKeysService = ApiKeysService()
    private val profileService = ProfileService()
    private val registerProfileService = RegisterProfileService()

    val apiKeys: ObservableList<ApiKey> = observableListOf()
    val profile: ObservableList<Profile> = observableListOf(Profile())
    val encryptionTypes: ObservableList<RemoteOption> = observableListOf()

    var search: String? = null
        set(value) {
            field = value
            GlobalScope.launch {
                getApiKeysList()
            }
        }
    var types: String = "all"
        set(value) {
            field = value
            GlobalScope.launch {
                getApiKeysList()
            }
        }
    var sort = Sort.CA
        set(value) {
            field = value
            GlobalScope.launch {
                getApiKeysList()
            }
        }

    suspend fun getApiKeysList() {
        Security.withAuth {
            val newApiKeys = apiKeysService.getApiKeysList(search, types, sort)
            console.log("List: $newApiKeys")
            apiKeys.syncWithList(newApiKeys)
        }
    }

    suspend fun getEncryptionTypesList() {
        Security.withAuth {
            val newEncTypes = apiKeysService.getEncryptionTypes()
            encryptionTypes.syncWithList(newEncTypes)
        }
    }

    suspend fun addApiKey(apiKey: ApiKey) {
        Security.withAuth {
            apiKeysService.getApiKey(apiKey)
            getApiKeysList()
        }
    }

    suspend fun updateApiKey(apiKey: ApiKey) {
        Security.withAuth {
            apiKeysService.updateApiKey(apiKey)
            getApiKeysList()
        }
    }

    suspend fun deleteApiKey(id: Int): Boolean {
        return Security.withAuth {
            val result = apiKeysService.deleteApiKey(id)
            getApiKeysList()
            result
        }
    }

    suspend fun readProfile() {
        Security.withAuth {
            profile[0] = profileService.getProfile()
        }
    }

    suspend fun registerProfile(profile: Profile, password: String): Boolean {
        return try {
            registerProfileService.registerProfile(profile, password)
        } catch (e: Exception) {
            console.log(e)
            false
        }
    }
}
