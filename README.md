# 黑马工序小助手

## Key Benefits

- 利用IDE的提示和纠错，提高书写效率
- 利用代码的抽象和复用，提高书写效率
- 自动扫描并组合api schema，统一显示api定义
- 自动扫描并组合时序图，按组件的架构层级展示链路调用关系
- 输出单一PDF方便阅读
- 支持上传到jira
- 架构分层(Node) + 工序(Process) + 流程描述(Flow) + 任务(task) + DSL描述调用关系 => 时序图，巨量提升可读性

## Features

- [x] 在时序图中显示api schema
- [x] 输出成markdown文件
- [x] 输出成pdf带时序图
- [x] 上传pdf到jira附件
- [x] 同步修改jira卡
- [x] 使用plantuml实现时序图
- [x] 使用时序图样式优化调用关系的表述
- [x] 优化dsl结构
- [x] 配置文件
- [x] 多项目支持(配置、路径、)
- [x] 工序预定义
- [x] 工序定义优化显示
- [x] 效能追踪（时间
- [x] 使用infix函数改造process的声明方式(A->B)
- [x] 效能报告输出
- [x] 根据git diff自动输出修改的story、architecture、performance
- [x] 协程改造
- [x] 插件化 - idea代码模板
- [x] TOC 跳转标签优化
- [x] 把Flow作为对AC的场景、技术拆解，包含example（e.g. AC-体现, flow-成功/失败)
- [x] 故事索引（支持搜索、tag、epic关系
- [x] 架构代码化
    - [x] 进程间架构
    - [x] 进程内架构
    - [x] 版本管理
    - [x] 架构变化不触发工序变化（避免架构变更对前序故事的影响）
- [x] Bug, 打印story时复用的container formatter产生的架构图会跑到temp/下

### Backlogs

- [ ] 效能报告优化
  - [ ] 效能记录（按工序为单位记录）
  - [ ] 自动化计算
- [ ] 集中显示api schema 合并AC中的时序图（按不同层级进行合并）(低优先级)
- [ ] 生成对应的测试报告
- [ ] 插件化 - idea生成快捷键
- [ ] 插件化 - idea codegen
- [ ] okhttp 替换 httpclient

### Issues

- pdf不支持中文

----

## How to start

### [Optional][Repo] Code Template

导入`code-template`

- `file-blackhorse`: Story文件Template
- `live-ac`: ac片段Template, 自定义short command

### [Required][Project] 初始化项目配置

在 `resources/projects.properties` 中添加项目名(`,`分隔多个项目)

```properties
projects=<your project name>,...,<your project name>
```

- 创建项目配置 `resources/projects/<your project name>/configure.properties` 并按如下规则写入配置信息

```properties
# ------
# [REQUIRED] 打印选项 = PrintOption
# MARKDOWN_TYPORA               生成markdown+typora时序图
# MARKDOWN_TYPORA_PLANTUML      生成markdown+plantuml时序图
# PDF_PLANTUML                  生成markdown+pdf+plantuml时序图
# JIRA_ATTACHMENT               生成markdown+pdf+plantuml时序图, 并上传jira
printer=PDF_PLANTUML
# ------
# [OPTIONAL, default = empty] 隐藏部分信息
#hidden=complexity
# ------
# 如果要上传jira, 需要配置server地址和access token
#jira_baseurl=https://jira.xxx.xxx.com/rest/api/2
#jira_token=*************************
# ------
# 远程仓库的地址, 直接链接访问仓库中的文件
repo_baseurl=https://github.com/Anddd7/a-blackhorse/blob
```

- 创建项目工作路径 `com/thoughtworks/projects/<your project name>`

> 生成时会按`<your project name>`来寻找配置, 需要保证这个别名是一个valid package name

### [Required][Project] 定义项目工序

创建项目架构描述, 定义系统、进程、进程内的组件模块(参考:`com/thoughtworks/projects/order/ProjectArchitecture.kt`)

```
object xxx : Container {                        // 应用进程
    object xxx1 : Component                     // 组件1
    object xxx2 : Component                     // 组件2
    
    override val id = "1"                       // 进程编号
    override val definitions = listOf(...)      // 工序定义
}
```  

### [Required][Story] 编写故事工序

- 创建Story, 填写参数(参考`com/thoughtworks/projects/order/Story-1001.kt`)

```
object StoryName: StoryOf(
    title       = String,                       // 标题
    estimation  = Estimation | Int,             // 估点
    cardId      = String,                       // 卡号 (jira id)
    cardType    = CardType.STORY,               // 类型
    configure   = lambda{},                     // 工序描述DSL
    tracking    = lambda{},                     // 效能追踪DSL
)

fun main() {
    Story10001.print()                          // BOOM!!! It's done~
}
```

- 按照DSL书写工序

```
inScope { 文字描述 }
outOfScope { 文字描述 }
ac {
    description { 文字描述 }
    example { 文字描述 }
    mockup { 图片地址 }
    links { 链接地址 }
    notes { 备注信息 }
    flows ( 目的 ) {
        task ( 架构节点 入参 返回 API场景) {
            // 多层依赖调用 e.g. A->B B->C C->B B-A
            task ( ... )
        } 
    }
}
```

### [Optional][Performance] 更新效能指标

开卡、结卡、完成task... 记录效能(参考 `com/thoughtworks/projects/order/Story-1001.kt`)

```
// TODO
```

### [Optional][Repo] CI Automation

编写了一个自动任务, 检测当前commits修改的文件, 并重新编译相关的Stories(见：`src/main/kotlin/com/thoughtworks/blackhorse/automation/AutoBuild.kt`)
- 本地：使用Git Diff, 计算修改文件
  > `cli/diffile.sh`
- CI：使用Pipeline获取修改文件, 并作为参数传递到gradle task`./gradlew rebuild`
  > Github actions: `.github/workflows/build.yml`

## Demo

### + Architecture

![architecture](./docs/architecture_dsl.jpg)

### + Story

![story](./docs/story_dsl.jpg)

### = Markdown

![markdown_preview](./docs/markdown_preview.png)

### => PDF

![pdf_preview](./docs/pdf_preview.png)

##### 代码架构

- Schema 模型
    - Node 架构节点
        - Container 进程应用
            - Process 工序定义
        - Component 进程内组件
    - Story 故事卡
        - AcceptanceCriteria AC描述
        - Flow 完成AC所需要的一个或多个工作流、调用链
        - Task 组成工作流、调用链的任务
- Printer 打印器
    - MarkdownPrinter 输出Markdown文件
        - Typora...Formatter 按Markdown语法序列化Story
    - Plantuml 输出uml图片
        - Plantuml...Formatter 按uml语法绘制流程图
    - PandocPdfPrinter 输出pdf文件
    - JiraPrinter 输出到Jira的Issue卡中
- GlobalConfig 全局配置加载和读取
