import akka.actor.Actor
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpRequest
import java.net.URLEncoder
import akka.http.scaladsl.Http
import scala.concurrent.Future
import akka.http.scaladsl.model.HttpResponse
import scala.concurrent.duration._

object AkkaHttp {

    implicit val system = ActorSystem() // Akka Actor
    implicit val materializer = ActorMaterializer // Akka Stream
    import system.dispatcher

    val source =
        """" 
            object SimpleApp  {
                val aField = 2
                def aMethod(x: Int) = x + 1 

                def main(args: Array[String]): Unit = println(aField)
            }
        """.stripMargin

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://markup.su/api/highlighter",
      entity = HttpEntity( // payload
        ContentTypes.`application/x-www-form-urlencoded`, // application/json
        s"source=${URLEncoder.encode(source, "UTF-8")}&language=Scala&theme=Sunburst" // the actual data we want to send
      )
    )

    def sendRequest(): Future[String] = {
        val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
        val entityFuture: Future[HttpEntity.Strict] =
            responseFuture.flatMap(response =>
                response.entity.toStrict(2.seconds)
            )
        entityFuture.map(entity => entity.data.utf8String)
    }

    def main(args: Array[String]): Unit = {
        sendRequest().foreach(println)
    }
}
