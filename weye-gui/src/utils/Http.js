import axios from 'axios'
import {Message} from 'element-ui'
import store from "../store/Store";
import router from "../router"

axios.interceptors.request.use(config => {
    //show request config
    console.debug("request config")
    console.debug("request url => ", config.url)
    console.debug("request method => ", config.method)
    console.debug("request data => ", config.data)

    const token = store.get('token')
    // 请求附加token
    if (token !== null && token !== undefined)
        config.headers.common['token'] = token
    return config;
}, err => {
    console.debug("request err")
    console.debug(err)
})
axios.interceptors.response.use(response => {
    const request = response.request
    console.debug("url: " + request.responseURL);
    console.debug("response data: ");
    console.debug(response.data)
    console.debug("response status: " + response.status);
    return response;
}, err => {
    console.debug("response err")
    console.debug(err.response.status)
    switch (err.response.status) {
        case 400:
            // 请求数据有问题
            Message.warning(err.response.data)
            break
        case 401:
            // 未登录
            // Message.warning(err.response.data)
            setTimeout(() => {
                router.push({path: "/login"})
            }, 300)
            break
        case 500:
            //服务器端错误
            Message.error("server error")
            break
        default :
            Message.error(err.response.data)
    }
    return Promise.reject(err)
})

axios.defaults.baseURL = 'api'
// let base = '';
export const postRequest = (url, params) => {
    return axios({
        method: 'post',
        url: url,
        data: JSON.stringify(params),
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    });
}
export const postRequestSync = async (url, params) => {
    return await axios.post(url, params);
}
export const putRequest = (url, params) => {
    return axios({
        method: 'put',
        url: url,
        data: JSON.stringify(params),
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    });
}
export const deleteRequest = (url, params) => {
    return axios({
        method: 'delete',
        url: url,
        data: params,
    });
}
export const getRequest = (url) => {
    return axios({
        method: 'get',
        url: url
    });
}