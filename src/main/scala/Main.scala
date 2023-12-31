import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

object Main {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def main(args: Array[String]): Unit = {
    // This needs typing, it will use the type of this type: Behaviors.empty
    implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Glassicon")

    // Define a route for serving the "docs.html" file from the "static" directory
    val staticFilesRoute: Route = pathPrefix("static") {
      getFromResourceDirectory("static")
    }

    val homepageRoute: Route = pathEndOrSingleSlash {
      getFromResource("static/index.html")
    }

    val docsRoute: Route = path("docs") {
      getFromResource("static/docs.html")
    }

    val route = get {
      path("greetings") {
        val random = scala.util.Random
        val randomInt = random.nextInt(100)

        complete(s"Hello World! $randomInt")
      }
    } ~ staticFilesRoute ~ homepageRoute ~ docsRoute

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    val serverBinding = bindingFuture.map { binding =>
      println(
        s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/"
      )
      binding
    }

    serverBinding.onComplete {
      case Success(_) =>
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
