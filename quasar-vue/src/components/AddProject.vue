<!-- Button Component -->
<template v-slot:top>
  <div>
    <div class="q-pa-md" style="max-width: 300px">
      <!--Button to expand/collapse project form -->
      <div class="q-pb-md">
        <q-btn
          class="q-ml-sm"
          color="primary"
          :label="showAddProject ? 'Close' : 'Add Project'"
          @click="toggleForm"
        />
      </div>

      <!-- We only show Project Form if AddProject button has been toggled -->
      <div v-if="showAddProject">
        <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
          <q-input
            v-model="project"
            label="Project Name"
            lazy-rules
            :rules="[
              (val) => (val && val.length > 0) || 'Please type something',
            ]"
          />
          <q-input
            v-model="description"
            label="Description"
            lazy-rules
            :rules="[
              (val) => (val && val.length > 0) || 'Please type something',
            ]"
          />
          <q-input
            v-model="devsLimit"
            type="number"
            label="Max Devs"
            lazy-rules
            :rules="[
              (val) =>
                (val !== null && val !== '') || 'Please enter a limit for devs',
              (val) => val >= 0 || 'Please enter a valid limit',
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
import { ProjectStatus } from '../types/ProjectStatus';
import { Project } from '../types/Interfaces';
import { QVueGlobals } from 'quasar';
import axios from 'axios';
import { Vue, Component } from 'vue-property-decorator';
@Component
export default class AddProject extends Vue {
  project: string = '';
  description: string = '';
  devsLimit: number = 0;
  showAddProject: boolean = false;
  options: ProjectStatus[] = Object.values(ProjectStatus);
  $q: QVueGlobals;

  onSubmit(): void {
    axios
      .post('http://localhost:8081/projects/', {
        project: this.project,
        description: this.description,
        devsLimit: this.devsLimit,
        status: ProjectStatus.NOT_STARTED,
        startDate: new Date().toISOString().slice(0, 10),
      })
      .then((response) =>
        response.data
          ? this.displaySuccess('Project successfully created')
          : this.displayError('Error')
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
  displayError(message: string): void {
    this.$q.notify({
      color: 'red-5',
      textColor: 'white',
      icon: 'error',
      message: message,
    });
  }
  onReset(): void {
    this.project = '' as string;
    this.description = '' as string;
    this.devsLimit = 0 as number;
  }
  toggleForm(): void {
    this.showAddProject = !this.showAddProject;
    this.onReset();
  }
}
</script>

<style>
</style>