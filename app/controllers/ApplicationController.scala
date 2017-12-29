package controllers

import model.{Value, ValueForm}
import play.api.mvc._
import scala.concurrent.Future
import service.PersistanceService
import scala.concurrent.ExecutionContext.Implicits.global

class ApplicationController extends Controller {

  def index = Action.async { implicit request =>
    PersistanceService.listAllValues map { values =>
      Ok(views.html.index(ValueForm.form, values))
    }
  }

  def addValue() = Action.async { implicit request =>
    ValueForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[Value]))),
      data => {
        val newValue = Value(0, data.stringValue)
        PersistanceService.addValue(newValue).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def deleteValue(id: Long) = Action.async { implicit request =>
    PersistanceService.deleteValue(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}

