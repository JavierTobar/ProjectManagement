<!-- Button Component -->
<template v-slot:top>
  <div>
    <div class="q-pa-md" style="max-width: 300px">
      <!--Button to expand/collapse user form -->
      <div class="q-pb-md">
        <q-btn
          class="q-ml-sm"
          color="primary"
          :label="showAddUser ? 'Close' : 'Add User'"
          @click="toggleForm"
        />
      </div>

      <!-- We only show User Form if AddUser button has been toggled -->
      <div v-if="showAddUser">
        <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
          <q-input
            v-model="employeeName"
            label="Employee Name"
            hint="first name and last name"
            lazy-rules
            :rules="[
              (val) => (val && val.length > 0) || 'Please type something',
            ]"
          />
          <div>
            <q-btn
              class="q-ml-sm"
              label="Submit"
              type="submit"
              color="primary"
            />
            <q-btn
              label="Reset"
              type="reset"
              color="primary"
              flat
              class="q-ml-sm"
            />
          </div>
        </q-form>
      </div>
    </div>
  </div>
</template>



<script lang="ts">
import axios from 'axios';
import { QVueGlobals } from 'quasar';
import { Vue, Component } from 'vue-property-decorator';
import { EmployeePostDTO } from '../types/Interfaces';
@Component
export default class AddUser extends Vue {
  employeeName: string = '';
  showAddUser: boolean = false;

  onSubmit() {
    const employee: EmployeePostDTO = {
      name: this.employeeName,
    };
    axios
      .post('http://localhost:8081/employees/', employee)
      .then((response) =>
        response.status == 201
          ? this.displaySuccess('User successfully created')
          : this.displayError()
      )
      .catch(() => this.displayError());
  }
  displaySuccess(message: string) {
    this.$emit('updateData');
    this.$q.notify({
      color: 'green-4',
      textColor: 'white',
      icon: 'cloud_done',
      message: message,
    });
  }
  displayError() {
    this.$q.notify({
      color: 'red-5',
      textColor: 'white',
      icon: 'error',
      message: 'There was an error in the backend',
    });
  }
  onReset() {
    this.employeeName = '';
  }
  toggleForm() {
    this.showAddUser = !this.showAddUser;
    this.onReset();
  }
}
</script>

<style>
</style>