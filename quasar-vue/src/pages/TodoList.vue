<!-- TODO: refactor tables into their own components -->
<template>
  <div>
    <!-- Vue component to Add/Create a user -->
    <AddTodoTask v-on:updateData="reload" />
    <!-- Uncompleted Tasks Component -->
    <div class="q-pa-md">
      <q-table
        style="height: calc(100vh - 175px)"
        class="my-sticky-header-table"
        title="Todo Tasks"
        :data="uncompleted"
        :columns="columns"
        :filter="uncompletedfilter"
        :rows-per-page-options="[0]"
        row-key="todoTaskID"
      >
        <!-- Search Component -->
        <template v-slot:top-right>
          <q-input
            borderless
            dense
            debounce="300"
            v-model="uncompletedfilter"
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
            <!-- Selection Component -->
            <q-item-section side top>
              <q-checkbox
                v-model="props.row.completed"
                v-on:click.native="updateTask(props.row)"
              />
            </q-item-section>

            <!-- Description Name Column -->
            <q-td key="description" :props="props">
              {{ props.row.description }}
            </q-td>

            <!-- Priority column -->
            <q-td key="todoTaskPriority" :props="props">
              {{ props.row.todoTaskPriority }}
            </q-td>

            <!-- Due date column -->
            <q-td key="dueDate" :props="props">
              {{ props.row.dueDate }}
            </q-td>

            <!-- Action column -->
            <q-td key="action" :props="props">
              <!-- Edit button -->
              <q-btn color="green" dense flat icon="edit">
                <q-popup-edit
                  v-model="props.row"
                  title="Edit Todo Task"
                  buttons
                  @cancel="
                    (newValue, oldValue) => {
                      cancelEdit(newValue, oldValue);
                    }
                  "
                  @save="saveEdit(props.row)"
                >
                  <q-input
                    type="string"
                    v-model="props.row.description"
                    dense
                    autofocus
                    counter
                    lazy-rules
                    :rules="[
                      (val) =>
                        (val !== null && val !== '') ||
                        'Please enter a description',
                    ]"
                  />

                  <q-input filled v-model="props.row.dueDate" mask="####-##-##">
                    <template v-slot:append>
                      <q-icon name="event" class="cursor-pointer">
                        <q-popup-proxy
                          ref="qDateProxy"
                          transition-show="scale"
                          transition-hide="scale"
                        >
                          <q-date
                            label="Date (optional)"
                            v-model="props.row.dueDate"
                            @input="() => $refs.qDateProxy.hide()"
                            mask="YYYY-MM-DD"
                          ></q-date>
                        </q-popup-proxy>
                      </q-icon>
                    </template>
                  </q-input>

                  <q-select
                    type="string"
                    v-model="props.row.todoTaskPriority"
                    :options="options"
                    lazy-rules
                    :rules="[
                      (val) =>
                        (val !== null && val !== '') ||
                        'Please pick a priority',
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
                @click="deleteUncompletedRow(uncompleted.indexOf(props.row))"
              />
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>

    <!-- Completed Tasks Component -->
    <div class="q-pa-md">
      <q-table
        style="height: calc(100vh - 175px)"
        class="my-sticky-header-table"
        title="Completed Tasks"
        :data="completed"
        :columns="columns"
        :filter="completedfilter"
        :rows-per-page-options="[0]"
        row-key="todoTaskID"
      >
        <!-- Search Component -->
        <template v-slot:top-right>
          <q-input
            borderless
            dense
            debounce="300"
            v-model="completedfilter"
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
            <!-- Selection Component -->
            <q-item-section side top>
              <q-checkbox
                v-model="props.row.completed"
                v-on:click.native="updateTask(props.row)"
              />
            </q-item-section>
            <!-- Description Name Column -->
            <q-td key="description" :props="props">
              <del>{{ props.row.description }}</del>
            </q-td>

            <!-- Priority column -->
            <q-td key="todoTaskPriority" :props="props">
              {{ props.row.todoTaskPriority }}
            </q-td>

            <!-- Due date column -->
            <q-td key="dueDate" :props="props">
              {{ props.row.dueDate }}
            </q-td>

            <!-- Action column -->
            <q-td key="action" :props="props">
              <!-- Delete button -->
              <q-btn
                color="red"
                dense
                flat
                icon="delete"
                @click="deleteCompletedRow(completed.indexOf(props.row))"
              />
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>
  </div>
</template>

<script lang="ts">
import AddTodoTask from '../components/AddTodoTask';
import { TodoPriority } from '../types/TodoPriority';
import axios from 'axios';
import { Component, Vue } from 'vue-property-decorator';
@Component({
  components: {
    AddTodoTask,
  },
})
export default class Users extends Vue {
  done: boolean = false;
  options: any[] = Object.values(TodoPriority);
  completedfilter: string = '';
  uncompletedfilter: string = '';
  columns: any[] = [
    {
      name: 'description',
      required: true,
      label: 'Description',
      align: 'left',
      field: 'description',
      // format: (val) => `${val}`,
      sortable: false,
    },
    {
      name: 'todoTaskPriority',
      align: 'left',
      label: 'Priority',
      field: 'todoTaskPriority',
      sortable: true,
      sort: (a, b) => this.compare(a, b),
    },
    {
      name: 'dueDate',
      align: 'left',
      label: 'Due date',
      field: 'dueDate',
      sortable: true,
    },
    {
      name: 'action',
      label: 'Action',
      field: 'action',
      align: 'right',
    },
  ];
  completed: any[] = [];
  uncompleted: any[] = [];

  created() {
    this.reload();
  }
  // fetch uncompleted and completed todo tasks
  reload() {
    axios
      .get('http://localhost:8081/todotasks/completed')
      .then((response) => (this.completed = response.data))
      .catch(() => this.displayError);
    axios
      .get('http://localhost:8081/todotasks/uncompleted')
      .then((response) => (this.uncompleted = response.data))
      .catch(() => this.displayError);
  }
  deleteCompletedRow(index: number) {
    axios
      .delete(
        `http://localhost:8081/todotasks/delete/${this.completed[index].todoTaskID}`
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Todo Task successfully deleted')
          : this.displayError()
      )
      .catch(() => this.displayError());
    this.completed.splice(index, 1);
  }
  deleteUncompletedRow(index: number) {
    axios
      .delete(
        `http://localhost:8081/todotasks/delete/${this.uncompleted[index].todoTaskID}`
      )
      .then((response) =>
        response.data
          ? this.displaySuccess('Todo Task successfully deleted')
          : this.displayError()
      )
      .catch(() => this.displayError());
    this.uncompleted.splice(index, 1);
  }
  saveEdit(record: any) {
    if (!record.description || !record.todoTaskPriority) {
      this.cancelEdit();
      return;
    }
    axios
      .put(`http://localhost:8081/todotasks/${record.todoTaskID}`, record)
      .then((response) =>
        response.data
          ? this.displaySuccess('Todo Task successfully updated')
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
  updateTask(record: any) {
    axios
      .put(`http://localhost:8081/todotasks/${record.todoTaskID}`, record)
      .then()
      .catch(() => this.displayError());
    this.$forceUpdate();
  }
  compare(a, b) {
    const map = {};
    map['LOW'] = 1;
    map['MEDIUM'] = 2;
    map['HIGH'] = 3;
    if (map[a] < map[b]) {
      return -1;
    }

    if (map[a] > map[b]) {
      return 1;
    }
    return 0;
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