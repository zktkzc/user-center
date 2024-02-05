<script setup lang="ts">
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../services/user.ts";
import {UserType} from "../models/user";

let currentUser = ref<UserType>();
onMounted(async () => {
  currentUser.value = await getCurrentUser();
})
</script>

<template>
  <div v-if="currentUser">
    <van-cell-group>
      <van-cell title="当前用户" :value="currentUser?.username"/>
      <van-cell title="修改个人信息" is-link to="/user/update"/>
      <van-cell title="我创建的队伍" is-link to="/user/team/create"/>
      <van-cell title="我加入的队伍" is-link to="/user/team/join"/>
    </van-cell-group>
  </div>
</template>

<style scoped>

</style>