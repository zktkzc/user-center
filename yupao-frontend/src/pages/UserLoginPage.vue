<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";

const route = useRoute();
const userAccount = ref('');
const userPassword = ref('');
const onSubmit = async () => {
  const res = await myAxios.post('/user/login', {
    userAccount: userAccount.value,
    userPassword: userPassword.value
  });
  if (res.data.code === 0) {
    showSuccessToast('登录成功');
    const redirectUrl = route.query?.redirectUrl as string ?? '/index';
    window.location.href = redirectUrl;
  } else {
    showFailToast('登录失败');
  }
}
</script>

<template>
  <div style="width: 100%; position: absolute; top: 50%; transform: translateY(-50%); text-align: center;">
    <h1>伙伴匹配系统</h1>
    <h2>用户登录</h2>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="userAccount"
            name="userAccount"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[
            { required: true, message: '请填写用户名' }]"
        />
        <van-field
            v-model="userPassword"
            type="password"
            name="userPassword"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>

</style>