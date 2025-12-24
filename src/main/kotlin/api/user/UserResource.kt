package api.user

import JwtUtil
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
open class UserResource {

    @Inject
    private lateinit var jwtUtil: JwtUtil

    @POST
    @Path("/auth/")
    open fun auth() : Response {
        val token = jwtUtil.generateToken("petrovich")
        return Response.ok(mapOf("token" to token)).build()
    }
}