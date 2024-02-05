<script setup lang="ts">
import {useRouter} from "vue-router";
import {ref} from "vue";
import routes from "../config/route.ts";

const router = useRouter();
const onClickLeft = () => router.back();
const onClickRight = () => {
  router.push('/search')
};
const DEFAULT_TITLE = '伙伴匹配';
const title = ref(DEFAULT_TITLE);
// 根据路由切换标题
router.beforeEach((to) => {
  const toPath = to.path;
  const route = routes.find(route => {
    return route.path === toPath;
  });
  title.value = route?.title ?? DEFAULT_TITLE;
});
</script>

<template>
  <van-nav-bar
      :title="title"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
      v-if="!router.currentRoute.value.path.includes('/login')"
  >
    <template #right>
      <van-icon name="search" size="18"/>
    </template>
  </van-nav-bar>
  <div id="content" style="padding-bottom: 50px;">
    <router-view/>
  </div>
  <van-tabbar route v-if="!router.currentRoute.value.path.includes('/login')">
    <van-tabbar-item to="/index" icon="home-o" name="index">主页</van-tabbar-item>
    <van-tabbar-item to="/team" icon="search" name="team">队伍</van-tabbar-item>
    <van-tabbar-item to="/user" icon="friends-o" name="user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>

</style>