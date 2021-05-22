<template>
  <div>
    <!-- Vue component to Add/Create a project -->
    <AddProject v-on:updateData="reload" />
    <div class="q-pa-md">
      <q-table
        style="height: calc(100vh - 175px)"
        class="my-sticky-header-table"
        title="Projects"
        :data="data"
        :columns="columns"
        :filter="filter"
        :rows-per-page-options="[0]"
        row-key="project"
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
            <!-- Project Column -->
            <q-td key="project" :props="props">
              {{ props.row.project }}
            </q-td>
            <!-- Description column -->
            <q-td key="description" :props="props">
              {{ props.row.description }}
            </q-td>

            <!-- Start date column -->
            <q-td key="startDate" :props="props">
              {{ props.row.startDate }}
            </q-td>

            <!-- Status column -->
            <q-td key="status" :props="props">
              {{ props.row.status.toString() }}
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
                    () => {
                      cancelEdit();
                    }
                  "
                  @save="
                    (newValue) => {
                      saveEdit(newValue);
                    }
                  "
                >
                  <div>
                    <q-input
                      v-model="props.row.description"
                      label="Description"
                      lazy-rules
                      :rules="[
                        (val) =>
                          (val && val.length > 0) || 'Please type something',
                      ]"
                    />
                    <q-select
                      v-model="props.row.status"
                      :options="options"
                      label="Project Status"
                      lazy-rules
                      :rules="[
                        (val) =>
                          (val && val.length > 0) || 'Please make a selection',
                      ]"
                    />
                    <q-input
                      type="number"
                      v-model="props.row.devsLimit"
                      label="Max number of devs"
                      lazy-rules
                      :rules="[
                        (val) =>
                          (val >= 0 && val.length) ||
                          'Please enter a valid number',
                      ]"
                    />
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
              <!-- Maximum number of devs section -->
              <div class="text-left">
                <b>Maximum number of devs:</b> {{ props.row.devsLimit }}
              </div>
              <!-- Assignee(s) section -->
              <div v-if="props.row.employees.length > 0">
                <div class="text-left">
                  <b>Assignee(s):</b>
                  <div
                    v-for="employee in props.row.employees"
                    :key="employee.name"
                  >
                    {{ employee.name + ` (ID: ${employee.employeeID})` }}
                  </div>
                </div>
              </div>
              <div v-else>
                <div class="text-left">
                  <b>No Assignee(s).</b>
                </div>
              </div>
              <!-- Tasks associated with project section -->
              <div class="text-left">
                <div v-if="props.row.tasks.length > 0">
                  <div class="text-left">
                    <b>Task(s)</b>
                    <div v-for="task in props.row.tasks" :key="task.taskID">
                      {{ task.taskID }}
                    </div>
                  </div>
                </div>
                <div v-else>
                  <div class="text-left">
                    <b>No Task(s).</b>
                  </div>
                </div>
              </div>
              <!--  Critical cost section -->
              <div v-if="props.row.criticalCost >= 0" class="text-left">
                <b>Critical Cost:</b> {{ props.row.criticalCost }} hours
              </div>
              <div v-else>
                <b>Critical Cost not available</b>
              </div>
              <!--  Critical path section -->
              <div v-if="props.row.criticalCost > 0" class="text-left">
                <b>Critical Path:</b>
                <div v-for="task in props.row.criticalPath" :key="task">
                  {{ task }}
                </div>
              </div>
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>
  </div>
</template>
<script lang="ts">
import { ProjectStatus } from '../types/ProjectStatus';
import AddProject from '../components/AddProject';
import { Component, Vue } from 'vue-property-decorator';
import axios from 'axios';
@Component({
  components: {
    AddProject,
  },
})
export default class Projects extends Vue {
  filter: string = '';
  errorMessageName: string = '';
  errorName: boolean = false;
  options: ProjectStatus[] = Object.values(ProjectStatus);
  columns: any[] = [
    {
      name: 'project',
      required: true,
      label: 'Project',
      align: 'left',
      field: 'project',
      // format: (val) => `${val}`;
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
      name: 'startDate',
      align: 'left',
      label: 'Start Date',
      field: 'startDate',
      sortable: true,
    },
    {
      name: 'status',
      align: 'left',
      label: 'Status',
      field: 'status',
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
      .get('http://localhost:8081/projects/')
      .then((response) => (this.data = response.data))
      .catch(() => this.displayError);
  }
  saveEdit(record: any) {
    axios
      .put(`http://localhost:8081/projects/${record.project}`, record)
      .then((response) =>
        response.data
          ? this.displaySuccess('Project successfully updated')
          : this.displayError()
      )
      .catch(() => this.displayError());
  }
  cancelEdit() {
    this.reload();
  }
  deleteRow(index: number) {
    axios
      .delete(
        `http://localhost:8081/projects/delete/${this.data[index].project}`
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Project successfully deleted')
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