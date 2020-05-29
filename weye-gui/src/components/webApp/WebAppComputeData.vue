<template>
    <el-table class="compute-rule-data" :data="computeDataList" v-loading="loading" :fit="true">
        <el-table-column prop="timestamp" label="时间戳"></el-table-column>
        <el-table-column prop="data" label="计算结果"></el-table-column>
        <el-table-column prop="computeRuleName" label="计算规则名"></el-table-column>
    </el-table>
</template>

<script>
    import {getRequest} from "../../utils/Http";

    export default {
        name: "WebAppComputeData"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                computeDataList: [],
                loading: true
            }
        }, methods: {
            getComputeDataList() {
                this.loading = true
                getRequest("/computeRule/record/all/" + this.webAppId).then((response) => {
                    // console.log("monitorRules =>")
                    // console.log(response.data);
                    this.computeDataList = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getComputeDataList()
        }
    }
</script>

<style scoped>

</style>