# Pelican 笔记

## 安装
通过 `pip` 安装，建议在 virtualenv 环境下操作：
```
$ pip install pelican Markdown typogrify
```

升级 Pelican：
```
$ pip install --upgrade pelican
```

快速启动：
```
$ pelican-quickstart
```
如果这一步遇到了 ValueError: unknown locale utf-8 错误，则需要导出环境变量：
```
$ export LC_ALL=en_US.UTF-8
$ export LANG=en_US.UTF-8
```

## 撰写内容
articles 和 pages 的区别：
前者是按时间相关的博文，后者是很少更改的（如「关于」）

### metadata
以 Markdown 为标准：
```
Title: My super title
Date: 2010-12-03 10:20
Modified: 2010-12-05 19:30
Category: Python
Tags: pelican, publishing
Slug: my-super-post
Author: Alexis Metaireau
Summary: Short version for index and feeds

This is the content of my super blog post.
```

解释：
- `title` 是唯一必须的 metadata
- `category` 可以通过文件夹的方式来指定
- `slug` 即页面在 url 中的表示
- 如果不填写 `summary` ，通过设置 `SUMMARY_MAX_LENGTH` 来指定

当要调试不同的 metadata 设置时，建议**关闭缓存功能**。在配置文件中添加：
```
LOAD_CONTENT_CACHE = False
```
或者在终端执行命令时添加选项 `--ignore-cache`。

metadata 甚至可以通过正则从文件名中获取。比如从我现有的文章中获取 `slug` 就很有用

### pages
在 content/pages 下的文件会被用来生成静态页面。

设置 `DISPLAY_PAGES_ON_MENU` 决定是否要在导航菜单中显示全部的 pages 页面（默认为 `True`）；如果要排除单篇页面在导航菜单中显示，在文章 metadata 中设置 `status: hidden`（可以用来制作错误页）。

### 内部链接
直接看原文：[http://docs.getpelican.com/en/stable/content.html#linking-to-internal-content][1]

### 语法高亮
🔘

### 草稿
添加 metadata `Status: draft`，文章将会被放入 drafts 文件夹。

设置默认新文章为草稿：
```
DEFAULT_METADATA = {
    'status': 'draft',
}
```
如果做以上设置，就需要手动把草稿更改为发布状态：`Status: published`

## 发布网站
生成静态网站：
```
$ pelican content [-s path/to/settings.py]
```
生成的内容会出现在 output/ 下；如不指定设置文件，默认是 pelicanconf.py。

用 Python 启动一个简单的 web 服务器：
```
$ cd output
$ python3 -m http.server
```

### 部署
用发布配置文件生成静态网站：
```
$ pelican content -s publishconf.py
```

让 publishconf.py 位于 pelicanconf.py 的基础上，在前者中添加：
```python
from pelicanconf import *
```

### 自动化
Fabric 或 Make
🔘

## 配置文件
通过 `quick-start` 创建的 Pelican 项目将默认生成 /pelicanconf.py 作为配置文件，如需指定配置文件，在生成静态网站时使用 `-s` 选项：
```
$ pelican content -s path/to/your/pelicanconf.py
```

设置项的所有**键**必须为大写；除了 number、boolean、dict 和 tuple 类型，其他的**值**都应该包含在引号中。当进行设置的调试时，建议像前面那样关闭缓存功能。（[样例配置][2]）

## 发布到 GitHub Pages
GitHub Pages 有两种，Project Pages 和 User Pages，只介绍后者，也比较简单。简单来说只要把 output 目录下的文件 push 到 \<username\>.github.io 的 master 分支下就好了。

用 `pip` 安装 `ghp-import` 这个工具，然后执行：
```
$ pelican content -o output -s pelicanconf.py
$ ghp-import output
$ git push <remote-url> gh-pages:master
```
`<remote-url>` 填写项目的 HTTPS url（不包括尖括号）。`ghp-import` 会在本地 repo 中更新（建立）gh-pages 分支并 push 到远程 repo 的 master 分支。

完成后，如果没有报错，就可以从 https://\<username\>.github.io/ 访问到了。

## 自定义模版
在设置中添加 `THEME` 字段，值为模版的路径。模版不必在该 Pelican 项目目录下。

生成网站时通过选项 `-t` 选择要用的模版：
```
$ pelican content -t /projects/your-site/themes/your-theme
```

自定义主题必须遵循以下结构：
```
├── static
│   ├── css
│   └── images
└── templates
    ├── archives.html         // to display archives
    ├── period_archives.html  // to display time-period archives
    ├── article.html          // processed for each article
    ├── author.html           // processed for each author
    ├── authors.html          // must list all the authors
    ├── categories.html       // must list all the categories
    ├── category.html         // processed for each category
    ├── index.html            // the index (list all the articles)
    ├── page.html             // processed for each page
    ├── tag.html              // processed for each tag
    └── tags.html             // must list all the tags. Can be a tag cloud.
```

我们在 pelicanconf.py 中定义的变量，是作为[**模版变量**][3]可被全局可访问的。除此之外，Pelican 还针对各个模版提供了多组局部变量。

Pelican 提供了可在模版中使用的**对象**，如最主要的 Article 对象。但匪夷所思的是文档并没有给出一个更详细的文档和全部对象列表。几个主要对象的简要说明：
- _Article_：该对象的字符串表示是它的 `source_path`属性
- _Author_ / _Category_ / _Tag_：来源于 pelicanconf.py 或文章的 metadata；字符串表示是它们的 `name` 属性
- _Page_：字符串表示是它的 `source_path`属性

从 3.0 版本开始，Pelican 支持直接从 Simple 模版**继承**。如果有必须文件缺失，Pelican 会自动继承，以保证仍能生成静态网站。

[1]:	http://docs.getpelican.com/en/stable/content.html#linking-to-internal-content
[2]:	https://raw.githubusercontent.com/getpelican/pelican/master/samples/pelican.conf.py
[3]:	http://docs.getpelican.com/en/stable/themes.html#templates-and-variables