package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import net.liftweb.db.{DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.mapper.{Schemifier, DB}
import wtfcode.model._
import net.liftweb.sitemap.Loc.{If, Hidden}
import wtfcode.util.WTFDateTimeConverter
import net.liftweb.sitemap.Loc.If

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // where to search snippet
    LiftRules.addToPackages("wtfcode")

    // I18n resources
    LiftRules.resourceNames = "i18n/messages" :: LiftRules.resourceNames

    Schemifier.schemify(true, Schemifier.infoF _,
      Language, User, Post, PostVote, Comment, CommentVote, Bookmark, LastSeen)

    if (Language.count == 0) {
      for (langName <- List("C", "C++", "Java", "PHP", "Python", "Scala"))
        Language.create.name(langName).save()
    }

    LiftRules.statelessRewrite.prepend(NamedPF("PrettyUrlRewriter") {
      case RewriteRequest(ParsePath("code" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse("code" :: Nil, Map("id" -> id))
      case RewriteRequest(ParsePath("lang" :: name :: Nil, _, _, _), _, _) =>
        RewriteResponse("lang-filter" :: Nil, Map("lang" -> name))
      case RewriteRequest(ParsePath("user" :: nick :: Nil, _, _, _), _, _) =>
      RewriteResponse("user" :: Nil, Map("nick" -> nick))
    })

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu(S ? "menu.home") / "index" :: // Simple menu form
        Menu(S ? "menu.post") / "post" ::
        Menu(S ? "menu.browse") / "browse" ::
        Menu(S ? "menu.feed") / "feed" ::
        Menu(Loc("Bookmarks", "bookmarks" :: Nil, S ? "menu.bookmarks", If(() => User.loggedIn_?, () => RedirectResponse("/user_mgt/login")))) ::
        Menu(Loc("Code", List("code") -> true, S ? "menu.code", Hidden)) ::
        Menu(Loc("Lang", List("lang-filter") -> true, S ? "menu.lang", Hidden)) ::
        Menu(Loc("User", List("user") -> true, S ? "menu.user", Hidden)) ::
        // Menu entries for the User management stuff
      User.sitemap :_*)

    LiftRules.setSiteMapFunc(sitemap)

    LiftRules.dateTimeConverter.default.set(() => WTFDateTimeConverter)
  }
}

