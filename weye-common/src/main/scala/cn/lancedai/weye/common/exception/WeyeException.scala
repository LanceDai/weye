package cn.lancedai.weye.common.exception

//Server
class UserLoginErrorException() extends RuntimeException("用户名或密码错误")

class UserNotLoginException() extends Exception("用户未登录")


class UnValidAgentException() extends Exception("未知的Agent")

class AgentOffLineException() extends Exception("Agent未上线， 请检查Agent运行情况")

class SQLExecuteErrorException(msg: String) extends Exception(msg)
class UnKnownSourceTypeException(sourceType:String) extends Exception(s"unknown sourceType: $sourceType")

//Agent

class NoApplicationListeningException() extends Exception("在指定的端口没有应用在监听")

class ScheduleFutureCancelFail() extends Exception("定时任务取消失败")

// Data Engine
class UnknownFilterOpException() extends Exception("未知的计算操作")

class UnSupportTypeException(clazz: Class[_]) extends Exception(s"不支持的类型 -- ${clazz.getSimpleName}")

class NoSuchItemInClassException(item: String, clazz: Class[_]) extends Exception(s"$item is not ${clazz.getName}'s field'")
