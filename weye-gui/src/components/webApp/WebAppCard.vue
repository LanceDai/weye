<template>
    <el-card class="web-app-card">
        <div slot="header" class="header">
            <span>{{webApp.name}}</span>
        </div>
        <div class="content">
            <div class="content-item">运行端口:
                <el-tag>{{webApp.port}}</el-tag>
            </div>
            <div class="content-item">Agent状态:
                <el-tag v-if="webApp.agentStatus===0" type="warning" effect="dark">离线</el-tag>
                <el-tag v-else-if="webApp.agentStatus===1" type="success" effect="dark">在线</el-tag>
                <el-tag v-else-if="webApp.agentStatus===3" effect="dark">未初始化</el-tag>
            </div>
            <div class="content-item">未处理的异常信息数:
                <el-tag>{{webApp.warnMsgNum}}</el-tag>
            </div>
            <div class="content-item">服务器IP
                <el-tag> {{webApp.serverIp}}</el-tag>
            </div>
            <div class="content-item">服务器名
                <el-tag>{{webApp.serverName}}</el-tag>
            </div>
        </div>
    </el-card>
</template>

<script>
    import {getRequest} from "../../utils/Http";

    export default {
        name: "WebAppCard",
        props: {
            webAppId: {
                type: Number,
                require: true
            }
        }, data() {
            return {
                webApp: {},
            }
        }, methods: {
            detail() {
                getRequest("/webApp/detail/" + this.webAppId).then((response) => {
                    this.webApp = response.data
                    console.log(this.webApp)
                })
            }
        }, created() {
            this.detail()
        }
    }
</script>

<style scoped lang="scss">
    .web-app-card {
        width: 100%;
        text-align: left;

        .header {
            font-family: Alegreya, monospace;
            font-size: 25px;
            font-weight: bold;
        }

        .content {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
            margin-bottom: -10px;

            .content-item {
                width: 400px;
                flex-basis: calc(50% - 20px);
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 10px;
            }
        }
    }
</style>