package Models
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

/** the case class contains all the attribute information of the employee
 */
case class Employee(
                     _id:Option[BSONObjectID],
                     _joiningDate: Option[DateTime],
                     name:String,
                     address:String,
                     department:String
                   )
/** companion object that contains the implicit JSON/BSON serializers
 * for JSON serializers we're using automated mapping
 * for external types we should provide their serializers as IMPLICIT just like the case for DateTime
 * for BSON we're implimenting our custom serializers
 */

object Employee{
  implicit val fmt : Format[Employee] = Json.format[Employee]
  implicit object MovieBSONReader extends BSONDocumentReader[Employee] {
    def read(doc: BSONDocument): Employee = {
      Employee(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONDateTime]("_joiningDate").map(dt => new DateTime(dt.value)),
        doc.getAs[String]("name").get,
        doc.getAs[String]("address").get,
        doc.getAs[String]("department").get)
    }
  }

  implicit object EmployeeBSONWriter extends BSONDocumentWriter[Employee] {
    def write(employee: Employee): BSONDocument = {
      BSONDocument(
        "_id" -> employee._id,
        "_joiningDate" -> employee._joiningDate.map(date => BSONDateTime(date.getMillis)),
        "name" -> employee.name,
        "address" -> employee.address,
        "department" -> employee.department

      )
    }
  }
}
