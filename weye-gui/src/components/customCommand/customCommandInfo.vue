<template>
    <div>
        <el-table class="custom-command-info" :data="customCommands" v-loading="loading" :fit="true">
            <el-table-column label="名称" prop="name"></el-table-column>
            <el-table-column label="命令" prop="command"></el-table-column>
            <el-table-column label="执行频率" prop="duration"></el-table-column>
            <el-table-column label="操作" min-width="90">
                <template slot-scope="scope">
                    <el-button size="mini" type="primary" @click="handleEdit(scope.$index)">编辑</el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!--form表单, 用来编辑-->
        <el-dialog title="编辑自定义命令配置" :visible.sync="isCustomCommandEditFormShow" width="50%">
            <el-form :model="editCustomCommand">
                <el-form-item label="名字" :label-width="editFormLabelWidth">
                    <el-input v-model="editCustomCommand.name"></el-input>
                </el-form-item>
                <el-form-item label="命令" :label-width="editFormLabelWidth">
                    <el-input v-model="editCustomCommand.command">

                    </el-input>
                </el-form-item>
                <el-form-item label="执行频率" :label-width="editFormLabelWidth">
                    <el-input v-model="editCustomCommand.duration"></el-input>
                </el-form-item>
                <el-form-item label="时间单位" :label-width="editFormLabelWidth">
                    <el-select v-model="editCustomCommand.timeUnit" placeholder="请选择时间单位"
                               style="width:100%">
                        <el-option v-for="item in timeUnit" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="isCustomCommandEditFormShow = false">取 消</el-button>
                <el-button type="primary" @click="handleEditCustomCommand()">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>
    import {deleteRequest, getRequest, putRequest} from "../../utils/Http";


    export default {
        name: "CustomCommandInfo",
        data() {
            return {
                customCommands: [],
                loading: true,
                timer: null,
                isCustomCommandEditFormShow: false,
                editCustomCommand: {},
                editFormLabelWidth: '100px',
                timeUnit: [
                    {
                        label: "小时",
                        value: "h"
                    }, {
                        label: "分钟",
                        value: "min"
                    }, {
                        label: "秒",
                        value: "s"
                    }, {
                        label: "毫秒",
                        value: "ms"
                    },
                ]
            }
        }, methods: {
            handleEdit(index) {
                this.isCustomCommandEditFormShow = true
                this.editCustomCommand = this.customCommands[index]
                const duration = this.editCustomCommand.duration
                this.editCustomCommand.timeUnit = duration.match(/[a-z|A-Z]+$/gi)[0];
                this.editCustomCommand.duration = duration.match(/^\d+/gi)[0];
                console.log(this.editCustomCommand);
            }, handleDelete(id) {
                this.loading = true
                deleteRequest("/customCommand/" + id).then(() => this.getCustomCommands())
            }, getCustomCommands() {
                getRequest("/customCommand/all").then((response) => {
                    // console.log("customCommand")
                    // console.log(data);
                    this.customCommands = response.data
                    setTimeout(() => this.loading = false, 100)

                })
            }, handleEditCustomCommand() {
                this.editCustomCommand.duration = this.editCustomCommand.duration + this.editCustomCommand.timeUnit
                putRequest("/customCommand/", this.editCustomCommand).then(() => {
                    this.getCustomCommands()
                    this.isCustomCommandEditFormShow = false
                })
            }
        }, mounted() {
            this.getCustomCommands()
        }
    }
</script>

<style scoped lang="scss">
</style>