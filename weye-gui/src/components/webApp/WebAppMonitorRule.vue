<template>
    <div class="web-app-compute-rule">
        <el-card class="card">
            <div slot="header" class="header">
                <span>监控规则列表</span>
                <el-button type="primary" @click="isMonitorFormShow = true">添加监控规则</el-button>
            </div>
            <div class="content" v-if="webAppMonitorRuleTable">
                <!--webAppMonitorRuleTable-->
                <web-app-monitor-rule-table class="web-app-monitor-rule-table"
                                            :web-app-id="webAppId"></web-app-monitor-rule-table>
            </div>
        </el-card>
        <!--form表单-->
        <el-dialog title="添加监控规则" :visible.sync="isMonitorFormShow" width="40%">
            <el-form :model="monitorRule">
                <el-form-item label="监控规则名" :label-width="formLabelWidth">
                    <el-input v-model="monitorRule.name"></el-input>
                </el-form-item>
                <el-form-item label="数据源类型" :label-width="formLabelWidth">
                    <el-select v-model="monitorRule.sourceType" placeholder="请选择数据源类型" style="width:100%">
                        <el-option v-for="item in sourceTypeOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--根据SourceType来显示-->
                <!--自定义命令-->
                <el-form-item label="自定义命令数据采集器" :label-width="formLabelWidth"
                              v-if="monitorRule.sourceType==='command'">
                    <el-select v-model="monitorRule.sourceId" placeholder="请选择采集器" @focus="getCommandOptions"
                               style="width:100%">
                        <el-option v-for="item in commandOptions" :key="item.subscribeId" :label="item.name" :value="item.subscribeId">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--计算数据-->
                <el-form-item label="计算规则" :label-width="formLabelWidth" v-else-if="monitorRule.sourceType==='compute'">
                    <el-select v-model="monitorRule.sourceId" placeholder="请选择计算规则" @focus="getComputeOptions"
                               style="width:100%">
                        <el-option v-for="item in computeOptions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="服务器数据项" :label-width="formLabelWidth"
                              v-else-if="monitorRule.sourceType==='server'">
                    <el-select v-model="monitorRule.item" placeholder="请选择服务器采集数据数据项" @focus="getServerOptions"
                               style="width:100%">
                        <el-option v-for="item in serverOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="HTTP请求数据项" :label-width="formLabelWidth"
                              v-else-if="monitorRule.sourceType==='http'">
                    <el-select v-model="monitorRule.item" placeholder="请选择HTTP请求采集数据数据项" @focus="getHttpOptions"
                               style="width:100%">
                        <el-option v-for="item in httpOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="输出数据格式" :label-width="formLabelWidth">
                    <el-input v-model="monitorRule.outputDataName"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isMonitorFormShow = false">取 消</el-button>
                <el-button type="primary" @click="addMonitorRule">确 定</el-button>
            </span>
        </el-dialog>
    </div>

</template>

<script>
    import WebAppMonitorRuleTable from "./WebAppMonitorRuleTable";
    import {postRequest, getRequest} from "../../utils/Http";

    export default {
        name: "WebAppMonitorRule",
        components: {WebAppMonitorRuleTable}
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                isMonitorFormShow: false,
                webAppMonitorRuleTable: true,
                monitorRule: {
                    name: "",
                    sourceType: "",
                    sourceId: "",
                    item: "",
                    chartType: "",
                    outputDataName: "",
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
            }
        }, methods: {
            addMonitorRule() {
                postRequest("/monitorRule/", this.monitorRule).then(() => {
                    this.isMonitorFormShow = false
                    this.refresh()
                    this.$message({
                        type: 'info',
                        message: "监控规则列表已刷新"
                    });
                })
            }, refresh() {
                console.log("refresh")
                this.webAppMonitorRuleTable = false;
                this.$nextTick(() => {
                    this.webAppMonitorRuleTable = true
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
            }
        }
    }
</script>

<style scoped lang="scss">
    .web-app-compute-rule {
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