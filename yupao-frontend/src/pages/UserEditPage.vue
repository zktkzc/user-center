<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/user.ts";
import {UserType} from "../models/user";

const route = useRoute();
const router = useRouter();
const editUser = ref({
  editKey: route.query.editKey,
  currentValue: route.query.currentValue,
  editName: route.query.editName
});
let currentUser: UserType;
onMounted(async () => {
  currentUser = await getCurrentUser();
  if (currentUser) {
    showSuccessToast('获取用户信息成功');
  } else {
    showFailToast('未登录');
    router.push('/user/login');
  }
})
const onSubmit = async () => {
  const res = await myAxios.post('/user/update', {
    'id': currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue
  })
  if (res.data.code === 0 && res.data.data) {
    showSuccessToast('修改成功');
    router.back();
  } else {
    showFailToast('修改失败');
  }
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-field
        v-model="editUser.currentValue"
        :name="editUser.editKey"
        :label="editUser.editName"
        :placeholder="`请输入${editUser.editName}`"
    />
    <div style="margin: 10px;">
      <van-button round block type="primary" native-type="submit">提交</van-button>
    </div>
  </van-form>
</template>

<style scoped>

</style>