<template>

    <div class="server-chart">
        <div class="operations">
            <el-select
                    v-model="selectOptions"
                    multiple
                    collapse-tags
                    @change="refreshChart"
                    placeholder="请选择想要查看的数据"
                    class="op-item" style="width: 300px"
            >
                <el-option
                        v-for="item in options"
                        :key="item"
                        :label="item"
                        :value="item">
                </el-option>
            </el-select>
            <el-checkbox v-model="checked" class="op-item">显示实时图表</el-checkbox>
            <div class=" op-item date-select" v-show="!checked">
                <span class="label">日期选择:</span>
                <el-date-picker
                        v-model="dateRange"
                        type="datetimerange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :clearable="false"
                        @change="refreshChart"
                        :picker-options="pickerOptions">
                </el-date-picker>
            </div>
        </div>
        <div class="chart">
            <div class="chart-box" v-for="item in showRecords" :key="item.url">
                <Chart :url="item.url"
                       :duration="item.duration"
                       :title="item.title">
                </Chart>
            </div>
        </div>
    </div>

</template>

<script>
    import {getRequest} from "../../utils/Http";
    import Chart from "../base/Chart";

    export default {
        name: "ServerChart",
        components: {Chart}
        , props: {
            serverId: {
                type: Number,
                require: true
            }
        }
        , data() {
            return {
                pickerOptions: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }]
                },
                dateRange: [],
                loading: false,
                options: [],
                selectOptions: [],
                checked: true,
                showRecords: [],
            }
        }, watch: {
            checked(val) {
                if (!val) {
                    this.updateDateRange()
                } else {
                    this.dateRange = []
                }
                this.refreshChart()
            }
        }, methods: {
            handleClick(tab, event) {
                console.log(tab, event);
            }, getOptions() {
                getRequest("/server/options").then(_ => {
                    this.options = _.data
                    this.selectOptions = _.data
                    this.refreshChart()
                })
            }, refreshChart() {
                console.log("refresh")
                // 刷新图表
                // 判断是否实时
                if (this.checked) {
                    // 是实时
                    this.showRecords = this.selectOptions.map(option => {
                        return {
                            url: `/server/record/${this.serverId}/${option}`,
                            duration: 5000,
                            title: option
                        }
                    })
                } else {
                    // 不是实时， 有时间范围
                    this.showRecords = this.selectOptions.map(option => {
                        return {
                            url: `/server/dataRangeRecord/${this.serverId}/${option}?startDate=${this.dateRange[0].getTime()}&endDate=${this.dateRange[1].getTime()}`,
                            duration: -1,
                            title: option
                        }
                    })
                }
                console.log("new showRecord => ", this.showRecords)
            }, updateDateRange() {
                const tempDateRange = []
                const endDate = new Date()
                tempDateRange[0] = new Date(
                    endDate.getFullYear(),
                    endDate.getMonth(),
                    endDate.getDate(),
                    endDate.getHours() - 2,
                    endDate.getMinutes(),
                    endDate.getSeconds()
                )
                tempDateRange[1] = endDate
                this.dateRange = tempDateRange
            }
        }, mounted() {
            this.getOptions()
        }
    }
</script>

<style scoped lang="scss">

    .server-chart {
        .operations {
            display: flex;
            justify-content: flex-start;
            align-items: center;

            .op-item {
                margin-right: 50px;

                &:last-child {
                    margin-right: 0;
                }
            }

            .date-select {
                .label {
                    margin-right: 15px;
                }
            }
        }

        .chart {
            margin-top: 20px;
            display: flex;
            flex-wrap: wrap;

            .chart-box {
                flex-basis: calc(50% - 20px);
            }
        }
    }
</style>