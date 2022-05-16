package controllers

import Models.Employee

import javax.inject._
import play.api.mvc._
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.{Json, __}

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.JsValue
import repositories.EmployeeRepository

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class EmployeeController @Inject()(implicit executionContext: ExecutionContext,
                                   val employeeRepository: EmployeeRepository,
                                   val controllerComponents: ControllerComponents) extends BaseController {
  /** creating two endpoints
   * first will return the employee list
   * second one will parse the given id and return the corresponding employee if it's found
   */



  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def findAll():Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    employeeRepository.findAll().map {
      employees => Ok(Json.toJson(employees))
    }
  }

  def findOne(id:String):Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => employeeRepository.findOne(objectId).map {
        employee => Ok(Json.toJson(employee))
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
    }
  }
  /** checking whether the JSON is valid by using the validate helper in the request body
   */

  def create():Action[JsValue] = Action.async(controllerComponents.parsers.json) { implicit request => {

    request.body.validate[Employee].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      employee =>
        employeeRepository.create(employee).map {
          _ => Created(Json.toJson(employee))
        }
    )
  }}

  def update(
              id: String):Action[JsValue]  = Action.async(controllerComponents.parsers.json) { implicit request => {
    request.body.validate[Employee].fold(
      _ => Future.successful(BadRequest("Cannot parse request body")),
      employee =>{
        val objectIdTryResult = BSONObjectID.parse(id)
        objectIdTryResult match {
          case Success(objectId) => employeeRepository.update(objectId, employee).map {
            result => Ok(Json.toJson(result.ok))
          }
          case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
        }
      }
    )
  }}

  def delete(id: String):Action[AnyContent]  = Action.async { implicit request => {
    val objectIdTryResult = BSONObjectID.parse(id)
    objectIdTryResult match {
      case Success(objectId) => employeeRepository.delete(objectId).map {
        _ => NoContent
      }
      case Failure(_) => Future.successful(BadRequest("Cannot parse the employee id"))
    }
  }}


}

