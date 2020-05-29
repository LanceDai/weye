<template>

    <!--webApp-->
    <el-table :data="webAppInfos" v-loading="loading">
        <el-table-column
                prop="name"
                label="web应用名">
        </el-table-column>
        <el-table-column
                prop="port"
                label="运行端口">
        </el-table-column>
        <el-table-column
                prop="warnMsgNum"
                label="异常信息">
        </el-table-column>
        <el-table-column
                prop="agentStatus"
                label="Agent在线状态">
        </el-table-column>
        <el-table-column label="操作" width="300">
            <template slot-scope="scope">
                <el-button
                        size="mini"
                        type="primary"
                        @click="handleDetail( scope.row.id)">查看详情

                </el-button>
                <el-button
                        size="mini"
                        @click="handleEdit(scope.$index, scope.row)">编辑
                </el-button>
                <el-button
                        size="mini"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)">删除
                </el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {getRequest} from "../../utils/Http";

    export default {
        name: "ServerWebAppInfo",
        props: {
            serverId: {
                type: Number,
                require: true
            }
        }, data() {
            return {
                webAppInfos: [],
                loading: true
            }
        }, methods: {
            handleDetail(id) {
                this.$router.push({path: "/webApp/detail/" + id})
            }, handleEdit(index, row) {
                console.log(index + "--" + row)
            }, handleDelete(index, row) {
                console.log(index + "--" + row)
            }, getWebAppInfoByServerId() {
                getRequest("/server/webApps/" + this.serverId).then((response) => {
                    this.webAppInfos = response.data
                    setTimeout(() => this.loading = false, 100)
                })
            }
        }, mounted() {
            this.getWebAppInfoByServerId()
        }
    }
</script>

<style scoped>

</style>