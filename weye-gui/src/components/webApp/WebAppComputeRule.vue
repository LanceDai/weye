<template>
    <div class="web-app-compute-rule">
        <el-card class="card">
            <div slot="header" class="header">
                <span>计算规则列表</span>
                <el-button type="primary" @click="isComputeFormShow = true">添加计算规则</el-button>
            </div>
            <div class="content" v-if="webAppComputeRuleTable">
                <!--webAppComputeRuleTable-->
                <web-app-compute-rule-table class="web-app-compute-rule-table"
                                            :web-app-id="webAppId"></web-app-compute-rule-table>
            </div>
        </el-card>
        <!--form表单-->
        <el-dialog title="添加计算规则" :visible.sync="isComputeFormShow" width="40%">
            <el-form :model="computeRule">
                <el-form-item label="计算规则名" :label-width="formLabelWidth">
                    <el-input v-model="computeRule.name"></el-input>
                </el-form-item>
                <el-form-item label="数据源类型" :label-width="formLabelWidth">
                    <el-select v-model="computeRule.sourceType" placeholder="请选择数据源类型" style="width:100%">
                        <el-option v-for="item in sourceTypeOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--根据SourceType来显示-->
                <!--自定义命令-->
                <el-form-item label="自定义命令数据采集器" :label-width="formLabelWidth"
                              v-if="computeRule.sourceType==='command'">
                    <el-select v-model="computeRule.sourceId" placeholder="请选择采集器" @focus="getCommandOptions"
                               style="width:100%">
                        <el-option v-for="item in commandOptions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--计算数据-->
                <el-form-item label="计算规则" :label-width="formLabelWidth" v-else-if="computeRule.sourceType==='compute'">
                    <el-select v-model="computeRule.sourceId" placeholder="请选择计算规则" @focus="getComputeOptions"
                               style="width:100%">
                        <el-option v-for="item in computeOptions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="服务器数据项" :label-width="formLabelWidth"
                              v-else-if="computeRule.sourceType==='server'">
                    <el-select v-model="computeRule.item" placeholder="请选择服务器采集数据数据项" @focus="getServerOptions"
                               style="width:100%">
                        <el-option v-for="item in serverOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="HTTP请求数据项" :label-width="formLabelWidth"
                              v-else-if="computeRule.sourceType==='http'">
                    <el-select v-model="computeRule.item" placeholder="请选择HTTP请求采集数据数据项" @focus="getHttpOptions"
                               style="width:100%">
                        <el-option v-for="item in httpOptions" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="过滤项" :label-width="formLabelWidth"
                              v-if="computeRule.sourceType==='command' || computeRule.sourceType === 'compute'">
                    <el-select v-model="computeRule.filterItem" placeholder="请选择采集数据过滤项"
                               style="width:100%">
                        <el-option v-for="item in filterOptions" :key="item" :label="item" :value="item">
                        </el-option>
                        <el-option label="不过滤" value="*"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="过滤项" :label-width="formLabelWidth"
                              v-else-if="computeRule.sourceType==='server'">
                    <el-select v-model="computeRule.filterItem" placeholder="请选择采集数据过滤项" @focus="getServerOptions"
                               style="width:100%">
                        <el-option v-for="item in serverOptions" :key="item" :label="item" :value="item">
                        </el-option>
                        <el-option label="不过滤" value="*"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="过滤项" :label-width="formLabelWidth"
                              v-else-if="computeRule.sourceType==='http'">
                    <el-select v-model="computeRule.filterItem" placeholder="请选择采集数据过滤项" @focus="getHttpOptions"
                               style="width:100%">
                        <el-option v-for="item in httpOptions" :key="item" :label="item" :value="item">
                        </el-option>
                        <el-option label="不过滤" value="*"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="过滤操作" :label-width="formLabelWidth" v-if="computeRule.filterItem !== '*' ">
                    <el-select v-model="computeRule.filterOp" placeholder="请选择采集数据过滤操作"
                               style="width:100%">
                        <el-option v-for="item in filterOps" :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="过滤值" :label-width="formLabelWidth" v-if="computeRule.filterItem !== '*'">
                    <el-input v-model="computeRule.filterValue"></el-input>
                </el-form-item>
                <el-form-item label="聚合操作" :label-width="formLabelWidth">
                    <el-select v-model="computeRule.op" placeholder="请选择数据聚合操作"
                               style="width:100%">
                        <el-option v-for="item in ops" :key="item.name" :label="item.label" :value="item.name">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="时间范围" :label-width="formLabelWidth">
                    <el-input v-model="computeRule.time"></el-input>
                </el-form-item>
                <el-form-item label="时间单位" :label-width="formLabelWidth">
                    <el-select v-model="computeRule.timeUnit" placeholder="请选择时间单位"
                               style="width:100%">
                        <el-option v-for="item in timeUnit" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isComputeFormShow = false">取 消</el-button>
                <el-button type="primary" @click="addComputeRule">确 定</el-button>
            </span>
        </el-dialog>
    </div>

</template>

<script>
    import WebAppComputeRuleTable from "./WebAppComputeRuleTable";
    import {postRequest, getRequest} from "../../utils/Http";

    export default {
        name: "WebAppComputeRule"
        , components: {WebAppComputeRuleTable},
        props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                isComputeFormShow: false,
                webAppComputeRuleTable: true,
                computeRule: {
                    name: "",
                    sourceType: "",
                    sourceId: "",
                    item: "",
                    filterItem: "*",
                    filterOp: "",
                    filterValue: "",
                    op: "",
                    time: "",
                    timeUnit: "",
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
                filterOptions: [
                    "timeStamp",
                    "data"
                ], filterOps: [">", "<", "=", ">=", "<="],
                ops: [
                    {
                        label: "计数",
                        name: "count"
                    },
                    {
                        label: "平均值",
                        name: "avg"
                    },
                    {
                        label: "最小值",
                        name: "min"
                    },
                    {
                        label: "最大值",
                        name: "max"
                    },
                    {
                        label: "求和",
                        name: "sum"
                    },
                ], timeUnit: [
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
            addComputeRule() {
                postRequest("/computeRule/", this.computeRule).then(() => {
                    this.isComputeFormShow = false
                    this.refresh()
                    this.$message({
                        type: 'info',
                        message: "计算规则列表已刷新"
                    });
                })
            }, refresh() {
                console.log("refresh")
                this.webAppComputeRuleTable = false;
                this.$nextTick(() => {
                    this.webAppComputeRuleTable = true
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