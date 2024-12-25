<template>
    <div class="course-tree">
      <ul>
        <li v-for="item in treeData" :key="item.id">
          <span 
            @click="toggle(item)" 
            :class="{ 
              'selected': selectedId === item.id,
              'open': item.isOpen,
              'has-children': item.children && item.children.length > 0,
              'chapter-item': isChapter(item),
              'completed': item.isCompleted
            }"
          >
            {{ item.name }}
            <span v-if="isChapter(item) && item.isCompleted" class="completed-icon">✓</span>
          </span>
          <template v-if="item.children">
            <ul v-if="isChapter(item)" class="chapter-list">
              <li v-for="child in item.children" :key="child.id">
                <span 
                  @click="selectNode(child.id)"
                  :class="{ 'selected': selectedId === child.id }"
                >
                  {{ child.name }}
                </span>
              </li>
            </ul>
            <CourseTree 
              v-else-if="item.isOpen"
              :treeData="item.children" 
              :selectedId="selectedId" 
              @select="selectNode" 
            />
          </template>
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    name: 'CourseTree',
    props: {
      treeData: {
        type: Array,
        required: true
      },
      selectedId: {
        type: [Number, String],
        default: null
      }
    },
    methods: {
      toggle(item) {
        console.log('点击章节:', item);
        if (!this.isChapter(item)) {
          item.isOpen = !item.isOpen;
        }
        this.selectNode(item.id);
      },
      selectNode(id) {
        console.log('选中节点ID:', id);
        this.$emit('select', id);
      },
      isChapter(item) {
        return item.children && item.children.every(child => !child.children);
      }
    }
  };
  </script>
  
  <style scoped>
  .course-tree {
    padding: 0;
  }
  
  .course-tree ul {
    list-style: none;
    padding-left: 0;
    margin: 0;
  }
  
  .course-tree li {
    margin: 8px 0;
  }
  
  .course-tree li span {
    display: block;
    padding: 8px 12px;
    border-radius: 4px;
    cursor: pointer;
    color: #606266;
    font-size: 14px;
    transition: all 0.3s;
  }
  
  .course-tree li span:hover {
    background-color: rgba(214, 204, 233, 0.2);
  }
  
  .course-tree li span.selected {
    background-color: rgba(214, 204, 233, 0.4) !important;
  }
  
  .completed {
    color: #67C23A;
  }
  
  .completed-icon {
    margin-left: 8px;
    color: #67C23A;
    font-weight: bold;
  }
  
  /* 子节点的样式 */
  .course-tree li li {
    padding-left: 20px;
  }
  
  /* 只给有子节点的项目添加展开/折叠图标 */
  .course-tree li span.has-children:not(.chapter-item)::before {
    content: '▶';
    display: inline-block;
    margin-right: 8px;
    font-size: 12px;
    transition: transform 0.3s;
    color: #909399;
  }
  
  .course-tree li span.has-children.open:not(.chapter-item)::before {
    transform: rotate(90deg);
  }
  
  /* 章节列表样式 */
  .chapter-list {
    margin-top: 4px !important;
  }
  
  .chapter-list li span {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  /* 章节标题样式 */
  .chapter-item {
    font-weight: 500;
    color: #303133;
  }
  </style>