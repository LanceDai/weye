<template>
    <el-table class="subscribe-table" :data="unsubscribeList" v-loading="loading" :fit="true">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="命令" prop="command"></el-table-column>
        <el-table-column label="执行频率" prop="duration"></el-table-column>
        <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button size="mini" type="primary" @click="subscribe(scope.row.id)">订阅</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {getRequest, postRequest} from "../../utils/Http";

    export default {
        name: "WebAppCommandUnsubscribeTable"
        , props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                unsubscribeList: [],
                loading: true,
            }
        }, methods: {
            subscribe(id) {
                postRequest("/subscribe/", {
                    webAppId: this.webAppId,
                    customCommandId: id
                }).then(() => this.getUnSubScribeList())
            }, getUnSubScribeList() {
                this.loading = true
                getRequest("/subscribe/others/" + this.webAppId).then((response) => {
                    this.unsubscribeList = response.data
                    setTimeout(() => this.loading = false, 100)
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getUnSubScribeList()
        }
    }
</script>

<style scoped>

</style>