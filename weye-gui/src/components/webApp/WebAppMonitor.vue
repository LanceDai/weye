<template>
    <el-tabs tab-position="right" @tab-click="handleClick" v-model="activeName">
        <el-tab-pane label="监控图表" name="chart">
            <web-app-monitor-chart :web-app-id="webAppId" v-if="chart"></web-app-monitor-chart>
        </el-tab-pane>
        <el-tab-pane label="监控规则" name="ruleTable">
            <web-app-monitor-rule :web-app-id="webAppId" v-if="ruleTable"></web-app-monitor-rule>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    import WebAppMonitorRule from "./WebAppMonitorRule";
    import WebAppMonitorChart from "./WebAppMonitorChart";

    export default {
        name: "WebAppMonitor",
        components: {WebAppMonitorChart, WebAppMonitorRule},
        props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                chart: true,
                ruleTable: false,
                activeName: "chart"
            }
        }, methods: {
            handleClick() {
                if (this.activeName === 'chart') {
                    this.chart = true
                    this.ruleTable = false
                } else {
                    this.chart = false
                    this.ruleTable = true
                }
            }
        }
    }
</script>

<style scoped>

</style>