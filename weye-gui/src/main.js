import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VCharts from 'v-charts'
import "echarts/lib/component/title";
import LanceV from "lance-v"
import "lance-v/lib/lance-v.css"
import axios from 'axios'
import './assets/iconfont/iconfont.css'
import './assets/iconfont/iconfont.js'

//router
import router from "./router"


Vue.prototype.$axios = axios
Vue.config.productionTip = true;
Vue.use(LanceV);
Vue.use(ElementUI);
Vue.use(VCharts)
new Vue({
    render: h => h(App),
    router
}).$mount('#app');
