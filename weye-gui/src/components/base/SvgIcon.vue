<template>
    <i v-if="iconName.startsWith('el-icon-')" :class="iconName"></i>
    <svg v-else class="icon" aria-hidden="true">
        <use :xlink:href="'#'+iconName"/>
    </svg>
</template>

<script>

    export default {
        name: 'SvgIcon',
        props: {
            iconName: {
                type: String,
                required: true
            }
        }
    }
</script>

<style scoped>
    .icon {
        width: 100%;
        height: 100%;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
    }
</style>
