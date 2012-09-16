package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.db.{DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.mapper.{By, Schemifier, DB}
import net.liftweb.sitemap.Loc._

import wtfcode.model._
import wtfcode.api._
import wtfcode.atom.AtomDispatcher
import wtfcode.util.WtfDateTimeConverter


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    DefaultConnectionIdentifier.jndiName = "java:jboss/datasources/PostgreSQLDS"
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
      Language, User, Post, PostVote, Comment, CommentVote, Bookmark, LastSeen, Notification, Tag, PostTags,
      ExtSession)

    if (Language.count == 0) {
      for (name <- List("C", "C++", "C#", "Java", "JavaScript", "PHP", "Python", "Scala"))
        Language.create.name(name).code(Language.mangleName(name)).save()
    }

    //fucking migration
    if (Comment.count(By(Comment.deleted, true)) == 0)
      Comment.findAll().foreach(comment => comment.deleted(false).save)
    //another fucking migration
    Comment.findAll(By(Comment.deleted, true)).foreach(comment =>
      if (comment.deletedAt.is == null) comment.deletedAt(Helpers.now).save)
    //yet another fucking migration
    if (Post.count(By(Post.deleted, true)) == 0)
      Post.findAll().foreach(post => post.deleted(false).save)
    //guess what?
    User.findAll().map(user => if (user.nickNameLower.is == null) user.nickNameLower(user.nickName.toLowerCase).save)

    List("php-dates", "lab", "boolshit").foreach(Tag.findOrCreate(_).save())

    LiftRules.earlyInStateful.append(ExtSession.testCookieEarlyInStateful)

    LiftRules.dispatch.prepend(AtomDispatcher.dispatch)

    LiftRules.statelessDispatchTable.prepend(CommentService).prepend(CodeService)

    LiftRules.statelessRewrite.prepend(NamedPF("PrettyUrlRewriter") {
      case RewriteRequest(ParsePath("code" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse("code" :: Nil, Map("id" -> id))
      case RewriteRequest(ParsePath("lang" :: name :: Nil, _, _, _), _, _) =>
        RewriteResponse("lang-filter" :: Nil, Map("lang" -> name))
      case RewriteRequest(ParsePath("user" :: nick :: Nil, _, _, _), _, _) =>
      RewriteResponse("user" :: Nil, Map("nick" -> nick))
    })

    def notificationsMessage() = {
      val unreadCount = User.currentUser.map(_.notifications.count(!_.read)).openOr(0)
      val unread = unreadCount match {
        case 0 => ""
        case i => " (" + i + ")"
      }
      S ? "menu.notifications" + unread
    }

    def helloMessage() = {
      val usr = User.currentUser
      usr.map { u => S ? ("user.hello", u.nickName.is) }.openOr(S ? "user.notLoggedIn")
    }

    val redirectIfNotLoggedIn = If(() => User.loggedIn_?, () => RedirectResponse(User.loginPageURL))

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu(S ? "menu.home") / "index",
      Menu(Loc("Add", "add" :: Nil, S ? "menu.add", redirectIfNotLoggedIn)),
      Menu(S ? "menu.browse") / "browse",
      Menu(S ? "menu.feed") / "feed",
      Menu(Loc("Bookmarks", "bookmarks" :: Nil, S ? "menu.bookmarks", redirectIfNotLoggedIn)),
      Menu(Loc("Notifications", "notifications" :: Nil, notificationsMessage(), redirectIfNotLoggedIn)),
      Menu(Loc("Code", List("code") -> true, S ? "menu.code", Hidden)),
      Menu(Loc("Lang", List("lang-filter") -> true, S ? "menu.lang", Hidden)),
      Menu(Loc("User", List("user") -> true, S ? "menu.user", Hidden)),
      // Menu entries for the User management stuff
      Menu(helloMessage()) / "user-management" submenus ( User.menus ))

    LiftRules.setSiteMapFunc(sitemap)

    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.dateTimeConverter.default.set(() => WtfDateTimeConverter)
  }
}

