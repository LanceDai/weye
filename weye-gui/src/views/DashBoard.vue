<template>
    <div class="dash-board">
        <el-row :gutter="20" class="info-row">
            <el-col :span="5" class="left-card">
                <icon-card>
                    <el-avatar slot="icon" class="success-icon icon" style="background-color: green">正常</el-avatar>
                    {{successNum}}
                </icon-card>
            </el-col>
            <el-col :span="5">
                <icon-card>
                    <el-avatar slot="icon" class="warn-icon icon" style="background-color: red">异常</el-avatar>
                    {{warnNum}}
                </icon-card>
            </el-col>
            <el-col :span="5">
                <icon-card>
                    <el-avatar slot="icon" class="warn-msg-num-icon icon" style="background-color: red">警告个数</el-avatar>
                    {{warnNum}}
                </icon-card>
            </el-col>
            <el-col :span="9" class="right-card">
                <icon-card>
                    <scroll-message
                            :messages="warnMessages"></scroll-message>
                </icon-card>
            </el-col>
        </el-row>
        <!--webapp card-->
        <el-row :gutter="20" class="web-app-row">
            <!-- webApp-->
            <web-app @refresh="refresh"></web-app>
        </el-row>
    </div>
</template>
<script>
    import ScrollMessage from "../components/base/ScrollMessage"
    import IconCard from "../components/base/IconCard";
    import {getRequest} from "../utils/Http";
    import WebApp from "./WebApp";

    export default {
        name: "DashBoard",
        components: {WebApp, IconCard, ScrollMessage},
        data() {
            return {
                successNum: 0,
                warnNum: 0,
                warnMsgNum: 0,
                warnMessages: [],
                webApps: [],
                timer: null
            }
        }, methods: {
            getSuccessNum() {
                getRequest("/webApp/success").then((response) => {
                    this.successNum = response.data
                })
            }, getWarnNum() {
                getRequest("/webApp/warn").then((response) => {
                    this.warnNum = response.data
                })
            }, getWarnMessages() {
                getRequest("/webApp/allWarnMsg").then((response) => {
                    const data = response.data
                    this.warnMsgNum = data.size
                    this.warnMessages = data
                })
            }, refresh() {
                this.getSuccessNum()
                this.getWarnNum()
                this.getWarnMessages()
            }
        }, mounted() {
            // 加载初始数据
            this.refresh()
            // 定时更新
            this.timer = setInterval(() => this.refresh(), 3000)
        }, beforeDestroy() {
            clearInterval(this.timer)
        }
    }
</script>

<style scoped lang="scss">
    .el-row {
        margin-bottom: 10px;
    }

    .dash-board {
        width: 100%;
        height: 100%;

        .left-card {
            padding-left: 0;
        }

        .right-card {
            padding-right: 0;
        }

        .info-row {

        }

        .web-app-row {
            padding-right: 10px;
            padding-left: 10px;

            .web-app-info-table {
            }
        }
    }
</style>