<script setup lang="ts">

import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";

const router = useRouter();
const teamList = ref([]);
const searchText = ref('');
onMounted(() => {
  listTeams();
});
const listTeams = async (val: string = '') => {
  const res = await myAxios.get('/team/list/create', {
    params: {
      searchText: val
    }
  });
  if (res?.data.code === 0 && res?.data.data) {
    teamList.value = res.data.data;
    showSuccessToast('获取队伍列表成功');
  } else {
    showFailToast('获取队伍列表失败，请刷新重试');
  }
};
const onSearch = (val: string) => {
  listTeams(val);
};
</script>

<template>
  <div id="teamCreatePage">
    <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch"/>
    <team-card-list :teamList="teamList"></team-card-list>
    <van-empty v-if="teamList?.length === 0" description="暂无队伍"/>
  </div>
</template>

<style scoped>

</style>