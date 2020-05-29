<template>
    <el-tabs tab-position="right" @tab-click="handleClick" v-model="activeName">
        <el-tab-pane label="计算数据" name="data">
            <web-app-compute-data :web-app-id="webAppId" v-if="data"></web-app-compute-data>
        </el-tab-pane>
        <el-tab-pane label="计算规则" name="ruleTable">
            <web-app-compute-rule :web-app-id="webAppId" v-if="ruleTable"></web-app-compute-rule>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    import WebAppComputeData from "./WebAppComputeData";
    import WebAppComputeRule from "./WebAppComputeRule";
    export default {
        name: "WebAppCompute"
        , components: {WebAppComputeRule, WebAppComputeData},
        props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                data: true,
                ruleTable: false,
                activeName: "data"
            }
        }, methods: {
            handleClick() {
                if (this.activeName === 'data') {
                    this.data = true
                    this.ruleTable = false
                } else {
                    this.data = false
                    this.ruleTable = true
                }
            }
        }
    }
</script>

<style scoped>

</style>