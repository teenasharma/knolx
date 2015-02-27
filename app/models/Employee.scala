package models
import scala.slick.driver.PostgresDriver.simple._

import java.util.Date
import java.text.SimpleDateFormat

import org.joda.time.LocalDateTime

case class User(name: String, address: String, email: String, company: String, phone: String, password: String, userType: Int, created: Date, updateDate: Date, id: Int = 0)

object Employee {

  implicit lazy val util2sqlDateMapper = MappedColumnType.base[java.util.Date, java.sql.Timestamp](
    { utilDate => new java.sql.Timestamp(utilDate.getTime()) },
    { sqlDate => new java.util.Date(sqlDate.getTime()) })

  class Employees(tag: Tag) extends Table[User](tag, "usertable") {
    def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull)
    def address: Column[String] = column[String]("address")
    def email: Column[String] = column[String]("email", O.NotNull)
    def company: Column[String] = column[String]("company", O.NotNull)
    def phone: Column[String] = column[String]("phone")
    def password: Column[String] = column[String]("password", O.NotNull)
    def userType: Column[Int] = column[Int]("userType", O.NotNull)
    def created: Column[Date] = column[Date]("created", O.NotNull)
    def updateDate: Column[Date] = column[Date]("updateDate", O.NotNull)
    def * = (name, address, email, company, phone, password, userType, created, updateDate, id) <> (User.tupled, User.unapply)
    def idx = index("idx_a", (email), unique = true)

  }

  val userTable = TableQuery[Employees]

  val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")

  /**
   * This method return list of the users from user table.
   */
  def getList: List[User] = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")

    dbObject.withSession {
      implicit session =>
        //userTable.ddl.create
        val userList = userTable.list
        userList
    }

  }

  /**
   *  This method insert new user into user table.
   * @param userInfo
   * @return
   */
  def insert(userInfo: User) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")

    dbObject.withSession {
      implicit session =>
        // userTable.ddl.create
        userTable.insert(userInfo)
    }
  }

  /**
   * This method update the user details of the matching columns.
   * @param id
   * @param user
   * @return
   */
  def update(id: Int, user: User) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")
    dbObject.withSession {
      implicit session =>
        userTable.filter(_.id === id).update(new User(user.name, user.address, user.email, user.company, user.phone, user.password, user.userType, user.created, user.updateDate, id))
    }
  }

  /**
   * This method authenticates the user by matching the email and password.
   * @param email
   * @param password
   * @return
   */
  def authenticate(email: String, password: String) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")
    dbObject.withSession {
      implicit session =>
        userTable.filter(_.email === email).firstOption
        userTable.filter(_.password === password).firstOption
    }
  }

  /**
   *  This method delete the user of given id from the database.
   * @param Id
   * @return
   */
  def delete(Id: Int) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")
    dbObject.withSession {
      implicit session =>
        userTable.filter(_.id === Id).delete
    }
  }

  /**
   * This method find the user by id.
   * @param id
   * @return
   */
  def findById(id: Int) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")
    dbObject.withSession {
      implicit session =>
        userTable.filter(_.id === id).firstOption
    }
  }

  /**
   * This method find the user by email.
   * @param Email
   * @return
   */
  def findByEmail(Email: String) = {
    val userTable = TableQuery[Employees]

    val dbObject = Database.forURL(url = "jdbc:postgresql://localhost:5432/playknolx", user = "postgres", password = "postgres", driver = "org.postgresql.Driver")
    dbObject.withSession {
      implicit session =>
        userTable.filter(_.email === Email).firstOption
    }
  }

}
