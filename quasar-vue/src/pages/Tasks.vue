<template>
  <div>
    <!-- Vue component to Add/Create a task -->
    <AddTask v-on:updateData="reload" :listOfTasks="data" />
    <div class="q-pa-md">
      <q-table
        style="height: calc(100vh - 175px)"
        class="my-sticky-header-table"
        title="Tasks"
        :data="data"
        :columns="columns"
        :filter="filter"
        :rows-per-page-options="[0]"
        row-key="taskID"
      >
        <!-- Search Component -->
        <template v-slot:top-right>
          <q-input
            borderless
            dense
            debounce="300"
            v-model="filter"
            placeholder="Search"
          >
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </template>

        <!-- Table format / indents -->
        <template v-slot:header="props">
          <q-tr :props="props">
            <q-th auto-width />
            <q-th v-for="col in props.cols" :key="col.name" :props="props">
              {{ col.label }}
            </q-th>
          </q-tr>
        </template>

        <!-- Table Component -->
        <template v-slot:body="props">
          <q-tr :props="props">
            <q-td auto-width>
              <q-btn
                size="sm"
                color="secondary"
                round
                dense
                @click="props.expand = !props.expand"
                :icon="props.expand ? 'remove' : 'add'"
              />
            </q-td>
            <!-- TaskID Column -->
            <q-td key="taskID" :props="props">
              {{ props.row.taskID }}
            </q-td>
            <!-- Description column -->
            <q-td key="description" :props="props">
              {{ props.row.description }}
            </q-td>

            <!-- Duration column (round to 2 decimals) -->
            <q-td key="duration" :props="props">
              {{ props.row.duration }}
            </q-td>
            <!-- Action column -->
            <q-td key="action" :props="props">
              <!-- Edit button -->
              <q-btn color="green" dense flat icon="edit">
                <q-popup-edit
                  v-model="props.row"
                  title="Edit record"
                  buttons
                  multiple
                  @cancel="
                    (newValue, oldValue) => {
                      cancelEdit(newValue, oldValue);
                    }
                  "
                  @save="
                    (newValue, oldValue) => {
                      saveEdit(newValue, oldValue);
                    }
                  "
                >
                  <div>
                    <div class="row sm-gutter">
                      <div class="col-12">
                        <q-input
                          inverted
                          v-model="props.row.description"
                          class="no-margin"
                          label="Description (optional)"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.optimisticEstimation"
                          class="no-margin"
                          label="Optimistic est."
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a task duration',
                            (val) =>
                              val >= 0 || 'Please enter a valid estimation',
                          ]"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.optimisticWeight"
                          class="no-margin"
                          label="weight"
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a weight',
                            (val) => val >= 0 || 'Please enter a valid weight',
                          ]"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.realisticEstimation"
                          class="no-margin"
                          label="Realistic est."
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a task duration',
                            (val) =>
                              val >= 0 || 'Please enter a valid estimation',
                          ]"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.realisticWeight"
                          class="no-margin"
                          label="weight"
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a weight',
                            (val) => val >= 0 || 'Please enter a valid weight',
                          ]"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.pessimisticEstimation"
                          class="no-margin"
                          label="Pessimistic est."
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a task duration',
                            (val) =>
                              val >= 0 || 'Please enter a valid estimation',
                          ]"
                        />
                      </div>
                      <div class="col-xs-12 col-sm-6">
                        <q-input
                          type="number"
                          inverted
                          v-model="props.row.pessimisticWeight"
                          class="no-margin"
                          label="weight"
                          lazy-rules
                          :rules="[
                            (val) =>
                              (val !== null && val !== '') ||
                              'Please enter a weight',
                            (val) => val >= 0 || 'Please enter a valid weight',
                          ]"
                        />
                      </div>
                    </div>
                  </div>
                </q-popup-edit>
              </q-btn>
              <!-- Delete button -->
              <q-btn
                color="red"
                dense
                flat
                icon="delete"
                @click="deleteRow(data.indexOf(props.row))"
              />
            </q-td>
          </q-tr>

          <!-- Expand button -->
          <q-tr v-show="props.expand" :props="props">
            <q-td colspan="100%">
              <!-- Project section -->
              <div class="text-left">
                <b>Project:</b> {{ props.row.project_fk }}
              </div>
              <!-- Assignees section -->
              <div v-if="props.row.employee_fk_id">
                <div class="text-left">
                  <b>AssigneeID:</b> {{ props.row.employee_fk_id }}
                </div>
              </div>
              <div v-else>
                <div class="text-left">
                  <b>There is currently no assignee.</b>
                </div>
              </div>
              <!-- Predesessor Task section -->
              <div class="text-left">
                <b>Predesessor: </b>
                <div v-if="props.row.predesessorTaskID">
                  {{ props.row.predesessorTaskID }}
                </div>
                <div v-else>none</div>
              </div>
              <!-- Successor section -->
              <div class="text-left">
                <b>Successor: </b>
                <div v-if="props.row.successorTaskID">
                  {{ props.row.successorTaskID }}
                </div>
                <div v-else>none</div>
              </div>
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>
  </div>
</template>

<script lang="ts">
import AddTask from '../components/AddTask';
import { Component, Vue } from 'vue-property-decorator';
import axios from 'axios';
@Component({
  components: {
    AddTask,
  },
})
export default class Tasks extends Vue {
  filter: string = '';
  columns: any[] = [
    {
      name: 'taskID',
      required: true,
      label: 'Task ID',
      align: 'left',
      field: 'taskID',
      // format: (val) => `${val}`,
      sortable: true,
    },
    {
      name: 'description',
      align: 'left',
      label: 'Description',
      field: 'description',
      sortable: false,
    },
    {
      name: 'duration',
      align: 'left',
      label: 'Duration Estimate (hours)',
      field: 'duration',
      sortable: true,
    },
    {
      name: 'action',
      label: 'Action',
      field: 'action',
      align: 'right',
    },
  ];
  data: any[] = [];

  created() {
    this.reload();
  }
  reload() {
    axios
      .get('http://localhost:8081/tasks/')
      .then((response) => (this.data = response.data))
      .catch(() => this.displayError);
  }
  saveEdit(record: any) {
    axios
      .put(`http://localhost:8081/tasks/${record.taskID}`, record)
      .then((response) =>
        response.data
          ? this.displaySuccess('User successfully updated')
          : this.displayError()
      )
      .catch(() => this.displayError())
      .finally(this.reload);
  }
  cancelEdit() {
    this.reload();
  }
  deleteRow(index: number) {
    axios
      .delete(`http://localhost:8081/tasks/delete/${this.data[index].taskID}`)
      .then((response) =>
        response.data
          ? this.displaySuccess('Task successfully deleted')
          : this.displayError()
      )
      .catch(() => this.displayError());
    this.data.splice(index, 1);
  }
  displayError() {
    this.$q.notify({
      color: 'red-5',
      textColor: 'white',
      icon: 'error',
      message: 'There was an error in the backend',
    });
  }
  displaySuccess(message: string) {
    this.$q.notify({
      color: 'green-4',
      textColor: 'white',
      icon: 'cloud_done',
      message: message,
    });
  }
}
</script>

<style>
</style>