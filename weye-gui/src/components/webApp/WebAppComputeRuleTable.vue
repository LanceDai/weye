<template>
    <el-table class="compute-rule-table" :data="computeRules" v-loading="loading" :fit="true">
        <el-table-column prop="name" label="计算规则名"></el-table-column>
        <el-table-column prop="sourceType" label="数据源"></el-table-column>
        <el-table-column prop="item" label="数据项"></el-table-column>
        <el-table-column prop="filterItem" label="过滤数据项"></el-table-column>
        <el-table-column prop="filterOp" label="过滤操作"></el-table-column>
        <el-table-column prop="filterValue" label="过滤值"></el-table-column>
        <el-table-column prop="op" label="聚合操作"></el-table-column>
        <el-table-column prop="time" label="时间范围"></el-table-column>
        <el-table-column prop="timeUnit" label="时间单位"></el-table-column>
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
        name: "WebAppComputeRuleTable"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                computeRules: [],
                loading: true,
            }
        }, methods: {
            handleDelete(id) {
                deleteRequest("/computeRule/" + id).then(() => this.getComputeRules())
            }, getComputeRules() {
                this.loading = true
                getRequest("/computeRule/all/" + this.webAppId).then((response) => {
                    // console.log("computeRules =>")
                    // console.log(response.data);
                    this.computeRules = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getComputeRules()
        }
    }
</script>

<style scoped>

</style>