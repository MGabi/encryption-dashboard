package com.example

enum class EncryptionTypes(val type: String) {
    SHA256("SHA256"),
    AES256("AES256"),
    RSA("RSA");

    companion object {
        val pairs
            get() = values().map {
                it.type to it.type
            }
    }
}