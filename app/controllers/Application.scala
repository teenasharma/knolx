package controllers

import java.util.Date
import scala.slick.driver.PostgresDriver.simple._
import models.Employee
import models.User
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.db.slick.DBAction
import play.api.db.slick.dbSessionRequestAsSession
import play.api.db.slick.dbSessionRequestAsSession
import play.api.mvc._
import play.api.mvc.Flash

case class Login(email: String, password: String)

object Application extends Controller {

  /**
   * Describe the User form.
   */
  private val userForm: Form[User] = Form(
    mapping(
      "name" -> nonEmptyText,
      "address" -> text,
      "email" -> nonEmptyText,
      "company" -> nonEmptyText,
      "phone" -> text,
      "password" -> nonEmptyText,
      "userType" -> ignored(2),
      "createDate" -> ignored(new Date),
      "updateDate" -> ignored(new Date),
      "id" -> ignored(0))(User.apply)(User.unapply))

  /**
   * Describe the Login form.
   */

  val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText) verifying (result => result match {
        case (email, password) => Employee.authenticate(email, password).isDefined
      }))

  /**
   * Handle default path requests, redirect to Login page
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle the 'new User form' Authentication.
   */
  def authenticate = Action { implicit request =>
    val newUserForm = loginForm.bindFromRequest()
    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.Application.login()).flashing("error" -> "Please enter valid email and password")
      },
      success = { newUserForm =>
        val user = Employee.findByEmail(newUserForm._1)
        Ok(views.html.userProfile(user.get)).withSession("email" -> user.get.email)

      })
  }

  
  /**
   * This result directly redirect to the User Sign up Form.
   */
  def employee = Action { implicit request =>
    Ok(views.html.employee(""))
  }

  /**
   * Handle the 'new User form' submission.
   */
  def userFormSubmit = Action { implicit request =>
    val newUserForm = userForm.bindFromRequest()
    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.Application.employee()).flashing("errorBlank" -> "Please Provide your details")

      },
      success = { newUserForm =>
        val user = Employee.findByEmail(newUserForm.email)
        if (user.isDefined) {
          Redirect(routes.Application.employee()).flashing("error" -> "Duplicate email ")
        } else {
          Employee.insert(newUserForm)
          Redirect(routes.Application.employee()).flashing("success" -> "Member Added Successfully")
        }

      })
  }

  /**
 * render page from user home to another page like view profile etc.
 * @param id, user id
 * @return typ, type of page
 */
def userHome(id: Int, typ: String) = Action { implicit request =>
    typ match {
      case "userpro" => {
        val s = Employee.findById(id)
        Ok(views.html.userProfile(s.get))
      }
      case "ep" => Redirect(routes.Application.editUser(id))
    }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the User to edit
   */
  def update(id: Int) = DBAction { implicit request =>
    userForm.bindFromRequest.fold(
      hasErrors = { form =>
        Ok("error")
      },
      success = { user =>
        Employee.update(id, user)
        val result = Employee.findById(id)
        Ok(views.html.userProfile(result.get)).flashing("success" -> "Profile Successfully updated")
      })
  }

  /**
   * Display the 'edit form' of a existing User.
   *
   * @param id Id of the User to edit
   */
  def editUser(id: Int) = DBAction { implicit request =>
    Employee.findById(id).map {
      user =>
        Ok(views.html.editForm(id, userForm.fill(user)))
    }.getOrElse(NotFound)
  }

}