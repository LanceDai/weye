<template>
    <el-container class="layout" v-if="show">
        <el-aside class="aside">
            <div class="logo">
                <svg-icon :icon-name="'icon-jiankong'" class="icon"></svg-icon>
                <span class="letter">Weye</span>
            </div>
            <hr>
            <el-menu class="nav"
                     background-color="#545c64"
                     text-color="#fff"
                     active-text-color="#ffd04b"
                     router
                     :default-active="activeIndex()">
                <el-menu-item index="/dashboard" class="item-box">
                    <div class="item">
                        <svg-icon :icon-name="'icon-dashboard'" class="icon"></svg-icon>
                        <span slot="title" class="letter">仪表盘</span>
                    </div>
                </el-menu-item>
                <el-menu-item index="/webApp" class="item-box">
                    <div class="item">
                        <svg-icon :icon-name="'icon-app'" class="icon"></svg-icon>
                        <span slot="title" class="letter">WEB应用</span>
                    </div>
                </el-menu-item>
                <el-menu-item index="/server" class="item-box">
                    <div class="item">
                        <svg-icon :icon-name="'icon-fuwuqi'" class="icon"></svg-icon>
                        <span slot="title" class="letter">服务器</span>
                    </div>
                </el-menu-item>

                <el-menu-item index="/customCommand" class="item-box">
                    <div class="item">
                        <svg-icon :icon-name="'icon-command-line'" class="icon"></svg-icon>
                        <span slot="title" class="letter">自定义命令</span>
                    </div>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container class="main-container">
            <el-header class="header">
                <top-bar class="bar"></top-bar>
            </el-header>
            <el-main class="main">
                <router-view v-wechat-title=" $route.meta.title"/>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
    import TopBar from "../components/layout/TopBar";
    import SvgIcon from "../components/base/SvgIcon";
    import {isLogin} from "../utils/auth"

    export default {
        name: 'layout',
        components: {
            SvgIcon,
            TopBar,
        }, data() {
            return {
                show: false
            }
        }, methods: {
            activeIndex() {
                const str = this.$route.path;
                console.log("str : " + str)
                const index = str.indexOf("/", 1)
                console.log("index : " + index)
                let res;
                if (index === -1) res = str;
                else res = str.slice(0, index)
                console.log("res = " + res)
                return res;
            }
        }, created() {
            //判断是否登录
            console.log("Layout - judge")
            //判断是否登录
            isLogin().then((loginRes)=>{
                console.log("layout loginRes: " + loginRes);
                if (loginRes === true) {
                    console.log("layout isLogin: " + true)
                    this.show = true
                } else {
                    console.log("layout isLogin: " + false)
                }
            })

        }
    }
</script>

<style lang="scss" scoped>
    .layout {
        height: 100vh;

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
                    max-width: 50px;
                }

                .letter {
                    height: 50px;
                    line-height: 50px;
                    text-align: center;
                }
            }

            .nav {
                min-height: calc(100vh - 68px);
                box-sizing: border-box;
                padding-top: 30px;

                .item-box {
                    display: flex;
                    justify-content: center;
                    align-items: center;

                    .item {
                        height: 50px;
                        width: 70%;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;

                        .icon {
                            margin-right: 15px;
                            width: 35px;
                            height: 35px;
                            color: #ffffff;
                        }

                        .letter {
                            font-weight: bold;
                            font-size: 20px;
                        }
                    }
                }
            }

        }

        .main-container {
            .header {
                background-color: #B3C0D1;
                color: #333;
                text-align: center;
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
            }
        }
    }

</style>
