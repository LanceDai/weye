// store模式
// 保存全局变量
export default {
    debug: false,
    state: {},
    set(key, value) {
        if (this.debug) console.log("set " + key + " => ", value);
        this.state[key] = value
        // 同时保存至localStorage
        localStorage.setItem(key, value);
    },
    get(key) {
        return this.state[key] || localStorage.getItem(key)
    }
};
