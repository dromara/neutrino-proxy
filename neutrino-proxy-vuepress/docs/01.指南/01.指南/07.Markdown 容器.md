---
title: Markdown 容器
date: 2020-05-29 11:16:18
permalink: /pages/d0d7eb/
article: false
---

Markdown 容器是对 Markdown 语法的一个扩展，使用简单的语法就可以在页面中呈现丰富的效果。

除了原默认主题自带的容器外，本主题还新增了一些好用的自定义容器。

## 信息框容器
**输入**
```` md
::: tip
这是一条提示
:::

::: warning
这是一条注意
:::

::: danger
这是一条警告
:::

::: note
这是笔记容器，在 <Badge text="v1.5.0 +" /> 版本才支持哦~
:::
````

**输出**
::: tip
这是一条提示
:::

::: warning
这是一条注意
:::

::: danger
这是一条警告
:::

::: note
这是笔记容器，在 <Badge text="v1.5.0 +" /> 以上版本才支持哦~
:::

以上容器均可自定义标题，如：
````
::: tip 我的提示
自定义标题的提示框
:::
````
::: tip 我的提示
自定义标题的提示框
:::

## 布局容器  <Badge text="v1.3.3 +" />
**输入**
```` md
::: center
  ### 我是居中的内容
  （可用于标题、图片等的居中）
:::

::: right
  [我是右浮动的内容](https://zh.wikipedia.org/wiki/%E7%89%9B%E9%A1%BF%E8%BF%90%E5%8A%A8%E5%AE%9A%E5%BE%8B)
:::

::: details
这是一个详情块，在 IE / Edge 中不生效
```js
console.log('这是一个详情块')
```
:::

::: theorem 牛顿第一定律
假若施加于某物体的外力为零，则该物体的运动速度不变。
::: right
来自 [维基百科](https://zh.wikipedia.org/wiki/%E7%89%9B%E9%A1%BF%E8%BF%90%E5%8A%A8%E5%AE%9A%E5%BE%8B)
:::
````

**输出**
::: center
  ### 我是居中的内容
  （可用于标题、图片等的居中）
:::

::: right
  [我是右浮动的内容](https://zh.wikipedia.org/wiki/%E7%89%9B%E9%A1%BF%E8%BF%90%E5%8A%A8%E5%AE%9A%E5%BE%8B)
:::

::: details
这是一个详情块，在 IE / Edge 中不生效
```js
console.log('这是一个详情块')
```
:::

::: theorem 牛顿第一定律
假若施加于某物体的外力为零，则该物体的运动速度不变。

::: right
来自 [维基百科](https://zh.wikipedia.org/wiki/%E7%89%9B%E9%A1%BF%E8%BF%90%E5%8A%A8%E5%AE%9A%E5%BE%8B)
:::

> 注意：`right`、`details`、`theorem`这三个容器在`v1.3.0 +`版本才支持。`center`容器在`v1.3.3 +`版本才支持。


## 普通卡片列表 <Badge text="v1.1.0 +"/>

普通卡片列表容器，可用于`友情链接`、`项目推荐`、`诗词展示`等。

先来看看效果：

**输出**
::: cardList
```yaml
- name: 麋鹿鲁哟
  desc: 大道至简，知易行难
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200122153807.jpg # 可选
  link: https://www.cnblogs.com/miluluyo/ # 可选
  bgColor: '#CBEAFA' # 可选，默认var(--bodyBg)。颜色值有#号时请添加单引号
  textColor: '#6854A1' # 可选，默认var(--textColor)
- name: XAOXUU
  desc: '#IOS #Volantis主题作者'
  avatar: https://fastly.jsdelivr.net/gh/xaoxuu/assets@master/avatar/avatar.png
  link: https://xaoxuu.com
  bgColor: '#718971'
  textColor: '#fff'
- name: 平凡的你我
  desc: 理想成为大牛的小陈同学
  avatar: https://reinness.com/avatar.png
  link: https://reinness.com
  bgColor: '#FCDBA0'
  textColor: '#A05F2C'
```
:::

上面效果在Markdown中的代码是这样的：

**输入**
```` md
::: cardList
```yaml
- name: 麋鹿鲁哟
  desc: 大道至简，知易行难
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200122153807.jpg # 可选
  link: https://www.cnblogs.com/miluluyo/ # 可选
  bgColor: '#CBEAFA' # 可选，默认var(--bodyBg)。颜色值有#号时请添加单引号
  textColor: '#6854A1' # 可选，默认var(--textColor)
- name: XAOXUU
  desc: '#IOS #Volantis主题作者'
  avatar: https://fastly.jsdelivr.net/gh/xaoxuu/assets@master/avatar/avatar.png
  link: https://xaoxuu.com
  bgColor: '#718971'
  textColor: '#fff'
- name: 平凡的你我
  desc: 理想成为大牛的小陈同学
  avatar: https://reinness.com/avatar.png
  link: https://reinness.com
  bgColor: '#FCDBA0'
  textColor: '#A05F2C'
```
:::
````


### 语法
````md
::: cardList <每行显示数量>
``` yaml
- name: 名称
  desc: 描述
  avatar: https://xxx.jpg # 头像，可选
  link: https://xxx/ # 链接，可选
  bgColor: '#CBEAFA' # 背景色，可选，默认var(--bodyBg)。颜色值有#号时请添加引号
  textColor: '#6854A1' # 文本色，可选，默认var(--textColor)
```
:::
````

* `<每行显示数量>` 数字，表示每行最多显示多少个，选值范围1~4，默认3。在小屏时会根据屏幕宽度减少每行显示数量。
* 代码块需指定语言为`yaml`
* 代码块内是一个`yaml`格式的数组列表
* 数组成员的属性有：
  * `name`名称
  * `desc`描述
  * `avatar`头像，可选
  * `link`链接，可选
  * `bgColor`背景色，可选，默认`var(--bodyBg)`。颜色值有`#`号时请添加引号
  * `textColor`文本色，可选，默认`var(--textColor)`

下面再来看另外一个示例：

**输入**
```` md
::: cardList 2
```yaml
- name: 《静夜思》
  desc: 床前明月光，疑是地上霜。举头望明月，低头思故乡。
  bgColor: '#F0DFB1'
  textColor: '#242A38'
- name: Vdoing
  desc: 🚀一款简洁高效的VuePress 知识管理&博客(blog) 主题
  link: https://github.com/xugaoyi/vuepress-theme-vdoing
  bgColor: '#DFEEE7'
  textColor: '#2A3344'
```
:::
````


**输出**
::: cardList 2
```yaml
- name: 《静夜思》
  desc: 床前明月光，疑是地上霜。举头望明月，低头思故乡。
  bgColor: '#F0DFB1'
  textColor: '#242A38'
- name: Vdoing
  desc: 🚀一款简洁高效的VuePress 知识管理&博客(blog) 主题
  link: https://github.com/xugaoyi/vuepress-theme-vdoing
  bgColor: '#DFEEE7'
  textColor: '#2A3344'
```
:::




## 图文卡片列表 <Badge text="v1.1.0 +" />

图文卡片列表容器，可用于`项目展示`、`产品展示`等。

先看效果：

**输出**
::: cardImgList
```yaml
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200529162253.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容 # 描述，可选
  author: Evan Xu # 作者，可选
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg # 头像，可选
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200530100256.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容
  author: Evan Xu
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200530100257.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容
  author: Evan Xu
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg
```
:::

**输入**
````md
::: cardImgList
```yaml
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200529162253.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容 # 描述，可选
  author: Evan Xu # 作者，可选
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg # 头像，可选
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200530100256.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容
  author: Evan Xu
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg
- img: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200530100257.jpg
  link: https://xugaoyi.com/
  name: 标题
  desc: 描述内容描述内容描述内容描述内容描述内容描述内容描述内容描述内容
  author: Evan Xu
  avatar: https://fastly.jsdelivr.net/gh/xugaoyi/image_store/blog/20200103123203.jpg
```
:::
````


### 语法
````md
::: cardImgList <每行显示数量>
``` yaml
- img: https://xxx.jpg # 图片地址
  link: https://xxx.com # 链接地址
  name: 标题
  desc: 描述 # 可选
  author: 作者名称 # 可选
  avatar: https://xxx.jpg # 作者头像，可选
```
:::
````
* `<每行显示数量>` 数字，表示每行最多显示多少个，选值范围1~4，默认3。在小屏时会根据屏幕宽度减少每行显示数量。
* 代码块需指定语言为`yaml`
* 代码块内是一个`yaml`格式的数组列表
* 数组成员的属性有：
  * `img`图片地址
  * `link`链接地址
  * `name`标题
  * `desc`描述，可选
  * `author`作者名称，可选
  * `avatar`作者头像，可选


## 增强配置 <Badge text="v1.9.0 +"/>
为了适应更多需求场景，`v1.9.0+`版本的普通卡片和图文卡片容器添加了一些新的配置：

### 1. 普通卡片和图文卡片容器

#### target
- 链接的打开方式，默认`_blank`

  - `_self` 当前页面

  - `_blank` 新窗口打开



### 2. 图文卡片容器

#### imgHeight
- 设置图片高度，默认 `auto`

  - 带单位


#### objectFit
- 设置图片的填充方式(object-fit)，默认 `cover`

  - `fill` 拉伸 (会改变宽高比)
  - `contain` 缩放 (保持宽高比，会留空)
  - `cover` 填充 (会裁剪)
  - `none` 保持原有尺寸 (会留空或裁剪)
  - `scale-down` 保证显示完整图片 (保持宽高比，会留空)


#### lineClamp
- 描述文本超出多少行显示省略号，默认`1`

​

### 3. 配置示例：

````yaml
::: cardImgList
``` yaml
config:
    target: _blank
    imgHeight: auto
    objectFit: cover
    lineClamp: 1

data:
  - img: https://xxx.jpg
    link: https://xugaoyi.com/
    name: 标题
    desc: 描述内容
    author: Evan Xu
    avatar: https://xxx.jpg
```
:::
````
