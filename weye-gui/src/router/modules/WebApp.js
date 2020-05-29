import Layout from "../../layout/Layout";
import WebApp from "../../views/WebApp";
import WebAppDetail from "../../components/webApp/WebAppDetail"
export default {
    path: '/webApp',
    component: Layout,
    meta: {
        title: 'WEB应用'
    }, children: [
        {
            path: "",
            component: WebApp,
        }, {
            path: "detail/:webAppId",
            component: WebAppDetail
        }
    ]
}