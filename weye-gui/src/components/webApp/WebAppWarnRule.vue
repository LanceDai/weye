<template>
    <div class="web-app-warn-rule">
        <el-card class="card">
            <div slot="header" class="header">
                <span>告警规则列表</span>
                <el-button type="primary" @click="isWarnFormShow = true">添加告警规则</el-button>
            </div>
            <div class="content" v-if="webAppWarnRuleTable">
                <!--webAppWarnRuleTable-->
                <web-app-warn-rule-table class="web-app-warn-rule-table"
                                         :web-app-id="webAppId"></web-app-warn-rule-table>
            </div>
        </el-card>
        <!--form表单-->
        <el-dialog title="添加告警规则" :visible.sync="isWarnFormShow" width="40%">
            <el-form :model="warnRule">
                <el-form-item label="告警规则名" :label-width="formLabelWidth">
                    <el-input v-model="warnRule.name"></el-input>
                </el-form-item>
                <el-form-item label="数据源类型" :label-width="formLabelWidth">
                    <el-select v-model="warnRule.sourceType" placeholder="请选择数据源类型" style="width:100%">
                        <el-option v-for="item in sourceTypeOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--根据SourceType来显示-->
                <!--自定义命令-->
                <el-form-item label="自定义命令数据采集器" :label-width="formLabelWidth"
                              v-if="warnRule.sourceType==='command'">
                    <el-select v-model="warnRule.sourceId" placeholder="请选择采集器" @focus="getCommandOptions"
                               style="width:100%">
                        <el-option v-for="item in commandOptions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--计算数据-->
                <el-form-item label="计算规则" :label-width="formLabelWidth" v-else-if="warnRule.sourceType==='compute'">
                    <el-select v-model="warnRule.sourceId" placeholder="请选择计算规则" @focus="getWarnOptions"
                               style="width:100%">
                        <el-option v-for="item in computeOptions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="服务器数据项" :label-width="formLabelWidth"
                              v-else-if="warnRule.sourceType==='server'">
                    <el-select v-model="warnRule.compareItem" placeholder="请选择服务器采集数据数据项" @focus="getServerOptions"
                               style="width:100%">
                        <el-option v-for="item in serverOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="HTTP请求数据项" :label-width="formLabelWidth"
                              v-else-if="warnRule.sourceType==='http'">
                    <el-select v-model="warnRule.compareItem" placeholder="请选择HTTP请求采集数据数据项" @focus="getHttpOptions"
                               style="width:100%">
                        <el-option v-for="item in httpOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="比较操作" :label-width="formLabelWidth" v-if="warnRule.filterItem !== '*' ">
                    <el-select v-model="warnRule.compareOp" placeholder="请选择数据项进行比较操作"
                               style="width:100%">
                        <el-option v-for="item in compareOps" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="阈值" :label-width="formLabelWidth" v-if="warnRule.filterItem !== '*'">
                    <el-input v-model="warnRule.compareValue"></el-input>
                </el-form-item>
                <el-form-item label="告警方式" :label-width="formLabelWidth">
                    <el-select v-model="warnRule.warnWay" placeholder="请选择告警方式"
                               style="width:100%">
                        <el-option v-for="item in warnWays" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="createWarnUrlLabel()" :label-width="formLabelWidth"
                              v-if="warnRule.warnWay !== ''">
                    <el-input v-model="warnRule.warnUrl"></el-input>
                </el-form-item>
                <el-form-item label="告警内容" :label-width="formLabelWidth" v-if="warnRule.filterItem !== '*'">
                    <el-input v-model="warnRule.warnMsg" type="textarea" :rows="4"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isWarnFormShow = false">取 消</el-button>
                <el-button type="primary" @click="addWarnRule">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {postRequest, getRequest} from "../../utils/Http"
    import WebAppWarnRuleTable from "./WebAppWarnRuleTable";

    export default {
        name: "WebAppWarnRule",
        props: {
            webAppId: {
                require: true,
                type: Number
            }
        },
        components: {WebAppWarnRuleTable},
        data() {
            return {
                isWarnFormShow: false,
                webAppWarnRuleTable: true,
                warnRule: {
                    name: "",
                    sourceType: "",
                    sourceId: "",
                    compareItem: "",
                    compareOp: "",
                    compareValue: "",
                    warnWay: '',
                    warnUrl: '',
                    warnMsg: "",
                    webAppId: this.webAppId
                },
                formLabelWidth: '160px',
                commandOptions: [],
                computeOptions: [],
                serverOptions: [],
                httpOptions: [],
                sourceTypeOptions: [
                    "server",
                    "http",
                    "command",
                    "compute"
                ],
                compareOptions: [
                    "timeStamp",
                    "data"
                ], compareOps: [">", "<", "=", ">=", "<="],
                warnWays: [
                    'Email',
                    'webHook'
                ]
            }
        }, methods: {
            addWarnRule() {
                postRequest("/warnRule/", this.warnRule).then(() => {
                    this.isWarnFormShow = false
                    this.refresh()
                    this.$message({
                        type: 'info',
                        message: "告警规则列表已刷新"
                    });
                })
            }, refresh() {
                console.log("refresh")
                this.webAppWarnRuleTable = false;
                this.$nextTick(() => {
                    this.webAppWarnRuleTable = true
                })
            }, getCommandOptions() {
                getRequest("/subscribe?webAppId=" + this.webAppId).then((response) => {
                    this.commandOptions = response.data
                })
            }, getComputeOptions() {
                getRequest("/computeRule?webAppId=" + this.webAppId).then((response) => {
                    this.computeOptions = response.data
                })
            }, getServerOptions() {
                getRequest("/server/options").then(_ => {
                    this.serverOptions = _.data
                })
            }, getHttpOptions() {
                getRequest("/webApp/options").then(_ => {
                    this.httpOptions = _.data
                })
            }, createWarnUrlLabel() {
                if (this.warnRule.warnWay === 'Email') {
                    return '邮箱地址'
                } else {
                    return "WebHook链接"
                }
            }
        }
    }
</script>

<style scoped lang="scss">
    .web-app-warn-rule {
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
                .nfo {
                    width: 100%;
                }
            }
        }
    }

</style>