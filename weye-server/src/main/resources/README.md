可以二次开发的任务, 包括ShellTask, ComputeTask

创建任务模板

ShellTask需要参数

name;        任务名

icon;        图标

description; 任务描述

version;     任务版本

suitableOS;  合适的操作系统(默认Linux)

initScript;  初始化脚本(可以为空)

dataStyle;   数据格式, 用','分割, 用来指明每一项输出对应的标签

displayMode; 数据展示模式(折线图 OR 柱形图 OR ....)

// 根据 displayMode： 获取对应Schema Args
如： 图表所需
xAxisFormat; x轴数据格式, 由于是监控程序, x轴一般是时间的格式化, 所以与时间格式化通用: eg: HH:mm:SS

yAxisFormat; y轴数据格式, 利用StringFormat, eg: %f,%d, 由于会产生多个输出, 所以总的格式: {%d},{%s},{%0.2f}...
表格所需：
displayColumn： 输出列

args:        执行参数, 格式: arg1,arg2...
    command: 执行命令
    duration： 执行频率

authority;    是否公开

Compute Task 所需参数

name;        任务名

icon;        图标

description; 任务描述

dataStyle;   数据格式, 用','分割, 用来指明每一项输出对应的标签

displayMode; 数据展示模式(表格 OR 折线图 OR 柱形图 OR ....)

// 根据 displayMode： 获取对应Schema Args
如： 图表所需
xAxisFormat; x轴数据格式, 由于是监控程序, x轴一般是时间的格式化, 所以与时间格式化通用: eg: HH:mm:SS

yAxisFormat; y轴数据格式, 利用StringFormat, eg: %f,%d, 由于会产生多个输出, 所以总的格式: {%d},{%s},{%0.2f}...
表格所需：
displayColumn： 输出列

args:        执行参数, 格式: arg1,arg2...
    source:  数据来源 => eg: taskName1.arg1, taskName2.arg2...
    operation: 聚合操作 => eg: count, avg, max, min ....
    range:     聚合范围： number + unit 数目 + 时间单位（年月日 时分秒）
    duration： 计算频率： 多长时间计算一次

authority;    是否公开

实例化任务

ShellTask

server 对应一个server数据源
webApp 对应一个Http数据源
自定义的只有一个Shell数据源
shell数据源是挂靠在server下的，
但可以通过关系指定到webApp下

规则：
计算规则：是对数据源的进一步加工
理论上可以是对计算规则产生的数据在做进一步加工

监控图表分为
server监控图表， 不可自定义
web监控图表， 这里可自定义， 即选择源的数据或者计算规则产生的数据进行展示
不同的图表， 不同的参数， 即一个总chart表， 根据Type指定详细的图表参数
后端构建成Echarts config格式

