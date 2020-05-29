<template>
    <el-table class="warn-rule-table" :data="warnRules" v-loading="loading" :fit="true">
        <el-table-column prop="name" label="告警规则名"></el-table-column>
        <el-table-column prop="sourceType" label="数据源"></el-table-column>
        <el-table-column prop="compareItem" label="比较数据项"></el-table-column>
        <el-table-column prop="compareOp" label="比较操作"></el-table-column>
        <el-table-column prop="compareValue" label="阈值"></el-table-column>
        <el-table-column prop="warnWay" label="告警方式"></el-table-column>
        <el-table-column prop="warnUrl" label="告警链接"></el-table-column>
        <el-table-column prop="warnMsg" label="告警信息"></el-table-column>
        <el-table-column label="操作" width="80">
            <template slot-scope="scope">
                <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {deleteRequest, getRequest} from "../../utils/Http";

    export default {
        name: "WebAppWarnRuleTable"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                warnRules: [],
                loading: true,
            }
        }, methods: {
            handleDelete(id) {
                deleteRequest("/warnRule/" + id).then(() => this.getWarnRules())
            }, getWarnRules() {
                this.loading = true
                getRequest("/warnRule/all/" + this.webAppId).then((response) => {
                    // console.log("warnRules =>")
                    // console.log(response.data);
                    this.warnRules = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getWarnRules()
        }
    }
</script>

<style scoped>

</style>