package api.point

import JwtUtil
import api.configs.AuthConfig
import database.model.Point2DRow
import database.repositories.DBPointsRepository
import database.repositories.DBUserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.Json
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import java.util.concurrent.ConcurrentHashMap


@ServerEndpoint(value = "/ws/points/{token}", configurator = AuthConfig::class)
@ApplicationScoped
open class PointWSResource {
    @Inject
    private lateinit var pointsRepository: DBPointsRepository

    @Inject
    private lateinit var userRepository: DBUserRepository

    @Inject
    private lateinit var jwtUtil: JwtUtil

    companion object {
        open val userSessions = ConcurrentHashMap<Long, MutableSet<Session>>()
    }

    @OnOpen
    open fun onOpen(session: Session, @PathParam("token") token: String) {
        val username = jwtUtil.getUsernameFromToken(token)
        if (username == null) {
            session.close(CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"))
            return
        }
        val user = userRepository.findByUsername(username)!!
        userSessions.getOrPut(user.id!!) { mutableSetOf() }.add(session)
    }

    open fun broadcastToAll(message: String) {
        userSessions.values.flatten().forEach { session ->
            if (session.isOpen) {
                session.asyncRemote.sendText(message)
            }
        }
    }

    open fun broadcastNewPoint(point: Point2DRow) {
        val json = Json.createObjectBuilder()
            .add("id", point.id ?: -1)
            .add("x", point.point2DR.x.toDouble())
            .add("y", point.point2DR.y.toDouble())
            .add("R", point.point2DR.R.toDouble())
            .add("inArea", point.inArea)
            .add("executionTime", point.executionTime)
            .add("timestamp", point.formattedTimestamp)
            .add("username", point.user.username)
            .build()
            .toString()

        userSessions.values.flatten().forEach { session ->
            if (session.isOpen) {
                session.asyncRemote.sendText(json)
            }
        }
    }

    @OnClose
    open fun onClose(session: Session, @PathParam("token") token: String) {
        val username = jwtUtil.getUsernameFromToken(token) ?: return
        val user = userRepository.findByUsername(username)!!

        userSessions[user.id]?.remove(session)
    }

    @OnError
    open fun onError(session: Session, error: Throwable) {
        error.printStackTrace()
        session.close()
    }

    open fun broadcastToUser(userId: Long, message: String) {
        userSessions[userId]?.forEach { session ->
            if (session.isOpen) {
                session.asyncRemote.sendText(message)
            } else {
                userSessions[userId]?.remove(session)
            }
        }
    }
}