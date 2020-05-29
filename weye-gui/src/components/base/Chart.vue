<template>
    <ve-line :data="chartData" :title="getTitle()" :settings="chartSettings"></ve-line>
</template>

<script>
    import {getRequest} from "../../utils/Http";

    export default {
        name: "Chart",
        props: {
            url: {
                type: String,
                require: true,
                description: "数据获取链接"
            }, duration: {
                type: Number,
                default: 1000,
                description: "数据更新频率， 单位ms"
            }, title: {
                type: String,
                require: true,
                description: "图表标题"
            }, item: {
                type: String,
                description: "数据说明"
            }
        }, data() {
            return {
                chartData: {},
                chartSettings: {
                    labelMap: {
                        'data': this.item || this.title,
                    },
                    // xAxisType: 'time'
                }, timer: null
            }
        }, methods: {
            getTitle() {
                return {
                    text: this.title
                }
            }, getChartData() {
                getRequest(this.url).then(_ => {
                    this.chartData = _.data
                })
            }
        }, mounted() {
            this.getChartData()
            if (this.duration !== -1)
                this.timer = setInterval(() => this.getChartData(), this.duration)
        }, beforeDestroy() {
            if (this.timer !== null)
                clearInterval(this.timer)
        }
    }
</script>

<style scoped>

</style>