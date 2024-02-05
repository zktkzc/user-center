<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/user.ts";

// const user: UserType = {
//   id: 1,
//   username: 'tkzc00',
//   userAccount: 'tkzc00',
//   avatarUrl: 'https://p.qqan.com/up/2020-12/16070652272519101.jpg',
//   createTime: new Date(),
//   email: "12345@qq.com",
//   gender: 0,
//   phone: "13512345678",
//   planetCode: "1234",
//   tags: []
// }
const user = ref();
const router = useRouter();
onMounted(async () => {
  const currentUser = await getCurrentUser();
  if (currentUser) {
    user.value = currentUser;
    showSuccessToast('获取用户信息成功');
  } else {
    showFailToast('获取用户信息失败');
  }
})

const toEdit = (editKey: string, editName: string, currentValue: any) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue
    }
  })
}
</script>

<template>
  <template v-if="user">
    <van-cell title="昵称" :value="user.username" is-link @click="toEdit('username', '昵称', user.username)"/>
    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="头像" is-link @click="toEdit('avatarUrl', '头像', user.avatarUrl)">
      <img :src="user.avatarUrl" style="width: 40px;height: 40px;"/>
    </van-cell>
    <van-cell title="性别" :value="user.gender === 0 ? '男' : '女'" is-link
              @click="toEdit('gender', '性别', user.gender)"/>
    <van-cell title="电话" :value="user.phone" is-link @click="toEdit('phone', '电话', user.phone)"/>
    <van-cell title="邮箱" :value="user.email" is-link @click="toEdit('email', '邮箱', user.email)"/>
    <van-cell title="星球编号" :value="user.planetCode"/>
    <van-cell title="注册时间" :value="user.createTime"/>
  </template>
</template>

<style scoped>

</style>