package api

import api.response.GeneralResponseBuilder
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.ResponseBuilder

open class GenericResource {
    companion object {
        fun ok(message: String): Response =
            generateSampleResponse(Response.Status.OK)
                .entity(GeneralResponseBuilder().ok(message))
                .build()

        fun badRequest(message: String): Response =
            generateSampleResponse(Response.Status.BAD_REQUEST)
                .entity(GeneralResponseBuilder().error(message))
                .build()

        fun unauthorized(message: String): Response =
            generateSampleResponse(Response.Status.UNAUTHORIZED)
                .entity(
                    GeneralResponseBuilder()
                        .error(message),
                ).build()

        fun conflict(message: String): Response =
            generateSampleResponse(Response.Status.CONFLICT)
                .entity(GeneralResponseBuilder().error(message))
                .build()

        protected fun generateSampleResponse(status: Response.Status): ResponseBuilder = Response.status(status)
    }
}
