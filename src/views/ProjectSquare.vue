<template>
  <div class="project-square">
    <h1>欢迎来到项目广场</h1>
  </div>
</template>

<script>
export default {
  name: 'ProjectSquare'
}
</script>

<style scoped>
.project-square {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
}

h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  text-align: center;
}
</style>
