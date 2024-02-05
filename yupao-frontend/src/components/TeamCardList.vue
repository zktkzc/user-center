<script setup lang="ts">

import {TeamType} from "../models/team";
import {TeamStatusEnum} from "../constants/team.ts";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {UserType} from "../models/user";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../services/user.ts";
import {useRouter} from "vue-router";

const router = useRouter();

interface TeamCardListProps {
  teamList: TeamType[]
}

const props = withDefaults(defineProps<TeamCardListProps>(), {
  // @ts-ignore
  teamList: []
});
onMounted(async () => {
  loginUser = await getCurrentUser()
})
const doJoinTeam = async () => {
  if (!joinTeamId.value) return;
  const res = await myAxios.post('/team/join',
      {
        teamId: joinTeamId.value,
        password: password.value
      });
  if (res?.data.code === 0) {
    showSuccessToast('加入队伍成功');
    showPasswordDialog.value = false;
    password.value = '';
    joinTeamId.value = null;
  } else {
    showFailToast('加入队伍失败：' + res.data.description);
  }
};
let loginUser: UserType;
const doUpdateTeam = (id: number) => {
  router.push({
    path: '/team/update',
    query: {
      id
    }
  })
};
const doDeleteTeam = async (id: number) => {
  const res = await myAxios.post('/team/delete', {teamId: id});
  if (res?.data.code === 0) {
    showSuccessToast('解散队伍成功');
  } else {
    showFailToast('解散队伍失败：' + res.data.description);
  }
};
const doQuitTeam = async (id: number) => {
  const res = await myAxios.post('/team/quit', {teamId: id});
  if (res?.data.code === 0) {
    showSuccessToast('退出队伍成功');
  } else {
    showFailToast('退出队伍失败：' + res.data.description);
  }
};
const showPasswordDialog = ref(false);
const password = ref('');
const joinTeamId = ref();
</script>

<template>
  <van-card v-for="team in props.teamList" :key="team.id"
            :title="`${team.name}`"
            thumb="https://static.vecteezy.com/system/resources/previews/000/649/142/original/team-icon-symbol-sign-vector.jpg"
            :desc="team.description">
    <template #tags>
      <van-tag plain type="success" round
               style="margin-right: 8px; margin-top: 8px;"
      >
        {{ TeamStatusEnum[team.status] }}
      </van-tag>
    </template>
    <template #bottom>
      成员数量：{{ team.members}}/{{ team.maxNum }} 人
      <div v-if="team.expireTime">
        过期时间：{{ team.expireTime }}
      </div>
      发布时间：{{ team.createTime }}
    </template>
    <template #footer>
      <van-button v-if="team.createUser?.id === loginUser.id" type="primary" size="mini" plain
                  @click="doUpdateTeam(team.id)">
        更新队伍
      </van-button>
      <van-button v-if="team.createUser?.id === loginUser.id" type="danger"
                  size="mini" plain
                  @click="doDeleteTeam(team.id)">
        解散队伍
      </van-button>
      <van-button v-if="team.createUser?.id !== loginUser.id && !team.hasJoin"
                  type="success" size="mini" plain
                  @click="() => {
                    joinTeamId = team.id;
                    if (team.status === 0)
                      doJoinTeam();
                    else
                      showPasswordDialog = true;
                  }">
        加入队伍
      </van-button>
      <van-button v-if="team.createUser?.id !== loginUser.id && team.hasJoin" type="danger" size="mini" plain @click="doQuitTeam(team.id)">
        退出队伍
      </van-button>
    </template>
  </van-card>
  <van-dialog v-model:show="showPasswordDialog" title="请输入密码"
              show-cancel-button @confirm="doJoinTeam"
              @cancel="() => {
                showPasswordDialog = false;
                password = '';
                joinTeamId = null;
              }">
    <van-field v-model="password" type="password" placeholder="请输入密码"/>
  </van-dialog>
</template>

<style scoped>

</style>