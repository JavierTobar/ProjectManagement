<template>
  <div>
    <!-- Vue component to Add/Create a user -->
    <AddUser v-on:updateData="reload" />
    <div class="q-pa-md">
      <q-table
        style="height: calc(100vh - 175px)"
        class="my-sticky-header-table"
        title="Users"
        :data="data"
        :columns="columns"
        :filter="filter"
        :rows-per-page-options="[0]"
        row-key="employeeID"
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
            <!-- Employee Name Column -->
            <q-td key="employeeName" :props="props">
              {{ props.row.name }}
            </q-td>
            <!-- employeeID column -->
            <q-td key="employeeID" :props="props">
              {{ props.row.employeeID }}
            </q-td>

            <!-- Action column -->
            <q-td key="action" :props="props">
              <!-- Edit button -->
              <q-btn color="green" dense flat icon="edit">
                <q-popup-edit
                  v-model="props.row.name"
                  title="Edit name"
                  buttons
                  @cancel="
                    (newValue, oldValue) => {
                      cancelEdit();
                    }
                  "
                  @save="saveEdit(props.row)"
                >
                  <q-input
                    type="string"
                    v-model="props.row.name"
                    dense
                    autofocus
                    counter
                    lazy-rules
                    :rules="[
                      (val) =>
                        (val !== null && val !== '') ||
                        'Please enter a valid name',
                    ]"
                  />
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
              <div v-if="props.row.projectnames.length > 0">
                <div class="text-left">
                  <b>Projects assigned:</b>
                  <div
                    v-for="projectnames in props.row.projectnames"
                    :key="projectnames"
                  >
                    {{ projectnames }}
                  </div>
                </div>
              </div>
              <div v-else>
                <div class="text-left">
                  <b>No projects currently assigned.</b>
                </div>
              </div>
              <!-- Task section -->
              <div v-if="props.row.tasks.length > 0">
                <div class="text-left">
                  <b>Tasks assigned:</b>
                  <div v-for="task in props.row.tasks" :key="task.taskID">
                    {{ task.taskID }}
                  </div>
                </div>
              </div>
              <div v-else>
                <div class="text-left"><b>No tasks currently assigned.</b></div>
              </div>
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>
  </div>
</template>

<script lang="ts">
import AddUser from '../components/AddUser';
import axios from 'axios';
import { Component, Vue } from 'vue-property-decorator';
import { EmployeeGetDTO } from '../types/Interfaces';
@Component({
  components: {
    AddUser,
  },
})
export default class Users extends Vue {
  filter: string = '';
  columns: any[] = [
    {
      name: 'employeeName',
      required: true,
      label: 'Name',
      align: 'left',
      field: 'name',
      sortable: true,
    },
    {
      name: 'employeeID',
      align: 'left',
      label: 'Employee ID',
      field: 'employeeID',
      sortable: true,
    },
    {
      name: 'action',
      label: 'Action',
      field: 'action',
      align: 'right',
    },
  ];
  data: EmployeeGetDTO[] = [];

  created() {
    this.reload();
  }

  reload() {
    axios
      .get('http://localhost:8081/employees/')
      .then((response) => (this.data = response.data))
      .catch(() => this.displayError);
  }
  deleteRow(index: number) {
    axios
      .delete(
        `http://localhost:8081/employees/delete/${this.data[index].employeeID}`
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('User successfully deleted')
          : this.displayError()
      )
      .catch(() => this.displayError());
    this.data.splice(index, 1);
  }
  saveEdit(record: any) {
    if (!record.name) {
      this.cancelEdit();
      return;
    }
    axios
      .put(`http://localhost:8081/employees/${record.employeeID}`, record)
      .then((response) =>
        response.data
          ? this.displaySuccess('User successfully updated')
          : this.displayError()
      )
      .catch(() => this.displayError());
  }
  cancelEdit() {
    this.reload();
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

<style lang="sass">
.my-sticky-header-table
  height: 310px

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th
    background-color: #d3dde6

  thead tr th
    position: sticky
    z-index: 1
  thead tr:first-child th
    top: 0

  /* this is when the loading indicator appears */
  &.q-table--loading thead tr:last-child th
    /* height of all previous header rows */
    top: 48px
</style>