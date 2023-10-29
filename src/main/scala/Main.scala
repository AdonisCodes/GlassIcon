// akka
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
// akka-http
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContext, Await}
import scala.util.{Failure, Success}

object Main {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem(Behaviors.empty, "Glassicon")

    val route = get {
      path("test") {
        val random = scala.util.Random
        val randomInt = random.nextInt(100)

        complete(s"Hello World! $randomInt")
      }
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    val serverBinding = bindingFuture.map { binding =>
      println(
        s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/"
      )
      binding
    }

    serverBinding.onComplete {
      case Success(binding) =>
        sys.addShutdownHook {
          bindingFuture
            .flatMap(_.unbind())
            .onComplete(_ => actorSystem.terminate())
        }
      case Failure(ex) =>
        println(s"Server could not start: ${ex.getMessage}")
        actorSystem.terminate()
    }

    Await.result(
      actorSystem.whenTerminated,
      scala.concurrent.duration.Duration.Inf
    )
  }

}
