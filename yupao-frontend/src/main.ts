import {createApp} from 'vue'
import App from './App.vue'
import * as VueRouter from 'vue-router'
import Vant from 'vant'
import 'vant/lib/index.css'
import routes from "./config/route.ts";
import './global.css';

const app = createApp(App)

// 按需引入Vant组件
app.use(Vant)

// VueRouter路由配置
const router = VueRouter.createRouter({
    history: VueRouter.createWebHistory(),
    routes
})

app.use(router)

app.mount('#app')