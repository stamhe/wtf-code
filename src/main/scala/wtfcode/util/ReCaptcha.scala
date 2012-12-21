package wtfcode.util

import net.liftweb.util.Props
import java.net.NoRouteToHostException
import net.liftweb.common.Logger
import net.tanesha.recaptcha.ReCaptchaException

/**
 * Copy-pasted from lift wiki
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object ReCaptcha extends Logger {

  import net.liftweb.common.{Box, Empty, Full, Failure}
  import net.liftweb.util.{FieldError, FieldIdentifier}
  import net.liftweb.http.S
  import net.tanesha.recaptcha.ReCaptchaFactory
  import net.liftweb.json.JsonDSL._
  import net.liftweb.http.js.JsCmd
  import net.liftweb.http.js.JE.JsFunc


  // add ReCaptcha
  /**
   * Define the public key to used to connect to reCapcha service
   */
  protected def reCaptchaPublicKey: String = Props.get("recaptcha.publicKey").open_!

  /**
   * Define the private key to used to connect to reCapcha service
   */
  protected def reCaptchaPrivateKey: String = Props.get("recaptcha.privateKey").open_!

  /**
   * Define the option to configure reCaptcha widget.
   *
   * @see http://code.google.com/apis/recaptcha/docs/customization.html to have the list possible customization
   * @return the javascript option map (as JObject)
   */
  protected def reCaptchaOptions = ("theme" -> "white") ~ ("lang" -> S.containerRequest.flatMap(_.locale).map(_.getLanguage).getOrElse("en"))

  private lazy val reCaptcha = ReCaptchaFactory.newReCaptcha(reCaptchaPublicKey, reCaptchaPrivateKey, false)

  def captchaXhtml() = {
    import scala.xml.Unparsed
    import net.liftweb.json.JsonAST._
    import net.liftweb.json.Printer._

    val RecaptchaOptions = compact(render(reCaptchaOptions))
    <xml:group>
      <script>
        var RecaptchaOptions = {Unparsed(RecaptchaOptions)};
      </script>
      <script type="text/javascript" src={"http://api.recaptcha.net/challenge?k=" + reCaptchaPublicKey}></script>
    </xml:group>
  }

  def validateCaptcha(): List[FieldError] = {
    def checkAnswer(remoteAddr: String, challenge: String, response: String): Box[String] = {
      try {
        val reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, response)
        reCaptchaResponse.isValid match {
          case true => Empty
          case false => Full(reCaptchaResponse.getErrorMessage)
        }
      } catch {
        case e: ReCaptchaException => {
          warn("Can not validate ReCaptcha: wrong server config", e)
          Empty
        }
        case e => throw e
      }
    }
    val res = for (
      remoteAddr <- S.containerRequest.map(_.remoteAddress);
      challenge <- S.param("recaptcha_challenge_field");
      response <- S.param("recaptcha_response_field");
      b <- checkAnswer(remoteAddr, challenge, response)
    ) yield b

    res match {
      case Failure(msg, _, _) => List(FieldError(FakeFieldIdentifier(Full("reCaptcha")), msg))
      case Full(msg) => List(FieldError(FakeFieldIdentifier(Full("reCaptcha")), msg))
      case Empty => Nil
    }
  }

  /**
   * to load a new Captcha with ajax
   * @return JsCmd
   */
  def reloadCaptcha(): JsCmd = JsFunc("Recaptcha.reload").cmd

  case class FakeFieldIdentifier(override val uniqueFieldId: Box[String]) extends FieldIdentifier

}
