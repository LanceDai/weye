<template>
    <el-tabs tab-position="right" @tab-click="handleClick" v-model="activeName">
        <el-tab-pane label="已订阅列表" name="subscribe">
            <web-app-command-subscribe-table :web-app-id="webAppId" v-if="subscribe"></web-app-command-subscribe-table>
        </el-tab-pane>
        <el-tab-pane label="未订阅列表" name="unsubscribe">
            <web-app-command-unsubscribe-table :web-app-id="webAppId"
                                               v-if="unsubscribe"></web-app-command-unsubscribe-table>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    import WebAppCommandSubscribeTable from "./WebAppCommandSubscribeTable";
    import WebAppCommandUnsubscribeTable from "./WebAppCommandUnsubscribeTable";

    export default {
        name: "WebAppCustomCommand"
        , components: {WebAppCommandUnsubscribeTable, WebAppCommandSubscribeTable},
        props: {
            webAppId: {
                require: true,
                type: Number
            }
        }, data() {
            return {
                subscribe: true,
                unsubscribe: false,
                activeName: "subscribe"
            }
        }, methods: {
            handleClick() {
                if (this.activeName === 'subscribe') {
                    this.subscribe = true
                    this.unsubscribe = false
                } else {
                    this.subscribe = false
                    this.unsubscribe = true
                }
            }
        }
    }
</script>

<style scoped>

</style>