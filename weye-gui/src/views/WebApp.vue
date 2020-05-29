<template>
    <div class="webapp">
        <el-card class="card">
            <div slot="header" class="header">
                <span>web应用列表</span>
                <el-button type="primary" @click="isWebAppFormShow = true">添加WEB应用</el-button>
            </div>
            <div class="content" v-if="webAppInfo">
                <!--webApp-->
                <web-app-info class="monitor-rule-table"></web-app-info>
            </div>
        </el-card>
        <!--form表单-->
        <el-dialog title="添加WEB应用" :visible.sync="isWebAppFormShow" width="30%">
            <el-form :model="webApp" :rules="rules" ref="webAppForm">
                <el-form-item label="服务器" :label-width="formLabelWidth" prop="serverId">
                    <el-select v-model="webApp.serverId" placeholder="请选择服务器" @change="validateCheck">
                        <el-option v-for="item in serverOptions" :key="item.id" :label="item.name" :value="item.id"
                                   :disabled="item.agentStatus !== 1">
                            <span style="float: left">{{ item.name}}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ agentStatusDescription(item.agentStatus) }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="web应用名" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="webApp.name" @change="validateCheck"></el-input>
                </el-form-item>
                <el-form-item label="web运行端口" :label-width="formLabelWidth" prop="port">
                    <el-input v-model.number="webApp.port" @change="validateCheck"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="cancel()">取 消</el-button>
                <el-button type="primary" @click="addWebApp()" :disabled="!valid">确 定</el-button>
            </span>
        </el-dialog>
    </div>

</template>

<script>

    import WebAppInfo from "../components/webApp/WebAppInfo";
    import {getRequest, postRequest} from "../utils/Http";

    export default {
        name: "WebApp",
        components: {WebAppInfo},
        data() {
            return {
                webAppInfo: true,
                isWebAppFormShow: false,
                formLabelWidth: '120px',
                serverOptions: [],
                webApp: {
                    serverId: null,
                    name: '',
                    port: null,

                }, timer: null,
                rules: {
                    serverId: [
                        {required: true, message: '请选择服务器', trigger: 'change'}
                    ], name: [
                        {required: true, message: 'WEB应用名不能为空'},
                    ], port: [
                        {required: true, message: 'WEB应用运行端口不能为空'},
                        {type: 'number', message: '端口必须为数字'}
                    ]
                }, valid: false
                , validTimer: null
            }
        }, methods: {
            cancel() {
                this.isWebAppFormShow = false
                this.clearWebAppModel()
            }, addWebApp() {
                postRequest("/webApp/", this.webApp).then(() => {
                    this.isWebAppFormShow = false
                    this.refresh()
                    // 触发 refresh 事件给父组件
                    this.$emit("refresh")
                    this.$message({
                        type: 'info',
                        message: "WEB应用列表已刷新"
                    });

                })
            }, refresh() {
                console.log("refresh")
                this.webAppInfo = false;
                this.$nextTick(() => {
                    this.webAppInfo = true
                })
            }, getServerOptions() {
                getRequest("/server/all").then((response) => {
                    this.serverOptions = response.data
                })
            }, clearWebAppModel() {
                this.webApp = {
                    serverId: null,
                    name: '',
                    port: null,

                }
            }, validateCheck() {
                console.log("change")
                this.$refs['webAppForm'].validate((valid) => {
                    this.valid = valid
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

    .webapp {
        .card {
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

            }
        }
    }
</style>