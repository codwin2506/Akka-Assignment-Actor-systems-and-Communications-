# Akka-Assignment-Actor-systems-and-Communications-

Part 1: Basic messaging and messaging back

Create an Actor named Ping
Create an Actor named Pong as Pings child (create Pong as val in Ping)
Immediately after creation - send message "ping" to Pong Actor
In Pong when "ping" is received => print (using log.info) "ping" and send "pong" to Ping
In Ping when "pong" is received => print (using log.info) "pong"

Part 2: State of actor and futures

1. Create a variable sum in both Ping and Pong Actors
2. Create a case class named End(receivedPings: Int)
3. Remove log.info from Ping and Pong
4. Send 10.000 "ping" messages:
a. On each "ping":
   i) increment the sum in Pong
   ii) send "pong" if sum < 10.000
   iii) when the sum is 10.000 - Send case class End with sum(For example ping ! End(sum))


b. On each "pong"
  i.) increment the sum in Ping

c) On each End:
i) print the sum in Ping and counter of Pong (should be 10.000 and 9999 because last pong was not sent, End was sent instead)

5. In Pong add a function that does work:

def doWork(): Int = {
       Thread sleep 1000
       1
}

6. Wrap doWork and add the result of doWork to the sum with a future (Future {sum += doWork()}) then execute 4 again. Did you get End? Ah? You didn't????? Why????
  a. List at least 2 possible solutions
  b. Implement one of the solutions and execute 4 again

7. Wrap sending "pong" in the future

8. Did you get dead letters? Why is that happening?

10. Create a case class named GetPongSum(sum : Option[Int])

Send 10000 "ping" messages
When receiving an End in Ping actor:
Send from Ping to Pong a GetPongSum(None)
In Pong send sum in GetPongSum
In ping print the sum (should be 10000)
Create a case class ThrowException()
Send ThrowException() From Ping to Pong
In Pong throw an exception (throw new Exception())
From Ping to Pong send GetPongSum(None) again
             i) The sum is not 10000, Why?             ii) Why Pong actor is active? It is got terminated, no?
