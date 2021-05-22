<template class="container">
  <div>
    <div class="one"><TaskWorkAssignment /></div>
    <div class="two"><ProjectWorkAssignment /></div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TaskWorkAssignment from '../components/TaskWorkAssignment';
import ProjectWorkAssignment from '../components/ProjectWorkAssignment';
@Component({
  components: {
    ProjectWorkAssignment,
    TaskWorkAssignment,
  },
})
export default class WorkAssignment extends Vue {}
</script>

<style>
.container {
  width: 80%;
  height: 200px;
  margin: auto;
  padding: 10px;
}

.one {
  width: 30%;
  height: 200px;
  float: left;
}

.two {
  margin-left: 30%;
  height: 200px;
}
</style>