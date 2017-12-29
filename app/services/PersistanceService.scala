package service

import model.{Value, Values}
import scala.concurrent.Future

object PersistanceService {

  def addValue(value: Value): Future[String] = {
    Values.add(value)
  }

  def deleteValue(id: Long): Future[Int] = {
    Values.delete(id)
  }

  def getValue(id: Long): Future[Option[Value]] = {
    Values.get(id)
  }

  def listAllValues: Future[Seq[Value]] = {
    Values.listAll
  }
}
