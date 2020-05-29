<template>
    <div class="test">
        <div class="test-container">
            <icon-card>
                <el-avatar class="icon" style="background-color: green" slot="icon">在线</el-avatar>
                <span>1</span>
            </icon-card>
        </div>
    </div>
</template>

<script>

    import IconCard from "../components/base/IconCard";

    export default {
        name: "Test",
        components: {IconCard},
    }
</script>

<style scoped lang="scss">
    .test {
        min-height: 100vh;
        min-width: 100vw;
        display: flex;
        justify-content: center;
        align-items: center;
        .test-container{
            width: auto;
            height: auto;
        }
    }

</style>