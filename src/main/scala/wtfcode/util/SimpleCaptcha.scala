package wtfcode.util

import _root_.nl.captcha._
import gimpy.BlockGimpyRenderer
import net.liftweb.http.S

/**
 * Simple captcha generator.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object SimpleCaptcha {

  val WIDTH = 250
  val HEIGHT = 75

  def getSessionCaptcha() : Captcha = {
    S.containerSession.open_!.attribute(Captcha.NAME).asInstanceOf[Captcha]
  }

  def clearSessionCaptcha() {
    S.containerSession.open_!.setAttribute(Captcha.NAME, null)
  }
}
