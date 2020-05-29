<template>
    <div id="app">
        <!-- 路由出口 -->
        <!-- 路由匹配到的组件将渲染在这里 -->
        <Login v-if="$route.path.toLowerCase() === '/login'"></Login>
        <div v-else>
            <el-container class="layout-container">
                <el-aside class="aside">
                    <div class="logo">
                        <svg class="icon" aria-hidden="true">
                            <use xlink:href="#icon-jiankong"></use>
                        </svg>
                        <span class="letter">Weye</span>
                    </div>
                    <hr>
                    <el-menu class="nav"
                             background-color="#545c64"
                             text-color="#fff"
                             active-text-color="#ffd04b"
                             router
                             :default-active="$route.path">
                        <el-menu-item index="/" class="item">
                            <svg class="icon" aria-hidden="true">
                                <use xlink:href="#icon-dashboard"></use>
                            </svg>
                            <span slot="title">仪表盘</span>
                        </el-menu-item>
                        <el-menu-item index="/server/" class="item">
                            <svg class="icon" aria-hidden="true">
                                <use xlink:href="#icon-fuwuqi"></use>
                            </svg>
                            <span slot="title">服务器</span>
                        </el-menu-item>
                        <el-menu-item index="/app/" class="item">
                            <svg class="icon" aria-hidden="true">
                                <use xlink:href="#icon-app"></use>
                            </svg>
                            <span slot="title">WebAPP</span>
                        </el-menu-item>
                        <el-menu-item index="/source/">
                            <svg class="icon" aria-hidden="true">
                                <use xlink:href="#icon-resources"></use>
                            </svg>
                            数据源
                        </el-menu-item>
                        <el-submenu index="/compute/">
                            <template slot="title">
                                <svg class="icon" aria-hidden="true">
                                    <use xlink:href="#icon-config-rule"></use>
                                </svg>
                                计算规则
                            </template>
                            <el-menu-item index="/compute/data">数据</el-menu-item>
                            <el-menu-item index="/compute/chart">结果展示</el-menu-item>
                        </el-submenu>
                        <el-submenu index="/warn/">
                            <template slot="title">
                                <svg class="icon" aria-hidden="true">
                                    <use xlink:href="#icon-config-rule"></use>
                                </svg>
                                告警规则
                            </template>
                            <el-menu-item index="/warn/data/">数据</el-menu-item>
                            <el-menu-item index="/warn/message/">告警消息</el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-container class="main-container">
                    <el-header class="header">
                        <top-bar class="bar"></top-bar>
                    </el-header>
                    <el-main main>
                        <router-view v-wechat-title="$route.meta.title"/>
                    </el-main>
                </el-container>
            </el-container>
        </div>

    </div>
</template>

<script>
    import Login from "./views/Login";
    import TopBar from "./components/layout/TopBar";

    export default {
        name: 'app',
        components: {
            TopBar,
            Login
        }, methods: {}, mounted() {
            console.log(this.$route.path)
        }
    }
</script>

<style lang="scss">
    html, body {
        margin: 0;
        padding: 0;
        user-select: none;
    }

    a {
        text-decoration: none;
    }

    #app {
        min-height: 100vh;

        .layout-container {
            min-height: 100vh;

            .aside {
                background-color: #545c64;
                color: #333;
                text-align: center;
                line-height: 200px;
                width: 150px;
                min-height: 100vh;

                .logo {
                    height: 50px;
                    font-size: 30px;
                    text-transform: uppercase;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    color: #ffffff;

                    .icon {
                        margin-right: 15px;
                        height: 100%;
                    }

                    .letter {
                        height: 50px;
                        line-height: 50px;
                        text-align: center;
                    }
                }

                .nav {
                    min-height: calc(100vh - 68px);

                    .item {
                        height: 50px;
                        line-height: 50px;

                    }

                    & .icon {
                        margin-right: 5px;
                        width: 25px;
                        height: 25px;
                    }

                }

            }

            .main-container {
                .header {
                    background-color: #B3C0D1;
                    color: #333;
                    text-align: center;
                    line-height: 60px;
                    margin: 0;
                    padding: 0;

                    .bar {
                        height: 100%;
                        width: 100%;
                    }
                }

                .main {
                    background-color: #E9EEF3;
                    color: #333;
                    text-align: center;
                    line-height: 160px;
                }
            }
        }

    }

</style>
