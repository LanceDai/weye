<template>
    <el-table class="subscribe-table" :data="subscribeList" v-loading="loading" :fit="true">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="命令" prop="command"></el-table-column>
        <el-table-column label="执行频率" prop="duration"></el-table-column>
        <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button size="mini" type="danger" @click="cancelSubscribe(scope.row.subscribeId)">取消订阅</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {deleteRequest, getRequest} from "../../utils/Http";

    export default {
        name: "WebAppCommandSubscribeTable"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                subscribeList: [],
                loading: true,
            }
        }, methods: {
            cancelSubscribe(id) {
                deleteRequest("/subscribe/" + id).then(() => this.getSubScribeList())
            }, getSubScribeList() {
                this.loading = true
                getRequest("/subscribe/all/" + this.webAppId).then((response) => {
                    this.subscribeList = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getSubScribeList()
        }
    }
</script>

<style scoped>

</style>