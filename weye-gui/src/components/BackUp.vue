<template>
    <top-bar></top-bar>
    <div class="row"></div>
    <div class="home-main">
        <div class="scalable" ref="scalable" :style="{width: scalableWidth+'px'}">
            <div class="content">
                <!--                        <el-menu -->
                <!--                                @open="handleOpen"-->
                <!--                                 @close="handleClose"-->
                <!--                        >-->
                <el-menu :default-active="activeIndex()">


                    <router-link :to="'/server'">
                        <el-menu-item index="1">
                            <i class="el-icon-menu"></i>
                            <span slot="title">服务器</span>
                        </el-menu-item>
                    </router-link>
                    <el-menu-item index="2">
                        <i class="el-icon-menu"></i>
                        <span slot="title">Web应用</span>
                    </el-menu-item>
                    <el-menu-item index="3">
                        <i class="el-icon-document"></i>
                        <span slot="title">警告事件</span>
                    </el-menu-item>
                    <el-menu-item index="4">
                        <i class="el-icon-setting"></i>
                        <span slot="title"></span>
                    </el-menu-item>
                </el-menu>
            </div>
            <div class="separator" @mousedown="startDrag">
                <i></i>
                <i></i>
            </div>
        </div>

        <div class="main">
            <div class="content">
                <router-view v-wechat-title="$route.meta.title"/>
            </div>
        </div>
    </div>
</template>

<script>
    import TopBar from "./layout/TopBar";
    import store from "../store/Store";

    export default {
        name: "BackUp",
        components: {TopBar}
        , data() {
            return {
                startX: 0,
                startWidth: 0,
                scalableWidth: 0,
            }
        }, methods: {
            startDrag(e) {
                this.startX = e.clientX
                this.startWidth = this.scalableWidth
                document.documentElement.addEventListener('mousemove', this.onDrag)
                document.documentElement.addEventListener('mouseup', this.stopDrag)
            }, onDrag(e) {
                this.scalableWidth = this.startWidth + e.clientX - this.startX
            }, stopDrag() {
                store.set("scalableWidth", this.scalableWidth)
                document.documentElement.removeEventListener("mousemove", this.onDrag)
                document.documentElement.removeEventListener("mouseup", this.stopDrag)
            }, getScalableDivWidth() {
                return this.strToInt(window.getComputedStyle(this.$refs.scalable).width)
            }, strToInt(str) {
                return parseInt(str, 10)
            }, activeIndex() {
                const path = this.$route.path
                if (path.toLowerCase() === "/server") return "1";
                else return "0"
            }
        }, mounted() {
            console.log(this.$route.path)
            const tempScalableWidth = this.strToInt(store.get("scalableWidth")) || this.getScalableDivWidth()
            this.scalableWidth = tempScalableWidth > 170 ? tempScalableWidth : 170
        }
    }
</script>

<style scoped lang="scss">
    .bar {
        height: 50px;
    }

    .row {
        height: 4px;
        box-shadow: 0 0 2px rgba(0, 0, 0, .35);
    }

    .home-main {
        height: calc(100vh - 50px - 4px);
        display: flex;
        margin: 0;

        .scalable {
            position: relative;
            background-color: #eeeeee;
            min-width: 170px;
            max-width: 300px;

            .content {
                padding: 20px 34px 20px 20px;

                img {
                    display: block;
                    max-height: 100%;
                    max-width: 100%;
                    border-radius: 50%;
                    margin: auto;
                }
            }

            .separator {
                display: flex;
                justify-content: center;
                align-items: center;
                position: absolute;
                top: 0;
                right: 0;
                width: 14px;
                height: 100%;
                background-color: #ffffff;
                box-shadow: 0 0 2px rgba(0, 0, 0, .35);
                cursor: col-resize;

                i {
                    display: inline-block;
                    height: 14px;
                    width: 1px;
                    background-color: #e9e9e9;
                    margin: 0 1px;
                }
            }
        }

        .main {
            flex: 1;

            .content {
                height: 100%;
                width: 100%;
            }
        }
    }
</style>