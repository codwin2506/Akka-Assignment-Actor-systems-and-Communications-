package part3

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import part3.PingPongSum.Ping.PongMessage
import part3.PingPongSum.Pong.PingMessage
import part3.PingPongSum.system.dispatcher
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object PingPongSum extends App{

  case object StartMessage
  case class End(receivedPings: Int)
  case class GetPongSum(sum:Option[Int])
  case class ThrowException()

  object Ping {
    case object PongMessage
  }

  class Ping extends Actor with ActorLogging {
    //println(s"${self.path} Creating new child Actor" )
    val childRef: ActorRef = context.actorOf(Props[Pong], "Pong")   //Creating an Actor named Pong as Pings child
    var sum: Int = 0
    var count:Int=0;

    override def receive: Receive = {
      case StartMessage => childRef ! PingMessage   //send message from Ping to Pong Actor
      case PongMessage => sum += 1; count +=1; sender() ! PingMessage   //Receive message from pong and increment its sum and counter, then reply to pong Actor again
      case End(pingSum) => println("Ping Sum: "+pingSum+" "+"counter:"+count); sender() ! GetPongSum(None)     // Here sum is 10000 and counter is 9999 because last pong was not sent.
      case GetPongSum(pongSum) => println(pongSum); sender() ! ThrowException()   //Getting Pong sum and Print it. After this throw an Exception
       // parent ! GetPongSum
    }
  }

  object Pong {
    case object PingMessage
  }

  class Pong extends Actor with ActorLogging {
    var sum: Int = 0

    override def receive: Receive = {
      case PingMessage =>
        val getSum=Future{ //Calculating sum in future
          sum += doWork()
          sum
        }
        Await.result(getSum,Duration.Inf)
        if(sum<10000) sender() ! PongMessage
        else if(sum==10000) sender() ! End(sum)

      case GetPongSum(None) =>
        val newSum: Option[Int]=Some(sum)
        sender() ! GetPongSum(newSum)
      case ThrowException() =>  //Throwing an exception
        throw new Exception("New Exception Occurred!")
    }
    def doWork():Int={  //Calculating work
      //Thread.sleep(10)
      1
    }
  }
  val system: ActorSystem = ActorSystem("PingPongActor")
  val parent: ActorRef = system.actorOf(Props[Ping], "Ping")
  parent ! StartMessage
  Thread.sleep(1000)
 // parent ! GetPongSum(None) //sending from ping to pong GetPongSum(None) again
                            //we got None because sum again initializes to zero.
                            // Also this does’t kill it. Instead it will be restarted again.

}
