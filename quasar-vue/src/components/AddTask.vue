<!-- Button Component -->
<template v-slot:top>
  <div class="q-pa-md" style="max-width: 300px">
    <!--Button to expand/collapse Task form -->
    <div class="q-pb-md">
      <q-btn
        class="q-ml-sm"
        color="primary"
        :label="showAddTask ? 'Close' : 'Add Task'"
        @click="toggleForm"
      />
    </div>
    <!-- We only show Task Form if AddTask button has been toggled -->
    <div v-if="showAddTask">
      <q-form @submit="onSubmit" @reset="onReset" class="row q-gutter-md">
        <div>
          <div class="row sm-gutter">
            <div class="col-12">
              <q-select
                v-model="project"
                :options="projects"
                @input="fetchTasks()"
                label="Project"
                lazy-rules
                :rules="[
                  (val) => (val && val.length > 0) || 'Please make a selection',
                ]"
              />
            </div>
            <div class="col-12">
              <q-input
                inverted
                v-model="description"
                class="no-margin"
                label="Description (optional)"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-select
                inverted
                :options="tasks"
                v-model="predecessorID"
                class="no-margin"
                label="Predecessor ID"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-select
                inverted
                :options="tasks"
                v-model="successorID"
                class="no-margin"
                label="Successor ID"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="optimisticEstimation"
                class="no-margin"
                label="Optimistic est."
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') ||
                    'Please enter a task duration',
                  (val) => val >= 0 || 'Please enter a valid estimation',
                ]"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="optimisticWeight"
                class="no-margin"
                label="weight"
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') || 'Please enter a weight',
                  (val) => val >= 0 || 'Please enter a valid weight',
                ]"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="realisticEstimation"
                class="no-margin"
                label="Realistic est."
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') ||
                    'Please enter a task duration',
                  (val) => val >= 0 || 'Please enter a valid estimation',
                ]"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="realisticWeight"
                class="no-margin"
                label="weight"
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') || 'Please enter a weight',
                  (val) => val >= 0 || 'Please enter a valid weight',
                ]"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="pessimisticEstimation"
                class="no-margin"
                label="Pessimistic est."
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') ||
                    'Please enter a task duration',
                  (val) => val >= 0 || 'Please enter a valid estimation',
                ]"
              />
            </div>
            <div class="col-xs-12 col-sm-6">
              <q-input
                type="number"
                inverted
                v-model="pessimisticWeight"
                class="no-margin"
                label="weight"
                lazy-rules
                :rules="[
                  (val) =>
                    (val !== null && val !== '') || 'Please enter a weight',
                  (val) => val >= 0 || 'Please enter a valid weight',
                ]"
              />
            </div>
          </div>
        </div>

        <div>
          <q-btn class="q-ml-sm" label="Submit" type="submit" color="primary" />
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
</template>



<script lang="ts">
import axios from 'axios';
import { QVueGlobals } from 'quasar';
import { Vue, Component } from 'vue-property-decorator';

@Component
export default class AddTask extends Vue {
  project: string = '';
  description: string = '';
  successorID: string = '';
  predecessorID: string = '';
  optimisticEstimation: number = 0;
  realisticEstimation: number = 0;
  pessimisticEstimation: number = 0;
  optimisticWeight: number = 1;
  realisticWeight: number = 4;
  pessimisticWeight: number = 1;
  showAddTask: boolean = false;
  projects: string[] = [];
  tasks: string[] = [];

  // fetch projects
  created() {
    axios
      .get('http://localhost:8081/projects/')
      .then((response) =>
        response.data.forEach((project: any) =>
          this.projects.push(project.project)
        )
      )
      .catch(() => this.displayError);
  }
  onSubmit() {
    axios
      .post('http://localhost:8081/tasks/', {
        project_fk: this.project,
        description: this.description,
        predesessorTaskID: this.predecessorID,
        successorTaskID: this.successorID,
        optimisticWeight: this.optimisticWeight,
        optimisticEstimation: this.optimisticEstimation,
        realisticWeight: this.realisticWeight,
        realisticEstimation: this.realisticEstimation,
        pessimisticWeight: this.pessimisticWeight,
        pessimisticEstimation: this.pessimisticEstimation,
      })
      .then((response) =>
        response.data
          ? this.displaySuccess('Task successfully created')
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
  fetchTasks() {
    this.tasks = [];
    axios
      .get(`http://localhost:8081/projects/${this.project}/tasks`)
      .then((response) =>
        response.data.forEach((task: any) => this.tasks.push(task.taskID))
      )
      .catch(() => this.displayError);
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
    this.project = '';
    this.description = '';
    this.optimisticEstimation = 0;
    this.realisticEstimation = 0;
    this.pessimisticEstimation = 0;
    this.optimisticWeight = 1;
    this.realisticWeight = 4;
    this.pessimisticWeight = 1;
    this.successorID = '';
    this.predecessorID = '';
  }
  toggleForm() {
    this.showAddTask = !this.showAddTask;
    this.onReset();
  }
}
</script>

<style>
</style>