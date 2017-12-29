package service

import com.google.inject.ImplementedBy
import model.{DbValuesRepo, Value}

import scala.concurrent.Future

@ImplementedBy(classOf[DbPersistanceService])
trait PersistanceService {

  def addValue(value: Value): Future[String]

  def deleteValue(id: Long): Future[Int]

  def getValue(id: Long): Future[Option[Value]]

  def listAllValues: Future[Seq[Value]]
}

class DbPersistanceService extends PersistanceService {

  def addValue(value: Value): Future[String] = {
    DbValuesRepo.add(value)
  }

  def deleteValue(id: Long): Future[Int] = {
    DbValuesRepo.delete(id)
  }

  def getValue(id: Long): Future[Option[Value]] = {
    DbValuesRepo.get(id)
  }

  def listAllValues: Future[Seq[Value]] = {
    DbValuesRepo.listAll
  }
}
