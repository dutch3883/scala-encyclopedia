package com.dutch.template.sangria
import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import sangria.ast.Document
import sangria.macros.derive.IncludeMethods
import sangria.macros.derive._
import sangria.parser.QueryParser
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import sangria.marshalling.sprayJson._

import scala.concurrent.Future


trait Identifiable {
  def id: String
}

class ProductRepo {
  private val Products = List(
    Product("1", "Cheesecake", "Tasty"),
    Product("2", "Health Potion", "+50 HP"))

  def product(id: String): Option[Product] =
    Products find (_.id == id)

  def products: List[Product] = Products
}


case class Picture(width: Int, height: Int, url: Option[String])

case class Product(id: String, name: String, description: String) extends Identifiable {
  def picture(size: Int): Picture =
    Picture(width = size, height = size, url = Some(s"http://cdn.com/$size/$id.jpg"))

  def promotion(amount: Int): Int = amount match {
    case 1 => 1
    case 2 => 3
    case 10 => 12
    case 20 => 23
    case _ => 0
  }
}



object Server extends App {
  import sangria.schema._
//
//  val PictureType = ObjectType(
//    "Picture",
//    "The product picture",
//
//    fields[Unit, Picture](
//      Field("width", IntType, resolve = _.value.width),
//      Field("height", IntType, resolve = _.value.height),
//      Field("url", OptionType(StringType),
//        description = Some("Picture CDN URL"),
//        resolve = _.value.url)))

  implicit val PictureType =
    deriveObjectType[Unit, Picture](
      ObjectTypeDescription("The product picture"),
      DocumentField("url", "Picture CDN URL"))


  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity that can be identified",
    fields[Unit, Identifiable](
      Field("id", StringType, resolve = context => Future { Thread.sleep(30000); context.value.id + 90 })
    )
  )


  val ProductType = deriveObjectType[Unit, Product](
      Interfaces(IdentifiableType),
      IncludeMethods("picture","promotion"))

  val Id = Argument("id", StringType)

  val QueryType = ObjectType("Query", fields[ProductRepo, Unit](
    Field("product", OptionType(ProductType),
      description = Some("Returns a product with specific `id`."),
      arguments = Id :: Nil,
      resolve = c ⇒ c.ctx.product(c arg Id)),

    Field("products", ListType(ProductType),
      description = Some("Returns a list of all available products."),
      resolve = _.ctx.products)))

  val schema = Schema(QueryType)

  implicit val system = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val route: Route =
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson ⇒
        graphQLEndpoint(requestJson)
      }
    } ~
      get {
        getFromResource("sangria/graphiql.html")
      }


  def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject): Future[(StatusCode, JsValue)] = {
    Executor.execute(schema, query, new ProductRepo, variables = vars, operationName = op)
      .map(OK → _)
      .recover {
        case error: QueryAnalysisError ⇒
          error.printStackTrace()
          BadRequest → error.resolveError
        case error: ErrorWithResolver ⇒
          error.printStackTrace()
          InternalServerError → error.resolveError
      }
  }


  def graphQLEndpoint(requestJson: JsValue): StandardRoute = {
    val JsObject(fields) = requestJson

    val JsString(query) = fields("query")

    val operation = fields.get("operationName") collect {
      case JsString(op) ⇒ op
    }

    val vars = fields.get("variables") match {
      case Some(obj: JsObject) ⇒ obj
      case _ ⇒ JsObject.empty
    }

    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        complete(executeGraphQLQuery(queryAst, operation, vars))

      // can't parse GraphQL query, return error
      case Failure(error) ⇒ {
        error.printStackTrace()
        complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
      }
    }
  }


  Http().bindAndHandle(route, "0.0.0.0", 8088)

}