package controllers

import javax.inject.Inject

import model.{Value, ValueForm}
import play.api.Logger
import play.api.mvc._

import scala.concurrent.Future
import service.PersistanceService

import scala.concurrent.ExecutionContext.Implicits.global

class ApplicationController@Inject()(persistanceService: PersistanceService) extends Controller {
  val logger = Logger(this.getClass())

  def index = Action.async { implicit request =>
    logger.info("Listing all values")
    persistanceService.listAllValues map { values =>
      Ok(views.html.index(ValueForm.form, values))
    }
  }

  def addValue() = Action.async { implicit request =>
    ValueForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[Value]))),
      data => {
        logger.info(s"Adding value: ${data.stringValue}")
        val newValue = Value(0, data.stringValue)
        persistanceService.addValue(newValue).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def deleteValue(id: Long) = Action.async { implicit request =>
    logger.info(s"Deleting value for $id")
    persistanceService.deleteValue(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}

