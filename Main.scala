import scalikejdbc._
import java.time._
import scala.collection.Seq

case class User(id: Long, name: Option[String], companyId: Option[Long] = None, company: Option[Company] = None)
object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"
  override val columns = Seq("id", "name", "company_id")
  def opt(u: SyntaxProvider[User], c: SyntaxProvider[Company])(rs: WrappedResultSet): Option[User] = opt(u.resultName, c.resultName)(rs)
  def opt(u: ResultName[User], c: ResultName[Company])(rs: WrappedResultSet): Option[User] = rs.longOpt(u.id).map(_ => apply(u, c)(rs))
  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(u.resultName)(rs)
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = User(rs.long(u.id), rs.stringOpt(u.name), rs.longOpt(u.companyId))
  def apply(u: SyntaxProvider[User], c: SyntaxProvider[Company])(rs: WrappedResultSet): User = apply(u.resultName, c.resultName)(rs)
  def apply(u: ResultName[User], c: ResultName[Company])(rs: WrappedResultSet): User = {
    (apply(u)(rs)).copy(company = rs.longOpt(c.id).map(_ => Company(c)(rs)))
  }
}
case class Company(id: Long, name: Option[String])
object Company extends SQLSyntaxSupport[Company] {
  override val tableName = "companies"
  override val columns = Seq("id", "name")
  def apply(c: SyntaxProvider[Company])(rs: WrappedResultSet): Company = apply(c.resultName)(rs)
  def apply(c: ResultName[Company])(rs: WrappedResultSet): Company = Company(rs.long(c.id), rs.stringOpt(c.name))
}
case class Group(id: Long, name: Option[String], createdAt: ZonedDateTime, members: Seq[User] = Nil)
object Group extends SQLSyntaxSupport[Group] {
  override val tableName = "groups"
  override val columns = Seq("id", "name", "created_at")
  def apply(g: SyntaxProvider[Group])(rs: WrappedResultSet): Group = apply(g.resultName)(rs)
  def apply(g: ResultName[Group])(rs: WrappedResultSet): Group = Group(rs.get(g.id), rs.get(g.name), rs.get(g.createdAt))
}
case class GroupMember(groupId: Long, userId: Long)
object GroupMember extends SQLSyntaxSupport[GroupMember] {
  override val tableName = "group_members"
  override val columns = Seq("group_id", "user_id")
}
