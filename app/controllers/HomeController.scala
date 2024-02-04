package controllers

import com.google.inject.Singleton
import org.jsoup.Jsoup
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Random

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    // Fetch XKCD archive page
    val xkcdArchiveUrl = "https://xkcd.com/archive/"
    val xkcdArchivePage = Jsoup.connect(xkcdArchiveUrl).get()

    // Extract all links to archived comics
    val comicLinks = xkcdArchivePage.select("#middleContainer a[href^='/']")

    // Take a random link
    val randomLink = if (comicLinks.size() > 0) {
      val randomIndex = Random.nextInt(comicLinks.size())
      val randomComicLink = comicLinks.get(randomIndex).attr("href")
      s"https://xkcd.com$randomComicLink"
    } else {
      // Default link if no comics found
      "https://xkcd.com/"
    }

    // Render the random link on the index page
    Ok(views.html.index(randomLink))
  }

}