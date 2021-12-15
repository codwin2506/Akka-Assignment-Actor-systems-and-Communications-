package part1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import part1.CreatingActors.Ping.CreateChild

object CreatingActors extends App {

  object Ping {
    case class CreateChild(name: String)
  }

  class Ping extends Actor with ActorLogging {

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} Creating child! $name")
        val childRef = context.actorOf(Props[Pong], name) //Creating an Actor named Pong as Ping child
        childRef ! "Ping" //send message "ping" to Pong Actor
      case message => log.info(message.toString)
    }

  }

  class Pong extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
        //println(s"${self.path} $message")
        sender() ! "Pong" //sending message "pong" to Ping Actor

    }
  }

  val system = ActorSystem("PingPongActors")
  val ping = system.actorOf(Props[Ping], "Ping") //Create an Actor named Ping
  ping ! CreateChild("Pong") //starting communication

}
