<template>
    <div class="top-bar">
        <div class="button">
            <el-button type="primary" round @click="open">
                Log Out
            </el-button>
        </div>

    </div>
</template>

<script>
    //顶栏

    export default {
        name: "TopBar",
        data() {
            return {
                dialogFormVisible: false,

            }
        }, methods: {
            open() {
                this.$confirm('确定登出吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios.get("/logout")
                        .then((response) => {
                            if (response.status === 200) {
                                this.$message({
                                    type: 'success',
                                    message: '登出成功!'
                                });
                                setTimeout(() => this.$router.push({path: "/"}), 500)
                            }
                        })
                        .catch(err => {
                            console.log(err);
                            this.$message({
                                type: 'warning',
                                message: '有错误发生： ' + err
                            });
                        });

                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消登出'
                    });
                });
            }
        }
    }
</script>

<style scoped lang="scss">

    .top-bar {
        $bar-height: 50px;
        padding: 10px 30px;
        height: $bar-height;
        width: 100%;
        box-sizing: border-box;
        background-color: #fcf3f3;
        display: flex;
        justify-content: flex-end;
        button{
            flex: 1;
        }
    }
</style>