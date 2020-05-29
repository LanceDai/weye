<template>
    <div class="custom-command">
        <el-card class="card">
            <div slot="header" class="header">
                <span>自定义命令列表</span>
                <el-button-group>
                    <el-button type="primary" @click="isCustomCommandFormShow = true">添加自定义命令</el-button>
                    <el-button type="primary" @click="handleTest">测试
                        <svg-icon slot="suffix" icon-name="icon-debug" class="debug el-icon--right"></svg-icon>
                    </el-button>
                </el-button-group>

            </div>
            <div class="content" v-if="customCommandInfo">
                <custom-command-info class="custom-command-info"></custom-command-info>
            </div>
        </el-card>
        <!--form表单-->
        <el-dialog title="添加自定义命令" :visible.sync="isCustomCommandFormShow" width="30%">
            <el-form :model="customCommand">
                <el-form-item label="名字" :label-width="formLabelWidth">
                    <el-input v-model="customCommand.name" placeholder="请尽量概括命令的功能"></el-input>
                </el-form-item>
                <el-form-item label="命令" :label-width="formLabelWidth">
                    <el-input v-model="customCommand.command" placeholder="用来执行的命令"></el-input>
                </el-form-item>
                <el-form-item label="执行频率" :label-width="formLabelWidth">
                    <el-input v-model="customCommand.duration" placeholder="命令执行的频率"></el-input>
                </el-form-item>
                <el-form-item label="时间单位" :label-width="formLabelWidth">
                    <el-select v-model="customCommand.timeUnit" placeholder="请选择时间单位"
                               style="width:100%">
                        <el-option v-for="item in timeUnit" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isCustomCommandFormShow = false">取 消</el-button>
                <el-button type="primary" @click="addCustomCommand()">确 定</el-button>
            </span>
        </el-dialog>
        <!--form表单, 用来测试-->
        <el-dialog title="测试自定义命令" :visible.sync="isCustomCommandTestFormShow" width="70%" class="test-dialog" center>
            <el-form class="test-content">
                <el-form-item label="执行服务器" :label-width="testFormLabelWidth">
                    <el-select v-model="testServerId" placeholder="请选择服务器" class="test-op-item">
                        <el-option v-for="item in serverOptions" :key="item.id" :label="item.name"
                                   :value="item.id" :disabled="item.agentStatus !== 1">
                            <span style="float: left">{{ item.name }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ agentStatusDescription(item.agentStatus) }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="执行命令" :label-width="testFormLabelWidth">
                    <el-input v-model="testCommand" class="test-op-item"></el-input>
                </el-form-item>
                <el-form-item label="执行结果" :label-width="testFormLabelWidth">

                    <el-input type="textarea" v-model="testRes" disabled autosize></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="footer">
                <el-button type="primary" @click="test">测试</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {postRequest, getRequest, postRequestSync} from "../utils/Http";
    import CustomCommandInfo from "../components/customCommand/customCommandInfo";
    import SvgIcon from "../components/base/SvgIcon";
    import {isEmpty} from "../utils/Common";

    export default {
        name: "CustomCommand"
        ,
        components: {CustomCommandInfo, SvgIcon},
        data() {
            return {
                customCommandInfo: true,
                isCustomCommandFormShow: false,
                isCustomCommandTestFormShow: false,
                testServerId: '',
                testCommand: '',
                testRes: '',
                testFormLabelWidth: '200px',
                serverOptions: {},
                customCommand: {name: '', command: '', duration: null, timeUnit: 's'},
                formLabelWidth: '120px',
                timeUnit: [
                    {
                        label: "小时",
                        value: "h"
                    }, {
                        label: "分钟",
                        value: "min"
                    }, {
                        label: "秒",
                        value: "s"
                    }, {
                        label: "毫秒",
                        value: "ms"
                    },
                ]
            }
        }, methods: {
            addCustomCommand() {
                this.customCommand.duration = this.customCommand.duration + this.customCommand.timeUnit
                postRequest("/customCommand/", this.customCommand).then(() => {
                    // const data = response.data
                    this.isCustomCommandFormShow = false
                    this.refresh()
                    this.$message({
                        type: 'info',
                        message: "自定义命令列表已刷新"
                    });
                })
            }, refresh() {
                console.log("refresh")
                this.customCommandInfo = false;
                this.$nextTick(() => {
                    this.customCommandInfo = true
                })
            }, handleTest() {
                this.isCustomCommandTestFormShow = true;
            }, test() {
                if (isEmpty(this.testServerId)) {
                    this.$message("请选择服务器")
                } else if (isEmpty(this.testCommand)) {
                    this.$message("请输入执行命令")
                } else {
                    const data = new FormData
                    data.append('serverId', this.testServerId)
                    data.append('command', this.testCommand)
                    postRequestSync('/customCommand/test', data)
                        .then((response) => {
                            const data = response.data
                            // this.$message("测试结果为：" + data)
                            this.testRes = data
                        })
                }
            }, getServerOptions() {
                getRequest("/server/all").then((response) => {
                    this.serverOptions = response.data
                })
            }, agentStatusDescription(agentStatus) {
                if (agentStatus === 0) return "Agent离线"
                else if (agentStatus === 1) return "Agent在线"
                else if (agentStatus === 2) return "Agent未初始化"
                else return "未知状态"
            }
        }, mounted() {
            this.getServerOptions()
        }
    }
</script>

<style scoped lang="scss">
    .custom-command {
        .debug {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100%;
            width: 30px;
            cursor: pointer;
        }

        .card {
            margin: 20px;
            height: 100%;

            .header {
                text-align: left;
                display: flex;
                justify-content: center;
                align-items: center;

                span {
                    flex: 1;
                }
            }

            .content {
                .server-info {
                    width: 100%;
                }
            }
        }
    }
</style>