<template>
    <div>
        <el-table class="server-info" :data="servers" v-loading="loading" :fit="true">
            <el-table-column label="服务器名称">
                <template slot-scope="scope">
                    <div class="box">
                        <svg-icon icon-name="icon-fuwuqi" class="icon server"></svg-icon>
                        <span style="margin-left: 10px">{{ scope.row.name }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="服务器IP" prop="ip"></el-table-column>
            <el-table-column label="Agent状态">
                <template slot-scope="scope">
                    <div class="box" v-if="scope.row.agentStatus === 0">
                        <svg-icon icon-name="icon-warn-f" class="icon unknown"></svg-icon>
                        <span style="margin-left: 10px"> Agent已下线</span>
                    </div>
                    <div class="box" v-else-if="scope.row.agentStatus === 1">
                        <svg-icon icon-name="icon-zhengchang" class="icon unknown"></svg-icon>
                        <span style="margin-left: 10px"> Agent在线</span>
                    </div>
                    <div class="box" v-else-if="scope.row.agentStatus === 3">
                        <svg-icon icon-name="icon-question" class="icon unknown"></svg-icon>
                        <span style="margin-left: 10px"> 请初始化Agent</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" min-width="90">
                <template slot-scope="scope">
                        <el-button
                                size="mini"
                                type="primary"
                                @click="handleDetail( scope.row.id)">查看详情

                        </el-button>
                        <el-button
                                size="mini"
                                @click="handleEdit(scope.$index)">编辑
                        </el-button>
                        <el-button
                                size="mini"
                                type="danger"
                                @click="handleDelete(scope.row.id)">删除
                        </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--form表单, 用来编辑-->
        <el-dialog title="编辑服务器配置" :visible.sync="isServerEditFormShow" width="30%">
            <el-form :model="editServer">
                <el-form-item label="服务器名字" :label-width="formLabelWidth">
                    <el-input v-model="editServer.name" placeholder="默认等于IP"></el-input>
                </el-form-item>
                <el-form-item label="服务器IP" :label-width="formLabelWidth" disabled>
                    <el-input v-model="editServer.ip"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isServerEditFormShow = false">取 消</el-button>
                <el-button type="primary" @click="handleEditServer()">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {deleteRequest, getRequest, putRequest} from "../../utils/Http";
    import SvgIcon from "../base/SvgIcon";

    export default {
        name: "ServerInfo"
        ,
        components: {SvgIcon},
        data() {
            return {
                servers: [],
                loading: true,
                timer: null,
                isServerEditFormShow: false,
                editServer: {},
                formLabelWidth: '120px'
            }
        }, methods: {
            handleDetail(id) {
                this.$router.push({path: "/server/detail/" + id})
            }, handleEdit(index) {
                this.isServerEditFormShow = true
                this.editServer = this.servers[index]
            }, handleDelete(id) {
                this.loading = true
                deleteRequest("/server/" + id).then(() => this.getServers())
            }, getServers() {
                getRequest("/server/all").then((response) => {
                    const data = response.data
                    console.log("servers =" + data)
                    this.servers = data
                    setTimeout(() => this.loading = false, 100)

                })
            }, handleEditServer(){
                putRequest("/server", this.editServer).then(() => {
                    this.getServers()
                    this.isServerEditFormShow = false
                })
            }
        }, mounted() {
            this.getServers()
            this.timer = setInterval(() => this.getServers(), 10000)
        }, beforeDestroy() {
            if (this.timer !== null) {
                clearInterval(this.timer)
            }
        }
    }
</script>

<style scoped lang="scss">
    .server-info {
        .box {
            width: 70%;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .server {
                height: 50px;
                width: 50px;
            }

            .warn {
                height: 50px;
                width: 50px;
                color: red;
            }

            .normal {
                height: 50px;
                width: 50px;
                color: green;
            }

            .unknown {
                height: 50px;
                width: 50px;
                color: #dede43;
            }
        }


    }
</style>