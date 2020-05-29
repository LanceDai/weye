<template>
    <el-table class="monitor-rule-table" :data="monitorRules" v-loading="loading" :fit="true">
        <el-table-column prop="name" label="监控规则名"></el-table-column>
        <el-table-column prop="sourceType" label="数据源"></el-table-column>
        <el-table-column prop="item" label="数据项"></el-table-column>
        <el-table-column prop="outputDataName" label="输出数据格式"></el-table-column>
        <el-table-column label="操作" width="240">
            <template slot-scope="scope">
                <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>

    import {deleteRequest, getRequest} from "../../utils/Http";

    export default {
        name: "WebAppMonitorRuleTable"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                monitorRules: [],
                loading: true,
            }
        }, methods: {
            handleDelete(id) {
                deleteRequest("/monitorRule/" + id).then(() => this.getMonitorRules())
            }, getMonitorRules() {
                this.loading = true
                getRequest("/monitorRule/all/" + this.webAppId).then((response) => {
                    // console.log("monitorRules =>")
                    // console.log(response.data);
                    this.monitorRules = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getMonitorRules()
        }
    }
</script>

<style scoped>

</style>