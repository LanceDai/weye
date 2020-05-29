<template>
    <!--webApp-->
    <el-table
            class="monitor-rule-table"
            :data="webApps"
            v-loading="loading"
            :fit="true">
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
                label="告警信息数">
        </el-table-column>
        <el-table-column
                prop="agentStatus"
                label="Agent在线状态">
        </el-table-column>
        <el-table-column
                prop="ServerName"
                label="所在服务器"
                width="200">
            <template slot-scope="scope">
                <el-tag class="server-detail">
                    <router-link :to="{path: '/server/detail/' + scope.row.serverId}">
                        {{scope.row.serverName}}({{scope.row.serverIp}})
                    </router-link>
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
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
                        @click="handleDelete(scope.row.id)">删除
                </el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
    import {deleteRequest, getRequest} from "../../utils/Http"

    export default {
        name: "WebAppInfo"
        , data() {
            return {
                webApps: [],
                loading: true,
                timer: null
            }
        }, methods: {
            handleDetail(id) {
                this.$router.push({path: "/webApp/detail/" + id})
            }, handleEdit(index, row) {
                console.log(index + "--" + row)
            }, handleDelete(id) {
                deleteRequest("/webApp/" + id).then(() => this.getWebApps())
            }, getWebApps() {
                this.loading = true
                getRequest("/webApp/infos").then((response) => {
                    if (response.status === 200) {
                        console.log("webApps =>" + response.data)
                        this.webApps = response.data
                        setTimeout(() => this.loading = false, 100)
                    } else {
                        this.loading = false
                    }
                }).catch(() => this.loading = false)
            }
        }, mounted() {
            this.getWebApps()
            this.timer = setInterval(this.getWebApps(), 10000)
        }, beforeDestroy() {
            if (this.timer !== null) {
                clearInterval(this.timer)
            }
        }
    }
</script>

<style scoped lang="scss">

    .monitor-rule-table {
        .server-detail {
            text-decoration: none;
        }
    }
</style>