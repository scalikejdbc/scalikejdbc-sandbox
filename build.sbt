enablePlugins(ScalikejdbcPlugin)

def Scala213 = "2.13.16"
scalaVersion := Scala213
crossScalaVersions := Seq("3.6.4", Scala213)
lazy val scalikejdbcVersion = scalikejdbc.ScalikejdbcBuildInfo.version
resolvers ++= Seq(
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)
libraryDependencies ++= Seq(
  "org.scalikejdbc"    %% "scalikejdbc"        % scalikejdbcVersion,
  "org.scalikejdbc"    %% "scalikejdbc-test"   % scalikejdbcVersion,
  "org.slf4j"          %  "slf4j-simple"       % "1.7.+",
  "org.hibernate"      %  "hibernate-core"     % "7.0.0.CR1",
  "org.hsqldb"         %  "hsqldb"             % "2.7.4"
)
initialCommands := """import scalikejdbc._
import java.time._
// loading data
Class.forName("org.hsqldb.jdbc.JDBCDriver")
ConnectionPool.singleton("jdbc:hsqldb:file:db/test", "", "")
DB localTx { implicit session =>
  try {
    sql"create table users(id bigint primary key not null, name varchar(255), company_id bigint)".execute.apply()
    sql"create table companies(id bigint primary key not null, name varchar(255))".execute.apply()
    sql"create table groups(id bigint primary key not null, name varchar(255), created_at timestamp not null)".execute.apply()
    sql"create table group_members(group_id bigint not null, user_id bigint not null, primary key(group_id, user_id))".execute.apply()
    Seq(
      insert.into(User).values(1, "Alice", null),
      insert.into(User).values(2, "Bob",   1),
      insert.into(User).values(3, "Chris", 1),
      insert.into(Company).values(1, "Typesafe"),
      insert.into(Company).values(2, "Oracle"),
      insert.into(Group).values(1, "Japan Scala Users Group", new java.util.Date()),
      insert.into(GroupMember).values(1, 1),
      insert.into(GroupMember).values(1, 2)
    ).foreach(sql => applyUpdate(sql))
  } catch { case e: Exception => e.printStackTrace }
}
GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = true, logLevel = "info")
implicit val session: DBSession = AutoSession
val (u, g, gm, c) = (User.syntax("u"), Group.syntax("g"), GroupMember.syntax("gm"), Company.syntax("c"))
"""
