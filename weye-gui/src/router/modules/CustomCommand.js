import Layout from "../../layout/Layout";
import CustomCommand from "../../views/CustomCommand"
export default {
    path: '/customCommand',
    component: Layout,
    meta: {
        title: '自定义命令数据采集器'
    }, children: [
        {
            path: "",
            component: CustomCommand,
        }
    ]
}