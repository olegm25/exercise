package model

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Value(id: Long, stringValue: String)

case class ValueFormData(stringValue: String)

object ValueForm {

  val form = Form(
    mapping(
      "stringValue" -> nonEmptyText
    )(ValueFormData.apply)(ValueFormData.unapply)
  )
}

class ValueTableDef(tag: Tag) extends Table[Value](tag, "persisted_values") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def stringValue = column[String]("string_value")

  override def * =
    (id, stringValue) <>(Value.tupled, Value.unapply)
}

object DbValuesRepo {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val values = TableQuery[ValueTableDef]

  def add(value: Value): Future[String] = {
    dbConfig.db.run(values += value).map(res => "Value successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(values.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Value]] = {
    dbConfig.db.run(values.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Value]] = {
    dbConfig.db.run(values.result)
  }

}
