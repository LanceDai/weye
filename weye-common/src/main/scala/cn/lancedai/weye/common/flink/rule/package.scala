package cn.lancedai.weye.common.flink

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import javax.sql.DataSource

package object rule {
  type JDouble = java.lang.Double

  def  getMySQLDataSource(username:String,password:String,dbUrl:String):DataSource = {
    val ds: MysqlDataSource = new MysqlDataSource()
    ds.setURL(dbUrl)
    ds.setUser(username)
    ds.setPassword(password)
    ds
  }
}
