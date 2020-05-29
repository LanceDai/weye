<template>
    <div class="server">
        <el-card class="card">
            <div slot="header" class="header">
                <span>服务器列表</span>
                <el-button type="primary" @click="isServerFormShow = true">添加服务器</el-button>
            </div>
            <div class="content" v-if="serverInfo">
                <server-info class="server-info"></server-info>
            </div>
        </el-card>

        <!--form表单-->
        <el-dialog title="添加服务器" :visible.sync="isServerFormShow" width="30%">
            <el-form :model="server">
                <el-form-item label="服务器名字" :label-width="formLabelWidth">
                    <el-input v-model="server.name" placeholder="默认等于IP"></el-input>
                </el-form-item>
                <el-form-item label="服务器IP" :label-width="formLabelWidth">
                    <el-input v-model="server.ip"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isServerFormShow = false">取 消</el-button>
                <el-button type="primary" @click="addServer()">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>

    import ServerInfo from "../components/server/ServerInfo";
    import {postRequest} from "../utils/Http";

    export default {
        name: "Server",
        components: {ServerInfo},
        data() {
            return {
                isServerFormShow: false,
                serverInfo: true,
                server: {ip: "", name: ""},
                formLabelWidth: '120px'
            }
        }, methods: {
            addServer() {
                postRequest("/server/", this.server).then((response) => {
                    const data = response.data
                    const msg = `
                    <p>请下载并安装Agent至此服务器，</p>
                    <p>修改config/config.properites):</p>
                    <p>api.key: ${data['apiKey']}</p>
                    <p>process.center.host: ${data['processCenterHost']}</p>`
                    this.$alert(msg, '提示', {
                        dangerouslyUseHTMLString: true,
                        confirmButtonText: '确定',
                        callback: () => {
                            this.isServerFormShow = false
                            this.refresh()
                            this.$message({
                                type: 'info',
                                message: "服务器列表已刷新"
                            });
                        }
                    });
                })
            }, refresh() {
                console.log("refresh")
                this.serverInfo = false;
                this.$nextTick(() => {
                    this.serverInfo = true
                })
            }
        }
    }
</script>

<style scoped lang="scss">
    .server {
        .card {
            margin: 20px;
            height: 100%;

            .header {
                text-align: left;
                display: flex;
                justify-content: center;
                align-items: center;

                span {
                    flex: 1;
                }
            }

            .content {
                .server-info {
                    width: 100%;
                }
            }
        }
    }
</style>