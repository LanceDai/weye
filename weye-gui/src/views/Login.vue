<template>
    <div class="layout-container" v-if="show">
        <section>
            <TypingText
                    style="font-size: 50px"
                    :text-one="'WEYE'"
                    :text-two="'A Web Application Monitor System'"
            ></TypingText>
        </section>
        <div class="progress">
            <!--用来进行背景颜色过渡-->
        </div>
        <div class="login-container">
            <div class="panel">
                <div class="stand">
                    <span class='login'>Login</span>
                    <div class="login-from">
                        <div class="input" data-placeholder='Username' @click="inputClick($event)">
                            <input type="text" v-model=username ref="username" @blur="inputBlur($event)">
                        </div>
                        <div class="input" data-placeholder='Password' @click="inputClick($event)">
                            <input type="password" v-model=password ref="password" @focus="inputFocus($event)"
                                   @blur="inputBlur($event)">
                        </div>
                        <div id='repeat' class="input" data-placeholder='Repeat' @click="inputClick($event)">
                            <input type="password" v-model=repeat ref="repeat" @focus="inputFocus($event)"
                                   @blur="inputBlur($event)">
                        </div>
                        <GlowingBtn class="button" @click.native="submit($event)">LOGIN</GlowingBtn>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

    import $ from 'jquery';
    import store from '../store/Store'
    import md5 from 'js-md5'
    import {isLogin} from "../utils/auth"
    import {isEmpty} from "../utils/Common";

    export default {
        name: "Login",
        data() {
            return {
                username: null,
                password: null,
                repeat: null,
                show: false
            }
        }, computed: {}, methods: {
            inputClick(e) {
                const input = $(e.currentTarget)
                input.addClass('focus')
                input.children('input').focus()
            }, inputFocus(e) {
                $(e.currentTarget).parent().addClass('focus')
            }, inputBlur(e) {
                let ele = $(e.currentTarget);
                if (ele.val() === '') {
                    ele.parent().removeClass('focus')
                }
            }, submit() {
                if (isEmpty(this.username) || isEmpty(this.password)) {
                    this.$message.warning("用户名和密码不能为空")
                } else {
                    const digest = md5.hex(`${this.username}$${this.password}`)
                    const data = new FormData
                    data.append("digest", digest)
                    this.$axios.post("/login", data).then((response) => {
                        let res = response.data;
                        if (res !== null && res !== undefined) {
                            store.set('token', res)
                            console.log("login success, jump to dashboard")
                            this.$router.push({path: "/"});
                        }
                    }).catch((err) => {
                        this.$message.warning("submit res: " + err.data)
                    })
                }
            }
        }, created() {
            console.log("Login - judge")
            //判断是否登录
            isLogin().then((loginRes) => {
                console.log("loginRes: " + loginRes);
                if (loginRes === true) {
                    console.log("Login - isLogin: " + true)
                    // this.$router.push({path: "/"});
                } else {
                    console.log("Login - isLogin: " + false)
                    this.show = true
                }
            })
        }
    }
</script>

<style lang="scss" scoped>

    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@500;600;700;800;900&display=swap');


    .layout-container {
        margin: 0;
        padding: 0;
        font-family: 'Poppins', sans-serif;

        section {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            font-size: 5em;
            font-weight: 700;
            text-transform: uppercase;
            background-color: #000;
            color: #fff;
        }

        .progress {
            position: relative;
            display: flex;
            background: linear-gradient(0deg, #cfc0ee, #4c67ea, #000);
            min-height: 300vh;
        }

        .login-container {
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
            background: #cfc0ee;

            .panel {
                background-image: url("../assets/img/bc.jpg");
                background-repeat: no-repeat;
                background-size: cover;
                width: 95%;
                height: 95%;
                box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3), 0 0 40px rgba(0, 0, 0, 0.1) inset;
                /* 网格布局*/
                display: grid;
                grid-template-columns: 12fr 5fr;
                grid-template-rows: 1fr;
                grid-template-areas: "img login";

                .stand {
                    transition: all 0.5s;
                    box-sizing: border-box;
                    padding-top: 25%;
                    padding-left: 5%;
                    padding-right: 5%;
                    grid-area: login;

                    span {
                        color: #ccc;
                        font-size: 3rem;
                        cursor: pointer;
                    }

                    .login-from {
                        margin-top: 50px;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;

                        div {
                            display: block;
                            color: rgb(110, 89, 167);
                            font-size: 2rem;
                        }

                        .button {
                            border: none;
                            outline: none;
                            margin: 2.5rem 0 0;
                            width: 80%;
                            border-radius: 3rem;
                            background: linear-gradient(90deg, rgb(181, 154, 254), rgb(245, 189, 253));
                            box-shadow: 0 0 8px rgb(181, 154, 254);
                            cursor: pointer;
                            color: white;
                            font-size: 40px;
                            transition: all 1s;
                            text-align: center;
                        }

                        .input {
                            position: relative;
                            width: 100%;
                            margin: 2rem 0;
                            transition: .4s;
                            cursor: text;

                            input {
                                background-color: transparent;
                                font-size: 20px;
                                outline: none;
                                width: 100%;
                                border: none;
                                border-bottom: .1rem solid rgb(181, 154, 254);
                            }

                            &::after {
                                content: attr(data-placeholder);
                                position: absolute;
                                left: 0;
                                top: -10%;
                                font-size: 1.8rem;
                                color: rgb(129, 101, 207);
                                transition: .3s;
                            }

                            &.focus::after {
                                top: -50%;
                                font-size: 1.5rem;
                            }
                        }
                    }
                }

            }
        }
    }

</style>