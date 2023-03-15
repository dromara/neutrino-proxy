---
title: 自动生成front matter
date: 2020-05-12 11:46:37
permalink: /pages/088c16
article: false
---

当你没有给`.md`文件的[front matter](https://vuepress.vuejs.org/zh/guide/frontmatter.html)指定标题(`title`)、时间(`date`)、永久链接(`permalink`)、分类(`categories`)、标签(`tags`)、主题配置中[extendFrontmatter](/pages/a20ce8/#extendfrontmatte)配置的字段时，在运行开发服务`npm run dev`或打包`npm run build`时将自动为你生成这些数据，你也可以自己手动设置这些数据，当你手动设置之后，相应的数据就不会再自动生成。



### 生成示例

```yaml
---
title: 《JavaScript教程》笔记
date: 2020-01-12 11:51:53
permalink: /pages/d8cae9
categories:
  - 前端
  - JavaScript
tags:
  -
---
```

### title

* 类型: `string`

* 默认：`.md`文件的名称

当前页面的标题



### date
* 类型: `string`
* 格式：`YYYY-MM-DD HH:MM:SS`
* 默认：`.md`文件在系统中创建的时间

当前页面的创建时间，如需手动添加或修改该字段时请按照格式添加或修改



### permalink
* 类型: `string`
* 默认：`/pages/`+ 6位字母加数字的随机码

当前页面的永久链接

> Q：自动生成front matter为什么要包含永久链接？
>
> A：使用永久链接是出于以下几点考虑:
>
> * 在config.js配置nav时使用永久链接，就不会因为文件的路径或名称的改变而改变。
>* 对于博客而言，当别人收藏了你的文章，在未来的时间里都可以通过永久链接来访问到。
>* 主题中的目录页需要通过永久链接来访问文章。


### categories

* 类型: `array`
* 默认：
  * `.md`所在的文件夹名称。
  * 如果`.md`文件所在的目录是`三级目录`，则会有两个分类值，分别是`二级目录`和`一级目录`的文件夹名称。如果在`四级目录`，则再多一个`三级目录`的文件夹名称分类。（[级别说明](/pages/33d574/#级别说明)）
  * 如果`.md`文件所在的目录是`_posts`，则默认值是`随笔`，这个默认值可以在`config.js`中修改，参考：[config.js配置](/pages/a20ce8/#碎片化博文默认分类值)
* 如果在 [config.js配置](/pages/a20ce8/#category) 设置了`category: false` 将不会自动生成该字段

当前页面的分类



### tags

* 类型: `array`
* 默认：空数组
* 如果在 [config.js配置]() 设置了`tag: false` 将不会自动生成该字段

当前页面的标签，默认值是空数组，自动生成该字段只是为了方便后面添加标签值。


### 扩展自动生成front matter

当在主题配置中配置了`extendFrontmatter`时，将在自动生成front matter时添加相应配置的字段和数据。详见：[extendFrontmatter](/pages/a20ce8/#extendfrontmatter)

### 碎片化文章‘分类’的自动生成规则 <Badge text="v1.12.5+"/>

> 碎片化文章即放在_posts文件夹的文章，里面的`.md`文件不需要遵循命名约定，不会生成结构化侧边栏和目录页。

当文章在_posts根目录时，分类获取 `themeConfig.categoryText` 的值，如`_posts/foo.md` ，则`foo.md`文件的分类会生成为：

```yaml
categories:
  - 随笔
```

> categoryText的默认值是‘随笔’，可在themeConfig修改，详见[categorytext](/pages/a20ce8/#categorytext)。

当文章在非_posts根目录时，获取父文件夹的名称作为分类，如

 `_posts/想法/奇思妙想/foo.md` ，则`foo.md`文件的分类会生成为：

```yaml
categories:
  - 想法
  - 奇思妙想
```
