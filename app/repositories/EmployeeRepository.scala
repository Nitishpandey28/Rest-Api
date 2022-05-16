package repositories
import Models.Employee

import javax.inject._
import reactivemongo.api.bson.collection.BSONCollection
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import org.joda.time.DateTime
import reactivemongo.api.commands.WriteResult
/** EmployeeRespository injects the execution context and reactive mongo api
 * the helper function returns a Future of BSONCollection
 *
 * the "Collection" is a function to avoid potential problem in developement with play auto reloading
 *
 * the FIND method takes two argument, the selector and projector.
 * the details about FIND can be explored in the mongo documentation
 */
@Singleton
class EmployeeRepository @Inject()(
                                    implicit executionContext: ExecutionContext,
                                    reactiveMongoApi: ReactiveMongoApi
                                   ) {
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(db => db.collection("employees"))
  // Repository methods ...
  def findAll(limit: Int = 100): Future[Seq[Employee]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Employee])
        .cursor[Employee](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Employee]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Employee]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Employee]).one[Employee])
  }
  def create(employee: Employee): Future[WriteResult] = {
    collection.flatMap(_.insert(ordered = false)
      .one(employee.copy(_joiningDate = Some(new DateTime()))))
  }
  /** the insert method returns an InsertBuilder instance which we can use to insert one or more
   * documentation.
   * update method returns UpdateBuilder
   * delete method returns DeleteBuilder
   */

  def update(id: BSONObjectID, employee: Employee):Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false).one(BSONDocument("_id" -> id),
        employee.copy(
          _joiningDate = Some(new DateTime())))
    )
  }
  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }

}
