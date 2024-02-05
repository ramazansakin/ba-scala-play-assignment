package controllers

import com.google.inject.Singleton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc._
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala

import java.nio.file.{Files, Paths}
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Random

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {

  def index() = Action { _ =>
    val xkcdBaseUrl = "https://xkcd.com"
    val xkcdArchiveUrl = xkcdBaseUrl + "/archive/"
    val localFilePath = "public/pages/xkcd_archive.html"

    // Try to fetch XKCD archive page online
    val xkcdArchivePage: Document = try {
      Jsoup.connect(xkcdArchiveUrl).get()
    } catch {
      case _: Exception =>
        // If online fetch fails, read from local file with a base URI
        val htmlContent = new String(Files.readAllBytes(Paths.get(localFilePath)))
        Jsoup.parse(htmlContent, xkcdArchiveUrl)
    }

    // Extract all links to archived comics
    val comicLinks = extractComicLinks(xkcdArchivePage)

    // Take a random link
    val randomLink = if (comicLinks.nonEmpty) {
      val randomIndex = Random.nextInt(comicLinks.size)
      val randomComicLink = comicLinks(randomIndex)
      randomComicLink
    } else {
      // Default link if no comics found
      xkcdBaseUrl
    }

    Ok(views.html.index(randomLink))
  }

  // Extract comic links from the document
  private def extractComicLinks(doc: Document): List[String] = {
    val links = doc.select("a[href][title]")
    if (links.isEmpty) {
      List.empty
    } else {
      links.map(link => link.absUrl("href")).toList
    }
  }

}