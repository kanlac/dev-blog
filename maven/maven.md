# Maven 构建工具

Maven 的**本质是一个插件执行框架**，所有工作都是通过插件（plugin）来完成的。使用 Maven 构建工具的目的，是为了「从**最佳实例**中提供一种清晰的路径，把易于理解且高效的模式运用到项目的构建工程中」。它的应用范围还包括项目构建、说明文档、报告、依赖、版本控制、项目发布等。

## 安装
1. 下载：[https://maven.apache.org/download.html](https://maven.apache.org/download.html)
2. 确保已设置 `JAVA_HOME` 环境变量
3. 解压文件，放到合适目录下，把 *bin* 目录添加进 `PATH` 环境变量
4. 查看是否安装成功：`mvn -v`

## 创建项目
命令参照：
```bash
$ mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=com.mycompany.app \
  -DartifactId=my-app
```
执行后将根据 `DartifactId` 的值生成目录

`archetype:generate` 称为 Maven **goal**；其中冒号之前的 `archetype` 称为 **plugin**，这些将在后面详细提到。

创建项目时我们用到了 Maven 的 archetype 机制，archetype 是项目模版，它结合用户的一些输入来构建符合需要的项目

查阅插件列表：[https://maven.apache.org/plugins/index.html](https://maven.apache.org/plugins/index.html)

> tip：生成的 pom.xml 中是自动附带了 `junit` 依赖的。

> 深入理解 archetype：[Introduction to Archetypes](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)

## 标准目录布局
```
my-app
|-- pom.xml
`-- src
    |-- main
    |   `-- java
    |       `-- com
    |           `-- mycompany
    |               `-- app
    |                   `-- App.java
    `-- test
        `-- java
            `-- com
                `-- mycompany
                    `-- app
                        `-- AppTest.java
```

## 构建项目
在 archetype 为我们生成的目录下，编译项目：
```bash
$ mvn compile
```
根据 Maven 采用的约定（convention），编译过的类会放到 `${basedir}/target/classes` 下。相比另一种构建工具 Ant，这种 convention over configuraion 的设计理念有非常明显的优势。

也可以跳过编译直接打包（虽然实际上也会经过编译）：
```bash
$ mvn package
```
执行后，会编译项目并在 target 目录下生成 JAR 包。

测试该 JAR 包（执行 Java 程序）：
```bash
$ java -cp target/my-app-1.0-SNAPSHOT.jar EXECUTABLE_CLASS_PATH
```

和前面的 `archetype:generate` 不同，这里的 `compile` 和 `package` 不是 goal 而是 **phase**（见后文）。

> 默认的 Maven 通常已经能满足需求，但如果需要更改缓存位置或是使用 HTTP 协议，则需要对 Maven 进行配置——查阅 [Guide to Configuring Maven](https://maven.apache.org/guides/mini/guide-configuring-maven.html)。

## 构建生命周期（Build Lifecycle）
Maven 构建生命周期是一组阶段（phase）的有序序列。**当执行一个阶段时，在该阶段之前已经包括它自身在内的所有阶段会被执行**。比如执行 `compile` 阶段，那么实际执行的阶段有： *validate* -\> *generate-sources* -\> *process-sources* -\> *generate-resources* -\> *process-resources* -\> *compile* 。

## Goals & plugins & phases
重新来梳理一下这几个关键名词之间的关系。

目标（goals）表示一个特定的、对构建和管理工程有帮助的任务，它可能绑定了 0 或多个构建阶段。没有绑定任何构建阶段的目标可以在构建生命周期之外被直接调用执行。

阶段（phases）的序列组成一个构建生命周期，每个阶段定义了目标被执行的顺序。虽然我们可以直接执行阶段，但这样的过程实际上最终仍是交给底层的目标来执行[^1]。

插件（plugins）是一组目标的集合。

> 目标 -:[绑定]:-\> 阶段 -:[组成]:-\> 生命周期

`mvn` 命令可以连续执行，如在
```bash
$ mvn clean dependency:copy-dependencies package
```
中，先执行 clean 阶段，然后是 dependency:copy-dependencies 目标，最后 package 阶段。

## 运行 Maven 工具

### Mave Phases
生命周期中一些默认的通常用于执行的阶段：
- **validate**
- **compile**：编译完成之后放入 target/classes
- **test**：用合适的单元测试框架测试编译过的源码，不需要先打包或部署
- **package**：按照指定的分发格式打包，如 JAR
- **intergration-test**：对有必要的包进行处理并部署到整合测试环境中
- **verify**：运行检查确保符合质量标准
- **install**：安装包到本地 repo，可用作其他项目的依赖
- **deploy**：在整合或发布环境中，把最终的包拷贝到远程 repo

另外两个非默认的阶段：
- **clean**：清除之前的构建中创建的 artifacts
- **site**：为项目生成站点文档

### 生成站点
执行 site 阶段，Maven 会根据 pom.xml 来生成站点
```bash
$ mvn site
```

## 项目对象模型 POM
POM（Project Object Model）是 Maven 项目的基本单元和配置核心，对于新手来说，了解 POM (Project Object Model) 的概念是重要的，建议阅读 [Introduction to the POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html)。

对 pom.xml 中的一些元素做必要的解释：
- **modelVersion**：POM 采用的对象模型版本
- **groupId**：表示组织或团队的特有标识符，如 `org.apache.maven.plugins` 是为所有 Maven 插件指定的 groupId
- **artifactId**：指示在这个项目下生成的主 artifact 的特有基本名称，主 artifact 通常是 JAR 包。Maven 中的 artifacts 通常是 \<artifactId\>-\<version\>.\<extension\> 的形式，如 `myapp-1.0.jar`
- **packaging**：指示 artifact 的打包格式（如 JAR、WAR、EAR 等），同时决定了生命周期中具体的构建处理方式
- **version**：项目生成的 artifact 的版本。常见的 `SNAPSHOT` 指示正在开发过程中
- **name**、**url**、**description**：这些通常用在 Maven 生成的文档中

## 编译测试源并运行单元测试
编译应用源后，如果写了单元测试，可以用 Maven 执行：
```bash
$ mvn test
```
🔘

[^1]:	具体每个 phase 是执行了哪些 goals，取决于打包格式是 JAR 还是 WAR。