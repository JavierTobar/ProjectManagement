<!-- Button Component -->
<template v-slot:top>
  <div>
    <div class="q-pa-md" style="max-width: 300px">
      <div class="center q-pb-md">Project Assignment</div>

      <q-form @reset="onReset" class="q-gutter-md">
        <q-select
          v-model="project"
          :options="this.projects"
          label="Project Name"
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
            @click="assignProject"
            color="primary"
          />
          <q-btn
            class="q-ml-sm"
            label="Unassign"
            @click="unassignProject"
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
export default class ProjectWorkAssignment extends Vue {
  project: string = '';
  employeeID: number = null;
  projects: string[] = [];

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
  assignProject() {
    axios
      .put(
        `http://localhost:8081/employees/${this.employeeID}/assignproject/${this.project}`,
        {}
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Successfully assigned user')
          : this.displayError('Project is currently full')
      )
      .catch(() => this.displayError('Error'));
  }
  unassignProject() {
    axios
      .put(
        `http://localhost:8081/employees/${this.employeeID}/unassignproject/${this.project}`,
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
    this.project = null;
    this.employeeID = null;
  }
}
</script>

<style>
.center {
  text-align: center;
}
</style>