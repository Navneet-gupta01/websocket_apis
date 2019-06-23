package com.evolutiongaming.assingment

import java.util.UUID

import cats.effect.{ContextShift, Sync}
import cats.effect.concurrent.Ref
import fs2.{Pipe, Stream}
import fs2.concurrent.{Queue, Topic}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame._

class ApiRotues[F[_]](state: Ref[F, InMemoryState], queue: Queue[F, UserReqWrapper], topic: Topic[F, UserReqWrapper])
                     (implicit F: Sync[F], C: ContextShift[F]) extends Http4sDsl[F] {

  def uuid = UUID.randomUUID().toString()

  val endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ws_api" =>
      WebSocketFlow.createChannel(state, queue, topic)
  }
}


object WebSocketFlow {
  def uuid = UUID.randomUUID().toString

  def toClient[F[_]](connectionUUID: String, topic: Topic[F, UserReqWrapper]): Stream[F, Text] =
    topic
      .subscribe(1000)
      .filter(a => a.user == connectionUUID)
      .map(msg => Text(JsonUtils.toJson(msg.msg)))

  def processMessages[F[_]](stream: Stream[F, WebSocketFrame], connectionUUID: String, queue: Queue[F, UserReqWrapper]): Stream[F, Unit] = {
    val parsedWebSocketInput: Stream[F, UserReqWrapper] =
      stream
        .collect({
          case Text(text, _) => JsonUtils.decode(text) match {
            case Left(ex) =>
              UserReqWrapper(connectionUUID, UnexpectedFailure(s"parsing failed for msg ${text}"))
            case Right(msg) => UserReqWrapper(connectionUUID, msg)
          }

          case Close(_) => UserReqWrapper(connectionUUID, Disconnect)
        })
    parsedWebSocketInput.through(queue.enqueue)
  }

  def createChannel[F[_] : Sync : ContextShift](state: Ref[F, InMemoryState], queue: Queue[F, UserReqWrapper], topic: Topic[F, UserReqWrapper]) = {
    val connectionUUID = uuid

    def inputStreamFlow(stream: Stream[F, WebSocketFrame]) =
      processMessages(stream, connectionUUID, queue)

    WebSocketBuilder[F].build(toClient(connectionUUID, topic), inputStreamFlow)
  }


}