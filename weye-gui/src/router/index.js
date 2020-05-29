import Vue from 'vue'
import Router from 'vue-router'
import VueWechatTitle from "vue-wechat-title";

// views
import Login from "../views/Login";
import Test from "../views/Test";

//router modules

import server from "./modules/Server"
import webApp from "./modules/WebApp"
import customCommand from "./modules/CustomCommand"

Vue.use(VueWechatTitle);

Vue.use(Router)
/*Layout*/
import Layout from '../layout/Layout'
import DashBoard from "../views/DashBoard";

const routers = [
    {
        path: '/login/',
        component: Login,
        name: 'login',
        hidden: true,
        meta: {
            title: '登陆页面'
        }
    }, {
        path: '/',
        component: Layout,
        redirect: '/dashboard/',
        children: [
            {
                path: 'dashboard',
                component: DashBoard,
                name: 'Dashboard',
                meta: {title: '首页', icon: 'dashboard', affix: true}
            },
        ]
    },
    server,
    webApp,
    customCommand,
    {
        path: '/test/',
        component: Test,
        name: 'test',
        meta: {
            title: '测试页面'
        }
    }
]

const createRouter = () => new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({y: 0}),
    routes: routers
})

const router = createRouter()

export default router