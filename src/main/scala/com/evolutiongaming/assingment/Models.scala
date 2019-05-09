package com.evolutiongaming.assingment


sealed trait UserType
case object AdminT extends UserType
case object UserT extends UserType


sealed trait Message
case class login(username: String, password: String) extends Message
object login_failed extends Message
case class login_successful(user_type : UserType) extends Message
case class ping(seq : Int) extends Message
case class pong(seq: Int) extends Message
case class fail(message: String) extends Message
case object not_authorized extends Message
object subscribe_tables extends Message
object unsubscribe_tables extends Message
case class table_list(tables : List[table]) extends Message
case class add_table(after_id: Int, table: table) extends Message
case class update_table(table: table) extends Message
case class remove_table(id: Int) extends Message
case class add_failed(id: Int) extends Message
case class removal_failed(id : Int) extends Message
case class update_failed(id : Int) extends Message
case class table_added(after_id : Int, table : table) extends Message
case class table_removed(id : Int) extends Message
case class table_updated(table : table) extends Message
case class UnexpectedFailure(msg: String) extends Message
case object Disconnect extends Message
case object Connect extends Message
case object KeepActive extends Message
case object EmptyMessage extends Message
case class AnotherSession(str: String) extends Message

case class UserReqWrapper(user: String, msg: Message)

case class User(username: String, password: String, userType: UserType)
case class table(id: Option[Int], name: String, participants: Int)
