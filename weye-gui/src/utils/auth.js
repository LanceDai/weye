import store from "../store/Store";
import {isEmpty} from "./Common";
import {postRequestSync} from "./Http";

export async function isLogin() {
    // 判断是否已登录
    const token = store.get('token')
    console.log("token: " + token)
    const data = new FormData();
    data.append("token", token);
    let res
    await postRequestSync("/isLogin", data)
        .then((response) => {
            res = !isEmpty(response) && response.status === 200;
        }).catch(() => {
            res = false
        })
    console.log("res: " + res)
    return res;
}