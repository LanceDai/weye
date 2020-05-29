import Server from "../../views/Server";
import Layout from "../../layout/Layout";
import ServerDetail from "../../components/server/ServerDetail"

export default {
    path: '/server',
    component: Layout,
    meta: {
        title: '服务器'
    }, children: [
        {
            path: "",
            component: Server,
        }, {
            path: "detail/:serverId",
            component: ServerDetail
        }
    ]
}