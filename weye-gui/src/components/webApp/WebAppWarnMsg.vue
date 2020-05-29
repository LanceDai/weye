<template>
    <el-table class="warn-rule-msg" :data="warnMsgs" v-loading="loading" :fit="true">
        <el-table-column prop="timestamp" label="发生时间"></el-table-column>
        <el-table-column prop="msg" label="告警信息"></el-table-column>
        <el-table-column prop="warnRuleName" label="告警规则名"></el-table-column>
        <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="handleWarnMsg(scope.row.id)">已处理</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {getRequest, postRequest} from "../../utils/Http";

    export default {
        name: "WebAppWarnMsg"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                warnMsgs: [],
                loading: true,
                timer: null
            }
        }, methods: {
            getWarnMsgs() {
                this.loading = true
                getRequest("/warnRule/record/all/" + this.webAppId).then((response) => {
                    // console.log("monitorRules =>")
                    // console.log(response.data);
                    this.warnMsgs = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }, handleWarnMsg(id) {
                postRequest("/warnRule/record/handle/" + id).then(() => this.getWarnMsgs())
            }
        }, mounted() {
            this.getWarnMsgs()
            this.timer = setInterval(() => this.getWarnMsgs(), 10000)
        }, beforeDestroy() {
            clearInterval(this.timer)
        }
    }
</script>

<style scoped>

</style>