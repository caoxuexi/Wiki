<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="6">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  查询
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add()">
                  新增
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <h3 v-if="levels.length === 0">目前项目并未创建文档</h3>
          <a-table
              v-if="levels.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="levels"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{ text }}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="18">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
                <SyncOutlined spin style="margin-left: 20px" />
                <span v-if="doc.id">当前正在编辑：{{editingDocName}}</span>
                <span v-if="!doc.id">当前正在新增文档</span>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined/>
                内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <!-- 没有其它人在操作的时候才显示-->
              <div id="content" v-show="!isSomeoneOperating"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>
    </a-layout-content>
  </a-layout>
</template>

<script>
import axios from "axios";
import {message, Modal} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import {createVNode} from "vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import E from 'wangeditor'

const columns = [
  {
    title: '名称',
    dataIndex: 'name',
    slots: {customRender: 'name'}
  },
  {
    title: 'Action',
    key: 'action',
    slots: {customRender: 'action'}
  }
];

//删除时需要的参数
const deleteIds = [];
const deleteNames = [];


export default {
  name: 'AdminDoc',
  components: {},
  methods: {
    handleQuery() {
      this.loading = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      this.levels = [];
      axios.get("/doc/all/" + this.route.query.ebookId).then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.docs = data.content;
          // console.log("原始数组：", this.docs);

          this.levels = [];
          this.levels = Tool.array2Tree(this.docs, 0);
          // console.log("树形结构：", this.levels);

          //父文档下拉框初始化，相当于点击新增,如果levels没数据则使用[]
          this.treeSelectData = Tool.copy(this.levels) || []
          // 为选择树添加一个"无"
          this.treeSelectData.unshift({id: 0, name: '无'});
        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 对话框选择确定的逻辑，即保存数据
     */
    handleSave() {
      this.doc.content = this.editor.txt.html();
      axios.post("/doc/save", this.doc).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          message.success("保存成功！");
          // 重新加载列表
          this.handleQuery();
          //更新正在操作的文档名(当我们修改了文档名进行保存的时候)
          this.editingDocName=this.doc.name

        } else {
          message.error(data.message);
        }
      });
    },
    /**
     * 删除文档
     */
    handleDelete(id) {
      // 清空数组，否则多次删除时，数组会一直增加
      this.deleteIds.length = 0;
      this.deleteNames.length = 0;
      this.getDeleteIds(this.levels, id);
      //用that指代this，这样onOk中才不会因为指代变化导致方法无法使用
      let that = this
      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          // console.log(ids)
          axios.delete("/doc/delete/" + deleteIds).then((response) => {
            const data = response.data; // data = commonResp
            console.log('delete方法返回的data', data)
            if (data.success) {
              // 重新加载列表
              that.handleQuery();
            } else {
              message.error(data.message);
            }
          });
        },
      });
    },
    /**
     * 新增文档
     */
    add() {
      // 清空富文本框
      this.editor.txt.html("")
      this.doc={}

      //全部都能选
      this.treeSelectData = Tool.copy(this.levels) || [];

      // 为选择树添加一个"无"
      this.treeSelectData.unshift({id: 0, name: '无'});
    },
    /**
     * 点击编辑按钮触发的方法
     */
    edit(record) {
      //如果直接把record的值赋给this.doc则会出现修改doc致record也修改的情况，
      // 所以我们更希望是一个深拷贝
      this.doc = Tool.copy(record);
      this.handleQueryContent()

      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      this.treeSelectData = Tool.copy(this.levels);
      this.setDisable(this.treeSelectData, record.id);
      this.editingDocName=this.doc.name
      // 为选择树添加一个"无"（unshift是往数组的前面添加一个项）
      this.treeSelectData.unshift({id: 0, name: '无'});
    },
    /**
     * 将某节点及其子孙节点全部置为disabled（为文档选择父文档时）
     * @param treeSelectData
     * @param id
     */
    setDisable(treeSelectData, id) {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              this.setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            this.setDisable(children, id);
          }
        }
      }
    },
    /**
     * 查找整个树枝(用于删除)
     * @param treeSelectData
     * @param id
     */
    getDeleteIds(treeSelectData, id) {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // console.log("delete", node);
          // 将目标ID放入结果集ids
          node.disabled = true;
          this.deleteIds.push(id);
          this.deleteNames.push(node.name);

          // 遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              this.getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            this.getDeleteIds(children, id);
          }
        }
      }
    },
    /**
     * 获取文档内容
     */
    handleQueryContent() {
      axios.get("/doc/find-content/" + this.doc.id).then((response) => {
        const data = response.data;
        if (data.success) {
          this.editor.txt.html(data.content)
        } else {
          message.error(data.message);
        }
      });
    },
    handlePreviewContent() {
      const html = this.editor.txt.html();
      this.previewHtml = html;
      this.drawerVisible = true;

    },
    onDrawerClose() {
      this.drawerVisible = false;
    }
  },
  mounted() {
    this.route = useRoute()
    this.doc = {
      ebookId: this.route.query.ebookId
    };
    this.handleQuery()
    this.editor = new E('#content')
    //设置层优先级(0的话，它之上就没有东西可以覆盖它)，这样我们选择父分类时就不会挡住富文本编辑器了
    this.editor.config.zIndex = 0;
    this.editor.create();

  },
  data() {
    return {
      columns: columns,
      // 文档相关
      loading: false,
      docs: [],
      //搜索的参数（先保留）
      param: {},
      //表单相关
      doc: {},
      docIds: "",
      //按等级分级后的菜单项，保持不变
      levels: [],
      //levels的数据拷贝，里面的属性是需要变化的
      treeSelectData: [],
      //前端传进来的路径参数
      route: "",
      deleteIds: deleteIds,
      deleteNames: deleteNames,
      //富文本编辑器
      editor: {},
      //是否展示内容预览
      drawerVisible: false,
      //内容预览用的数据
      previewHtml: "",
      //是否已经有人在操作文档了
      isSomeoneOperating: false,
      //正在操作的文档名
      editingDocName:""
    }
  }
}
</script>
