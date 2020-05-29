<template>
    <div class="detail">
        <el-row class="info">
            <server-card :server-id="serverId"></server-card>
        </el-row>
        <div class="bar"></div>
        <el-row type="flex" justify="center" class="tabs-container">
            <!--webApp-->
            <el-tabs type="border-card" class="tabs">
                <el-tab-pane label="WEB应用列表">
                    <server-web-app-info :server-id="serverId"></server-web-app-info>
                </el-tab-pane>
                <el-tab-pane label="监控图表">
                    <server-chart :server-id="serverId"
                    ></server-chart>
                </el-tab-pane>
            </el-tabs>
        </el-row>
    </div>
</template>

<script>
    import ServerCard from "./ServerCard";
    import ServerWebAppInfo from "./ServerWebAppInfo";
    import ServerChart from "./ServerChart";

    export default {
        name: "ServerDetail",
        components: {ServerChart, ServerWebAppInfo, ServerCard}
        , data() {
            return {
                webApps: [],
                serverInfo: {},
                serverId: parseInt(this.$route.params.serverId, 10)
            }
        }
        , methods: {
            handleDetail(id) {
                this.$router.push({path: "/webApp/detail/" + id})
            }, handleEdit(index, row) {
                console.log(index + "--" + row)
                console.log("id = " + row.id)
            }, handleDelete(index, row) {
                console.log(index + "--" + row)
                console.log("id = " + row.id)
            }
        }
    }
</script>

<style scoped lang="scss">

    .detail {
        .info {

        }

        .bar {
            height: 10px;
        }

        .tabs-container {
            .tabs {
                width: 100%;
            }
        }
    }
</style>