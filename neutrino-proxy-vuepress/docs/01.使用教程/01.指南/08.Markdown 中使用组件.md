---
title: Markdown 中使用组件
date: 2020-11-10 18:56:22
permalink: /pages/197691/
article: false
---

主题的内置组件可以直接在`Markdown`文件中以类似html标签的方式使用。

## 标记
- **Props:**
    - `text`- string
    - `type` - string, 可选值： `tip | warning | error`，默认： `tip`
    - `vertical` - string, 可选值： `top | middle`，默认： `top`

- **Usage:**

你可以在标题或其他内容中使用标记：
```md
#### 《沁园春·雪》 <Badge text="摘"/>
北国风光<Badge text="注释" type="warning"/>，千里冰封，万里雪飘。

> <Badge text="译文" type="error" vertical="middle"/>: 北方的风光。
```
**效果：**
#### 《沁园春·雪》 <Badge text="摘"/>
北国风光<Badge text="注释" type="warning"/>，千里冰封，万里雪飘。

> <Badge text="译文" type="error" vertical="middle"/>: 北方的风光。

## 代码块选项卡 <Badge text="v1.8.0 +"/>

在`<code-group>`中嵌套`<code-block>`来配合使用。在`<code-block>`标签添加`title`来指定tab标题，`active`指定当前tab：

````md
<code-group>
  <code-block title="YARN" active>
  ```bash
  yarn add vuepress-theme-vdoing -D
  ```
  </code-block>

  <code-block title="NPM">
  ```bash
  npm install vuepress-theme-vdoing -D
  ```
  </code-block>
</code-group>
````

**效果：**

<code-group>
  <code-block title="YARN" active>
  ```bash
  yarn add vuepress-theme-vdoing -D
  ```
  </code-block>

  <code-block title="NPM">
  ```bash
  npm install vuepress-theme-vdoing -D
  ```
  </code-block>
</code-group>

::: warning
- 请在`<code-group>`标签与markdown内容之间使用空行隔开，否则可能会解析不出来。
- 该组件只适用于放置代码块，放其他内容在体验上并不友好。如您确实需要放置其他内容的选项卡，推荐使用[vuepress-plugin-tabs](https://superbiger.github.io/vuepress-plugin-tabs)插件。
:::
