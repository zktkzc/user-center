<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import {UserType} from "../models/user";
import myAxios from "../plugins/myAxios.ts";
import qs from 'qs';
import {showFailToast, showSuccessToast} from "vant";
import UserCardList from "../components/UserCardList.vue";

const route = useRoute()
const tags = route.query.tags
// const mockUser = {
//   id: 1,
//   username: 'tkzc00',
//   userAccount: 'tkzc00',
//   avatarUrl: 'https://p.qqan.com/up/2020-12/16070652272519101.jpg',
//   createTime: new Date(),
//   email: "12345@qq.com",
//   gender: 0,
//   phone: "13512345678",
//   planetCode: "1234",
//   tags: ['java', 'emo', '打工中', 'c++', 'python'],
//   profile: '我是一个打工人，我为自己代言',
// }
const userList = ref<UserType[]>([])
onMounted(async () => {
  const userListData: UserType[] = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  }).then(response => {
    showSuccessToast('请求成功')
    return response.data.data;
  }).catch(_ => {
    showFailToast('请求失败')
  })
  if (userListData) {
    userListData.forEach(user => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})
</script>

<template>
  <user-card-list :userList="userList"/>
  <van-empty v-if="!userList || userList.length < 1" description="搜索结果为空"/>
</template>

<style scoped>

</style>