package part2

  import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
  import part2.StateOfActorAndFuture.Ping.PongMessage
  import part2.StateOfActorAndFuture.Pong.PingMessage
  import part2.StateOfActorAndFuture.system.dispatcher

  import scala.concurrent.duration.Duration
  import scala.concurrent.{Await, Future}


  object StateOfActorAndFuture extends App  {

    case object StartMessage
    case class End(receivedPings: Int)

    object Ping {
      case object PongMessage
    }

    class Ping extends Actor with ActorLogging {
      val childRef: ActorRef = context.actorOf(Props[Pong], "Pong")  //Creating an Actor named Pong as Pings child
      var sum: Int = 0
      var count:Int=0

      override def receive: Receive = {
        case StartMessage => childRef ! PingMessage  //send message from Ping to Pong Actor
        case PongMessage => sum += 1; count +=1; sender() ! PingMessage  //Receive message from pong and increment its sum and counter, then reply to pong Actor again
        case End(pingSum) => println("Ping Sum: "+pingSum+" "+"counter:"+count) // Here sum is 10000 and counter is 9999 because last pong was not sent.
      }
    }

      object Pong {
        case object PingMessage
      }

      class Pong extends Actor with ActorLogging {
        var sum: Int = 0

        override def receive: Receive = {
          case PingMessage =>
            val getSum=Future{ //Here we calculate sum in Future
              sum += doWork()
              sum
            }
            Await.result(getSum,Duration.Inf)
            if(sum<10000) sender() ! PongMessage
            else if(sum==10000) sender() ! End(sum)
        }
        def doWork():Int={
          //Thread.sleep(10)
          1
        }
      }
        val system: ActorSystem = ActorSystem("PingPongActor")
        val parent: ActorRef = system.actorOf(Props[Ping], "Ping") //Create an Actor named Ping
        parent ! StartMessage //start communicating
  }
