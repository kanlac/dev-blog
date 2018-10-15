# Jinja 模版渲染引擎

## 概要
Jinja 模版就是一份文本文件，它可以渲染的不单单是 HTML，还包括 XML，CSV，LaTeX 等，跟文件使用的后缀名也无关。Jinja 模版包含用于渲染的**变量**和**表达式**，以及控制逻辑的**标签**。

## 变量与表达式
默认配置下包含的定界符（**delimiters**）
- `{% ... %}` 语句
- `{{ ... }}` 输出表达式
- `{# ... #}` 多行注释
- `## ...` 单行注释（Jinja 2.2+）
- `#  ...` 行语句

行语句（Line Statements）的示例（两段等价代码）：
```Jinja2
<ul>
# for item in seq:
    <li>{{ item }}</li>
# endfor
</ul>

<ul>
{% for item in seq %}
    <li>{{ item }}</li>
{% endfor %}
</ul>
```
第二行冒号是为了增加可读性。

访问变量的属性的两种方式：
1. 通过 `foo.bar` 访问变量属性，Jinja 在 Python 层的 fall back 顺序
	1. `getattr(foo, 'bar')`
	2. `foo.__gititem__('bar')`
	3. 返回未定义对象
2. 通过 `foo['bar']` 访问对象包含成员，和以上的区别仅是第 1、2 步倒置了
在对象没有名字相同的子成员和属性时，两者是**等价**的。

对于不存在的变量的处理：
默认会在输出时将该变量作为空字符串处理，如果进行其他操作将会报错。最终取决于应用的配置。

变量的 Filters & Tests：
- **Filters** 对变量进行更改
- **Tests** 对变量进行判断
	- `name is defined` 测试 _name_ 变量在当前模版环境下是否已定义，返回布尔值
	- `{% if loop.index is divisibleby(3) %}` 测试是否能被三整除（参数不多于一个时可省略括号）

## Whitespace Control
关于渲染过程中如何处理 whitespace 的问题。默认配置下：
- 单个行尾换行会被省略
- 其他 whitespace（空格、tab、换行等）等会保留

## 转义
1. 在单个输出表达式中转义
```Jinja2
{{ '{{' }}
```
2. 多行转义，比如要在网页中展示一段 Jinja 代码
```Jinja2
{% raw %}
    <ul>
    {% for item in seq %}
        <li>{{ item }}</li>
    {% endfor %}
    </ul>
{% endraw %}
```

## 模版继承
应当尽可能多的使用继承，把冗余的代码放到 base.html 中，后续开发会方便很多。

复制 block `{{ self.title() }}`：
```Jinja2
<title>{% block title %}{% endblock %}</title>
<h1>{{ self.title() }}</h1>
```

嵌套 block 中的变量
block 中不能访问它外部的变量，如
```Jinja2
{% for item in seq %}
    <li>{% block loop_item %}{{ item }}{% endblock %}</li>
{% endfor %}
```
`<li>` 的结果将为空。这是因为如果该 block 被继承，那么该变量就成了未定义的。

但从 Jinja 2.2 开始，可以通过 `scoped` 修饰词显式声明其中的变量是已定义的。

## HTML 转义
为了避免因变量中包含一些特殊字符导致的最后生成的 HTML 被转义，可以进行转义配置。

## 宏（Macros）
Jinja 的函数。

定义一个渲染表单元素的宏
```Jinja2
{% macro input(name, value='', type='text', size=20) -%}
    <input type="{{ type }}" name="{{ name }}" value="{{
        value|e }}" size="{{ size }}">
{%- endmacro %}
```

调用
```Jinja2
<p>{{ input('username') }}</p>
<p>{{ input('password', type='password') }}</p>
```

如果需要调用另一个模版的宏，需要先 `import`

内部细节。宏的属性：
- _name_：`{{ input.name }}` 的输出结果为 `input`
- _arguments_：宏接收到的参数的名称，以元组存储
- _defaults_：默认值的元组
- _catch\_kwargs_：当宏接收了多余的 kwargs 时返回 true
- _catch\_varargs_：当宏接收了多余的 varargs 时返回 true
- _caller_：当宏访问特定 caller 变量且可能被 call 标签调用时返回 true❓

## 进阶
- Call 标签
- Filter 标签
- ……
