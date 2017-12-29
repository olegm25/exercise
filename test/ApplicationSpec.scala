import controllers.ApplicationController
import model.Value
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.specs2.mock.Mockito
import play.api.Application
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._
import play.libs.Json
import service.PersistanceService
import play.api.inject.bind
import scala.concurrent.Future

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends PlaySpecification with Mockito {

  implicit val persistanceServiceMock = mock[PersistanceService]
  implicit val appMock = new GuiceApplicationBuilder()
    .overrides(bind[PersistanceService].toInstance(persistanceServiceMock))
    .build

  "Application" should {

    "send 404 on a bad request" in new WithPersistanceApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithPersistanceApplication{
      //set up
      persistanceServiceMock.listAllValues returns Future.successful(Nil)

      //test
      val home = route(FakeRequest(GET, "/")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Registered Values")
    }

    "add a value" in new WithPersistanceApplication() {
      //set up
      val value = Value(0, "testvalue")
      persistanceServiceMock.addValue(value) returns Future.successful("ok")
      persistanceServiceMock.listAllValues returns Future.successful(value :: Nil)

      //test
      val result = route(FakeRequest().withBody(Json.toJson(value)), "/add").get
      val resultAsString = contentAsString(result)
      resultAsString must contain ("testvalue")
    }

    /*    TODO: Missing tests for :
          viewing all values,
          deleting values,
          deleting non existing values,
          database connection failures,
          adding values that are too large
          ...*/
  }

}

class WithPersistanceApplication(implicit persistanceService: PersistanceService, app: Application)
  extends WithApplication(app) {

  val employeeController = new ApplicationController(persistanceService)
}