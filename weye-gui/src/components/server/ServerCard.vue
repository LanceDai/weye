<template>
    <el-card class="server-card">
        <div slot="header" class="header">
            <span>{{server.name}}</span>
        </div>
        <div class="content">
            <div class="content-item">IP地址:
                <el-tag>{{server.ip}}</el-tag>
            </div>
            <div class="content-item">Agent状态:
                <el-tag v-if="server.agentStatus===0" type="warning" effect="dark">离线</el-tag>
                <el-tag v-else-if="server.agentStatus===1" type="success" effect="dark">在线</el-tag>
                <el-tag v-else-if="server.agentStatus===3" effect="dark">未初始化</el-tag>
            </div>
            <div class="content-item">API_KEY:
                <el-tag>{{server.apiKey}}</el-tag>
            </div>
            <div class="content-item">CPU:
                <el-tag> {{server.processorInfo}}</el-tag>
            </div>
            <div class="content-item">内存:
                <el-tag>{{server.totalMem}}</el-tag>
            </div>
            <div class="content-item">操作系统类型:
                <el-tag>{{server.systemFamily}}</el-tag>
            </div>
            <div class="content-item">操作系统版本:
                <el-tag>{{server.systemVersion}}</el-tag>
            </div>
            <div class="content-item">操作系统架构:
                <el-tag>{{server.bitness}}位</el-tag>
            </div>
        </div>
    </el-card>
</template>

<script>
    import {getRequest} from "../../utils/Http";

    export default {
        name: "ServerCard",
        props: {
            serverId: {
                type: Number,
                require: true
            }
        }, data() {
            return {
                server: {},

            }
        }, methods: {
            detail() {
                getRequest("/server/detail/" + this.serverId).then((response) => {
                    this.server = response.data
                    // console.log("server detail")
                    // console.log(this.server)
                })
            }
        }, created() {
            this.detail()
        }
    }
</script>

<style scoped lang="scss">
    .server-card {
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