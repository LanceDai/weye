<template>
    <el-card class="icon-card">
        <div class="icon-card-body">
            <div class="icon-card-wrapper">
                <slot name="icon"></slot>
            </div>
            <div class="icon-card-description">
                <slot></slot>
            </div>
        </div>
    </el-card>
</template>

<script>
    export default {
        name: "IconCard"
    }
</script>

<style scoped lang="scss">
    .icon-card {
        .icon-card-body {
            margin: 10px 20px;
            height: auto;
            width: auto;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .icon-card-wrapper {
                margin-right: 25px;
                max-height: 80px;
                max-width: 80px;
                display: flex;
                justify-content: center;
                align-items: center;
                transition: all 0.2s ease-in-out;
                font-size: 30px;
                & > *{
                    height: 100%;
                    width: 100%;
                }

            }

            .icon-card-description {
                height: 100%;
                width: 100%;
                min-height: 50px;
                min-width: 50px;
                line-height: 50px;
                text-transform: uppercase;
                color: #baa6a6;
                font-size: 50px;
            }
        }
    }
</style>