<script setup lang="ts">
import {UserType} from "../models/user";

interface UserCardListProps {
  userList: UserType[],
  loading: boolean
}

const props = withDefaults(defineProps<UserCardListProps>(), {
  // @ts-ignore
  userList: [],
  loading: true
});
</script>

<template>
  <van-skeleton title avatar :row="3" :loading="props.loading" v-for="user in props.userList" :key="user.id">
    <van-card
        :title="`${user.username}（${user.planetCode}）`"
        :thumb="user.avatarUrl"
        :desc="user.profile">
      <template #tags>
        <van-tag v-for="tag in user.tags" :key="tag" plain type="danger" round
                 style="margin-right: 8px; margin-top: 8px;"
        >
          {{ tag }}
        </van-tag>
      </template>
      <template #footer>
        <van-button type="primary" size="mini" plain>
          联系我
        </van-button>
      </template>
    </van-card>
  </van-skeleton>
</template>

<style scoped>

</style>