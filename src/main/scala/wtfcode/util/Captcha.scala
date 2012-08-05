package wtfcode.util

import nl.captcha._
import gimpy.BlockGimpyRenderer

/**
 * Simple captcha generator.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object SimpleCaptcha {

  val WIDTH = 250
  val HEIGHT = 75

  def apply() : Captcha = {
    new Captcha.Builder(WIDTH, HEIGHT).addText().gimp(new BlockGimpyRenderer()).build()
  }
}
