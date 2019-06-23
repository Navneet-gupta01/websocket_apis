package com.evolutiongaming.assingment

import java.util.UUID
import cats.implicits._

import org.scalatest.{BeforeAndAfterEach, FunSuite}

class InMemoryStateTest extends FunSuite {
  def uuid: String = UUID.randomUUID().toString()

  test("Login should be successful for Valid Credentials") {
    val connectionUUID:String = uuid
    val memoryState = InMemoryState()
    val newMemoryState = memoryState.copy(connected = memoryState.connected + ( "admin" -> (connectionUUID, AdminT)), subscribers = memoryState.subscribers + connectionUUID  )

    val response = memoryState.processMessage(UserReqWrapper(connectionUUID,login("admin", "admin")))
    // assert( response._1.subscribers == newMemoryState.subscribers)//  this will be updated after subscription message
    assert( response._1.connected.get("admin") == (connectionUUID, AdminT))
    assert( response._1.tables.length == 0)
    assert(response._2 == Seq(UserReqWrapper(connectionUUID, login_successful(AdminT))))

  }

  test("Login should be unsuccessful for InValid Credentials") {
    val connectionUUID:String = uuid
    val memoryState = InMemoryState()
    val newMemoryState = memoryState.copy(connected = memoryState.connected + ( "admin" -> (connectionUUID, AdminT)), subscribers = memoryState.subscribers + connectionUUID  )

    val response = memoryState.processMessage(UserReqWrapper(connectionUUID,login("admin", "admin123")))
    assert( response._1 == memoryState)
    assert(response._2 == Seq(UserReqWrapper(connectionUUID, login_failed)))
  }
}
