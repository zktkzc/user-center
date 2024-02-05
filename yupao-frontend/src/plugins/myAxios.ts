import axios from 'axios';

const myAxios = axios.create({
    baseURL: 'http://localhost:8080/api'
});

/**
 * 允许跨域携带cookie
 */
myAxios.defaults.withCredentials = true;

/**
 * 全局请求拦截器
 */
myAxios.interceptors.request.use(function (config) {
    return config;
}, function (error) {
    return Promise.reject(error);
});

/**
 * 全局响应拦截器
 */
myAxios.interceptors.response.use(function (response) {
    if (response?.data.code === 40100) {
        const redirectUrl = window.location.href;
        window.location.href = `/user/login?redirect=${redirectUrl}`;
    }
    return response;
}, function (error) {
    return Promise.reject(error);
});

export default myAxios;