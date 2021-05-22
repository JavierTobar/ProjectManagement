<!-- Button Component -->
<template v-slot:top>
  <div>
    <div class="q-pa-md" style="max-width: 300px">
      <!--Button to expand/collapse user form -->
      <div class="q-pb-md">
        <q-btn
          class="q-ml-sm"
          color="primary"
          :label="showAddTodo ? 'Close' : 'Add Todo'"
          @click="toggleForm"
        />
      </div>

      <!-- We only show User Form if AddUser button has been toggled -->
      <div v-if="showAddTodo">
        <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
          <q-input
            v-model="description"
            label="Description"
            lazy-rules
            :rules="[
              (val) => (val && val.length > 0) || 'Please type something',
            ]"
          />
          <q-input filled v-model="dueDate" mask="####-##-##">
            <template v-slot:append>
              <q-icon name="event" class="cursor-pointer">
                <q-popup-proxy
                  ref="qDateProxy"
                  transition-show="scale"
                  transition-hide="scale"
                >
                  <q-date
                    label="Date (optional)"
                    v-model="dueDate"
                    @input="() => $refs.qDateProxy.hide()"
                    mask="YYYY-MM-DD"
                  ></q-date>
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>
          <q-select
            v-model="todoTaskPriority"
            label="Priority"
            :options="options"
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
import { TodoPriority } from '../types/TodoPriority';
import { Vue, Component } from 'vue-property-decorator';
@Component
export default class AddUser extends Vue {
  description: string = '';
  dueDate: string = '';
  todoTaskPriority: string = TodoPriority.MEDIUM;
  showAddTodo: boolean = false;
  options: string[] = Object.values(TodoPriority);

  onSubmit() {
    axios
      .post('http://localhost:8081/todotasks/', {
        description: this.description,
        dueDate: this.dueDate,
        todoTaskPriority: this.todoTaskPriority,
      })
      .then((response) =>
        response.data
          ? this.displaySuccess('Todo successfully created')
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
  getCurrentDate(): string {
    var today: any = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();
    today = yyyy + '-' + mm + '-' + dd;
    return today;
  }
  onReset() {
    this.description = '';
    this.dueDate = this.getCurrentDate();
    this.todoTaskPriority = TodoPriority.MEDIUM;
  }
  toggleForm() {
    this.showAddTodo = !this.showAddTodo;
    this.onReset();
  }
}
</script>

<style>
</style>