<!-- Button Component -->
<template v-slot:top>
  <div>
    <div class="q-pa-md" style="max-width: 300px">
      <div class="center q-pb-md">Task Assignment</div>

      <q-form @reset="onReset" class="q-gutter-md">
        <q-select
          v-model="project"
          :options="this.projects"
          label="Project Name"
          @input="fetchTasks()"
          lazy-rules
          :rules="[(val) => (val && val.length > 0) || 'Please type something']"
        />
        <q-select
          :options="this.tasks"
          v-model="taskID"
          label="TaskID"
          lazy-rules
          :rules="[(val) => (val && val.length > 0) || 'Please type something']"
        />
        <q-input
          v-model="employeeID"
          type="number"
          label="Employee ID"
          lazy-rules
          :rules="[
            (val) =>
              (val !== null && val !== '') || 'Please enter an employeeID',
            (val) => val >= 0 || 'Please enter a valid employeeID',
          ]"
        />

        <div>
          <q-btn
            class="q-ml-sm"
            label="Assign"
            @click="assignTask"
            color="primary"
          />
          <q-btn
            class="q-ml-sm"
            label="Unassign"
            @click="unassignTask"
            color="primary"
          />
          <!-- <q-btn
              label="Reset"
              type="reset"
              color="primary"
              flat
              class="q-ml-sm"
            /> -->
        </div>
      </q-form>
    </div>
  </div>
</template>



<script lang="ts">
import axios from 'axios';
import { Vue, Component } from 'vue-property-decorator';

@Component
export default class TaskWorkAssignment extends Vue {
  taskID: string = '';
  employeeID: number = null;
  tasks: string[] = [];
  projects: string[] = [];
  project: string = '';
  created() {
    axios
      .get('http://localhost:8081/projects/')
      .then((response) =>
        response.data.forEach((project: any) => {
          this.projects.push(project.project);
          this.projects.sort();
        })
      )
      .catch(() => this.displayError);
  }
  assignTask() {
    axios
      .put(
        `http://localhost:8081/employees/${this.employeeID}/assigntask/${this.taskID}`,
        {}
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Successfully assigned user')
          : this.displayError("Employee does not belong to the task's project")
      )
      .catch(() => this.displayError('Error'));
  }
  fetchTasks() {
    this.tasks = [];
    axios
      .get(`http://localhost:8081/projects/${this.project}/tasks`)
      .then((response) => {
        response.data.forEach((task: any) => this.tasks.push(task.taskID));
        this.tasks.sort();
      })
      .catch(() => this.displayError);
  }
  unassignTask() {
    axios
      .put(
        `http://localhost:8081/employees/${this.employeeID}/unassigntask/${this.taskID}`,
        {}
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Successfully unassigned user')
          : this.displayError('Error in the backend')
      )
      .catch(() => this.displayError('Error'));
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
  displayError(message: string) {
    this.$q.notify({
      color: 'red-5',
      textColor: 'white',
      icon: 'error',
      message: message,
    });
  }
  onReset() {
    this.taskID = null;
    this.employeeID = null;
  }
}
</script>

<style>
.center {
  text-align: center;
}
</style>