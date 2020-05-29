<template>
    <div id="app">
        <!-- 路由出口 -->
        <!-- 路由匹配到的组件将渲染在这里 -->
        <router-view v-wechat-title="$route.meta.title"/>

    </div>
</template>

<script>

    export default {
        name: 'app',
    }
</script>

<style>
    html, body {
        margin: 0;
        padding: 0;
        /*user-select: none;*/
    }
</style>
