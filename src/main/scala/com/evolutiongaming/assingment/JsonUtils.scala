package com.evolutiongaming.assingment

import io.circe.Printer
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.syntax._

object JsonUtils {
  implicit val genDevConfig: Configuration = Configuration.default.withDiscriminator("$type")

  def decode(str: String): scala.Either[io.circe.Error, Message] = {
    io.circe.parser.decode[Message](str)
  }

  def toJson(msg: Message) = msg.asJson.pretty(Printer.noSpaces.copy(dropNullValues = true))
}
