package com.evolutiongaming.assingment

import cats.effect._
import cats.implicits._
import fs2.concurrent.{Queue, Topic}
import cats.effect.concurrent.Ref
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import scala.concurrent.duration._
import fs2.Stream

object Server extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = for {
    queue <- Queue.unbounded[IO, UserReqWrapper]
    topic <- Topic[IO, UserReqWrapper](UserReqWrapper("", Connect))
    ref <- Ref.of[IO, InMemoryState](InMemoryState())
    exitCode <- {
      val httpStream = ServerChannel.channel[IO](9090, ref, queue, topic)
      val keepAlive = Stream.awakeEvery[IO](30.seconds).map(_ => UserReqWrapper("",KeepActive)).through(topic.publish)

      val processingStream =
        queue
          .dequeue
          .evalMap(msg => ref.modify(a => a.processMessage(msg)))
          .flatMap(Stream.emits)
          .through(topic.publish)

      Stream(httpStream, keepAlive, processingStream)
        .parJoinUnbounded
        .compile
        .drain
        .as(ExitCode.Success)
    }

  } yield exitCode
}

object ServerChannel {
  def channel[F[_]: ConcurrentEffect : Timer: ContextShift](port: Int, state: Ref[F, InMemoryState], queue: Queue[F, UserReqWrapper], topic: Topic[F, UserReqWrapper]): fs2.Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(port, "0.0.0.0")
      .withIdleTimeout(300.seconds)
      .withHttpApp(Router(
        "/" -> new ApiRotues[F](state, queue, topic).endpoints
      ).orNotFound)
      .serve
}
