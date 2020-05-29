<template>
    <div class="message-box">
        <transition-group name="fade">
            <p v-for="(item) in messages" :key="item.id">
                {{item.msg}}
            </p>
        </transition-group>
    </div>
</template>

<script>

    export default {
        name: "ScrollMessage"
        , props: {
            messages: {
                type: Array,
                require: true,
                description: "滚动数据流"
            }
        }
        , data() {
            return {}
        }, methods: {}, mounted() {
        }
    }
</script>

<style scoped lang="scss">
    .message-box {
        border: #eeeeee;
        height: 100%;
        overflow: hidden;
        p{
            font-size: 15px;
            color: red;
        }
    }
</style>