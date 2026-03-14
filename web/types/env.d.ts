/// <reference types="vite/client" />

declare module '*.vue' {
  import { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare module 'virtual:*' {
  const result: any;
  export default result;
}

// 引入 Ant Design Vue 的类型并重命名，防止冲突
import type { TableColumnsType as AntdTableColumnsType } from 'ant-design-vue';

// 声明到全局作用域
declare global {
  type TableColumnsType = AntdTableColumnsType;
}