<script setup lang="ts">
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";

const initFormData = {
  description: "",
  expireTime: null,
  maxNum: 0,
  name: "",
  password: "",
  status: 0
};
// 需要用户填写的表单数据
const addTeamData = ref({...initFormData});
const showDatePicker = ref(false);
const showTimePicker = ref(false);
const now = new Date();
const minDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
const minTime = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
const router = useRouter();
const onSubmit = async () => {
  addTeamData.value.expireTime = new Date(selectedDate.value + ' ' + selectedTime.value);
  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.status)
  };
  const res = await myAxios.post('/team/add', postData);
  if (res?.data.code === 0 && res?.data.data) {
    showSuccessToast('创建成功');
    router.push({
      path: '/team',
      replace: true
    });
  } else {
    showFailToast('创建失败');
  }
};
const expireDate = ref([]);
const expireTime = ref([]);
const selectedDate = ref('');
const selectedTime = ref('');
const onDateConfirm = ({selectedValues}) => {
  showDatePicker.value = false;
  selectedDate.value = selectedValues.join('-');
};
const onTimeConfirm = ({selectedValues}) => {
  showTimePicker.value = false;
  selectedTime.value = selectedValues.join(':');
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="addTeamData.name"
          name="name"
          label="队伍名"
          placeholder="请输入队伍名"
          :rules="[
            { required: true, message: '请填写队伍名' }]"
      />
      <van-field
          v-model="addTeamData.description"
          type="textarea"
          rows="4"
          autosize
          name="description"
          label="队伍描述"
          placeholder="请输入队伍描述"
          :rules="[{ required: true, message: '请填写描述' }]"
      />
      <van-field
          v-model="addTeamData.maxNum"
          name="maxNum"
          label="最大人数"
      >
        <template #input>
          <van-stepper v-model="addTeamData.maxNum" integer min="3" max="10"/>
        </template>
      </van-field>
      <van-field
          v-model="selectedDate"
          is-link
          readonly
          label="过期日期"
          placeholder="请选择过期日期"
          @click="showDatePicker = true"
      />
      <van-popup v-model:show="showDatePicker" position="bottom">
        <van-date-picker
            v-model="expireDate"
            title="选择过期时间"
            :min-date="minDate"
            @confirm="onDateConfirm"
            @cancel="showDatePicker = false"
        />
      </van-popup>
      <van-field
          v-model="selectedTime"
          is-link
          readonly
          label="过期时间"
          placeholder="请选择过期时间"
          @click="showTimePicker = true"
      />
      <van-popup v-model:show="showTimePicker" position="bottom">
        <van-time-picker
            v-model="expireTime"
            title="选择过期时间"
            :min-time="minTime"
            @confirm="onTimeConfirm"
            @cancel="showTimePicker = false"
        />
      </van-popup>
      <van-field label="类型">
        <template #input>
          <van-radio-group v-model="addTeamData.status" direction="horizontal">
            <van-radio name="0">公开</van-radio>
            <van-radio name="1">私密</van-radio>
            <van-radio name="2">加密</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field
          v-if="Number(addTeamData.status) === 2"
          v-model="addTeamData.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        创建
      </van-button>
    </div>
  </van-form>
</template>

<style scoped>

</style>