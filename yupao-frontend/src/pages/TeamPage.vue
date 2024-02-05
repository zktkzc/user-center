<script setup lang="ts">

import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";

const router = useRouter();
const teamList = ref([]);
const searchText = ref('');
const doCreateTeam = () => {
  router.push('/team/add');
};
onMounted(() => {
  listTeams();
});
const listTeams = async (val: string = '', status: number = 0) => {
  const res = await myAxios.get('/team/list', {
    params: {
      searchText: val,
      status: status
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
const active = ref<string>('public');
const onTagChange = (name: string) => {
  if (name === 'public') {
    listTeams(searchText.value, 0);
  } else {
    listTeams(searchText.value, 2);
  }
};
</script>

<template>
  <div id="teamPage">
    <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch"/>
    <van-tabs v-model:active="active" @change="onTagChange">
      <van-tab title="公开" name="public"/>
      <van-tab title="加密" name="secret"/>
    </van-tabs>
    <van-button size="small" icon="plus" class="add-button" type="primary" @click="doCreateTeam"/>
    <team-card-list :teamList="teamList"></team-card-list>
    <van-empty v-if="teamList?.length === 0" description="暂无队伍"/>
  </div>
</template>

<style scoped>

</style>