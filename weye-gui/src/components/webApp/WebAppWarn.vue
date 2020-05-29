<template>
    <el-tabs tab-position="right" @tab-click="handleClick" v-model="activeName">
        <el-tab-pane label="告警信息" name="msg">
            <web-app-warn-msg :web-app-id="webAppId" v-if="msg"></web-app-warn-msg>
        </el-tab-pane>
        <el-tab-pane label="告警规则" name="ruleTable">
            <web-app-warn-rule :web-app-id="webAppId" v-if="ruleTable"></web-app-warn-rule>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    import WebAppWarnMsg from "./WebAppWarnMsg";
    import WebAppWarnRule from "./WebAppWarnRule";
    export default {
        name: "WebAppWarn",
        components: {WebAppWarnRule, WebAppWarnMsg}
        ,props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                msg: true,
                ruleTable: false,
                activeName: "msg"
            }
        }, methods: {
            handleClick() {
                if (this.activeName === 'msg') {
                    this.msg = true
                    this.ruleTable = false
                } else {
                    this.msg = false
                    this.ruleTable = true
                }
            }
        }
    }
</script>

<style scoped>

</style>