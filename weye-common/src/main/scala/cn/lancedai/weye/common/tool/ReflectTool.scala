package cn.lancedai.weye.common.tool

import java.lang.reflect.Field

object ReflectTool {

  implicit class PlusField(field: Field) {
    def withAccessible(flag: Boolean = true): Field = {
      field.setAccessible(flag)
      field
    }
  }

}
