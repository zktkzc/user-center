<script setup lang="ts">
import {ref, watchEffect} from "vue";
import {UserType} from "../models/user";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import UserCardList from "../components/UserCardList.vue";

const userList = ref<UserType[]>([])
const isMatchMode = ref<boolean>(false);
const loading = ref<boolean>(true);
const loadData = async () => {
  let userListData: UserType[] = [];
  loading.value = true;
  if (isMatchMode.value) {
    // 心动模式，匹配用户
    const res = await myAxios.get('/user/match', {
      params: {
        num: 10
      }
    });
    if (res?.data.code === 0 && res?.data.data) {
      const userListData: UserType[] = res.data.data;
      userListData.forEach(user => {
        if (user.tags) {
          user.tags = JSON.parse(user.tags);
        }
      })
      userList.value = userListData;
      loading.value = false;
      showSuccessToast('匹配成功')
    } else {
      loading.value = false;
      showFailToast('匹配失败')
    }
  } else {
    // 普通模式，分页查询用户
    userListData = await myAxios.get('/user/recommend', {
      params: {
        page: 1,
        pageSize: 8
      }
    }).then(response => {
      showSuccessToast('请求成功')
      return response.data.data.records;
    }).catch(() => {
      loading.value = false;
      showFailToast('请求失败')
    })
    if (userListData) {
      userListData.forEach(user => {
        if (user.tags) {
          user.tags = JSON.parse(user.tags);
        }
      })
      userList.value = userListData;
      loading.value = false;
    }
  }
}
watchEffect(() => {
  loadData();
});
</script>

<template>
  <van-cell center title="心动模式">
    <template #right-icon>
      <van-switch v-model="isMatchMode" size="24"/>
    </template>
  </van-cell>
  <user-card-list :userList="userList" :loading="loading"/>
  <van-empty v-if="!userList || userList.length < 1" description="暂无数据"/>
</template>

<style scoped>

</style>