<template>
    <div class="detail">
        <el-row class="info">
            <web-app-card :web-app-id="webAppId"></web-app-card>
        </el-row>
        <div class="bar"></div>
        <el-row type="flex" justify="center" class="tabs-container">
            <!--webApp-->
            <el-tabs type="border-card" class="tabs">
                <el-tab-pane label="监控">
                    <web-app-monitor :web-app-id="webAppId"></web-app-monitor>
                </el-tab-pane>
                <el-tab-pane label="计算">
                    <web-app-compute :web-app-id="webAppId"></web-app-compute>
                </el-tab-pane>
                <el-tab-pane label="告警">
                    <web-app-warn :web-app-id="webAppId"></web-app-warn>
                </el-tab-pane>
                <el-tab-pane label="数据采集器">
                    <web-app-custom-command :web-app-id="webAppId"></web-app-custom-command>
                </el-tab-pane>
            </el-tabs>
        </el-row>
    </div>
</template>

<script>
    import WebAppCard from "./WebAppCard";
    import WebAppMonitor from "./WebAppMonitor";
    import WebAppCompute from "./WebAppCompute";
    import WebAppWarn from "./WebAppWarn";
    import WebAppCustomCommand from "./WebAppCustomCommand";

    export default {
        name: "WebAppDetail",
        components: {
            WebAppCustomCommand, WebAppWarn, WebAppCompute, WebAppMonitor, WebAppCard
        },
        data() {
            return {
                webAppId: parseInt(this.$route.params.webAppId, 10)
            }
        }
        , methods: {
            handleDetail(id) {
                this.$router.push({path: "/webApp/detail/" + id})
            }, handleEdit(index, row) {
                console.log(index + "--" + row)
            }, handleDelete(index, row) {
                console.log(index + "--" + row)
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