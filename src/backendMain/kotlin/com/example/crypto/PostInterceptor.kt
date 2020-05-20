package com.example.crypto

import com.google.gson.Gson
import com.mgabbi.encryption.lib.crypto.Encryption
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route

inline fun <reified R : Any, reified O : Any> Route.postSecure(
    path: String,
    crossinline body: (R) -> O
): Route {
    return route(path, HttpMethod.Post) {
        handle {
            val gson = Gson()
            val received = call.receive<ByteArray>()

            // Decrypt the request
            val decrypted = Encryption.decode(received)
            val requestJson = gson.fromJson(decrypted, R::class.java)

            println("received >>>> $received")
            println("received >>>> $requestJson")

            val response = body(requestJson)

            // Encrypt the response
            val responseJson = gson.toJson(response)
            val encrypted = Encryption.encode(responseJson)

            println("returned >>>> $response")
            println("returned >>>> $encrypted")

            call.respond(encrypted)
        }
    }
}