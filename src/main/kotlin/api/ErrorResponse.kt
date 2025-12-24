package api

import jakarta.json.Json
import jakarta.json.JsonObject

open class ErrorResponse {

    val body = Json.createObjectBuilder()

    open fun error(message: String) : JsonObject {
        body.add("error", message)
        return build()
    }

    override fun toString(): String {
        return build().toString()
    }

    open fun build() : JsonObject {
        return body.build()
    }
}