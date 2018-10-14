# Maven

Maven 在以下几个方面的管理中提供帮助：
- 项目构建
- 说明文档
- 报告
- 依赖
- 版本控制
- 发布

> 简略言之，Maven 想要达成的是「从最佳实例中提供一种清晰的路径，把易于理解且高效的模式运用到项目的构建工程中」。

Maven 的本质是一个插件执行框架，所有工作都是通过插件（plugin）来完成的。

## 安装
1. 下载：[https://maven.apache.org/download.html](https://maven.apache.org/download.html)
2. 确保已设置 `JAVA_HOME` 环境变量
3. 解压文件，放到合适目录下，把 *bin* 目录添加进 `PATH` 环境变量
4. 查看是否安装成功：`mvn -v`

## 创建项目
切换到项目目录，执行：
`$ mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false `

`archetype:generate` 称为 Maven **goal**；冒号之前的 `archetype` 称为 **plugin**。Plugin 是一组 goals 的集合。

查阅插件列表：[https://maven.apache.org/plugins/index.html](https://maven.apache.org/plugins/index.html)

> tip：生成的 pom.xml 中是自动附带了 `junit` 依赖的。

> 深入理解 archetype：[Introduction to Archetypes](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)

## 标准项目结构
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

## 构建项目
	$ mvn package
执行后，会编译项目并在 target 目录下生成 JAR 包。

测试该 JAR 包，会看到输出 `Hello World!`：
	$ java -cp target/my-app-1.0-SNAPSHOT.jar com.mycompany.app.App

和前面的 `archetype:generate` 不同，这里的 `package` 不是 goal 而是 **phase**。phase 是 build lifecycle 的组成单位（见后文）。

> 默认的 Maven 通常已经能满足需求，但如果需要更改缓存位置或是使用 HTTP 协议，则需要对 Maven 进行配置——查阅 [Guide to Configuring Maven](https://maven.apache.org/guides/mini/guide-configuring-maven.html)。

## 构建生命周期（Build Lifecycle）
即阶段（phase）的有序序列。当给出一个阶段时，就是让 Maven 从头开始一个个执行按序序列中的阶段，直到执行完给定阶段。比如执行 `compile` 阶段，那么实际执行的阶段有：
1. validate
2. generate-sources
3. process-sources
4. generate-resources
5. process-resources
6. compile

[http://wiki.jikexueyuan.com/project/maven/build-life-cycle.html](http://wiki.jikexueyuan.com/project/maven/build-life-cycle.html)
[https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)

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

另外两个非默认的生命周期阶段：
- **clean**：清除之前的构建中创建的 artifacts
- **site**：为项目生成站点文档

Phases 最后实际上是对应到了底层的 goals，具体每个 phase 是执行了哪些 goals，取决于打包格式是 JAR 还是 WAR。

tip：phases 和 goals 允许在一个序列中执行，如
	$ mvn clean dependency:copy-dependencies package

### 生成站点
	$ mvn site
*site* 阶段会根据 pom.xml 来生成站点。

## 项目对象模型
对 pom.xml 中的一些元素做必要的解释：
- **modelVersion**：POM 采用的对象模型版本
- **groupId**：表示组织或团队的特有标识符，如 `org.apache.maven.plugins` 是为所有 Maven 插件指定的 groupId
- **artifactId**：指示在这个项目下生成的主 artifact 的特有基本名称，主 artifact 通常是 JAR 包。Maven 中的 artifacts 通常是 \<artifactId\>-\<version\>.\<extension\> 的形式，如 `myapp-1.0.jar`
- **packaging**：指示 artifact 的打包格式（如 JAR、WAR、EAR 等），同时决定了生命周期中具体的构建处理方式
- **version**：项目生成的 artifact 的版本。常见的 `SNAPSHOT` 指示正在开发过程中
- **name**、**url**、**description**：这些通常用在 Maven 生成的文档中

对于新手来说，了解 POM (Project Object Model) 的概念是重要的，建议阅读 [Introduction to the POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html)。

## 编译测试源并运行单元测试
🔘