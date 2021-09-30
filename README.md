# 黑马工序小助手

`PaC = '工序' as Code`

* [Key Benefits](#key-benefits)
* [How to start](#how-to-start)
* [Roadmaps](#roadmaps)
    + [Features](#features)
    + [Backlogs](#backlogs)
    + [Issues](#issues)
* [Technical Design](#technical-design)
  
## Key Benefits

- 利用IDE的提示和纠错，提高书写效率
- 利用代码的抽象和复用，提高书写效率
- 自动扫描并组合api schema，统一显示api定义
- 自动扫描并组合时序图，按组件的架构层级展示链路调用关系
- 输出单一PDF方便阅读
- 支持上传到jira
- 架构分层(Node) + 工序(Process) + 流程描述(Flow) + 任务(task) + DSL描述调用关系 => 时序图，巨量提升可读性

## How to start

`[<Is it required or not?>][<Scope of the task>] <What you need to do>`

### [Required][Repo] Clone

1. `git clone git@github.com:Anddd7/a-blackhorse.git`
2. Open with IDEA
3. Import/init project via gradle

### [Optional][Repo] Code Template

1. 导入`code-template`
    - `file-blackhorse`: Story文件Template
    - `live-ac`: ac片段Template, 自定义short command

### [Required][Project] 初始化项目配置

1. 在 `resources/projects.properties` 中添加项目名(`,`分隔多个项目)
    ```properties
    projects=<your project name>,...,<your project name>
    ```
2. 创建项目配置 `resources/projects/<your project name>/configure.properties` 并按如下规则写入配置信息
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
    # 远程仓库的地址, 用于生成链接直接访问仓库中的文件
    repo_baseurl=https://github.com/Anddd7/a-blackhorse/blob
    # [OPTIONAL, default = en] 文档标签支持的语言
    language=zh
    ```

3. 创建项目工作路径 `com/thoughtworks/projects/<your project name>`
   > WARNING: 生成时会按`<your project name>`来寻找配置, 需要保证这个别名是一个valid package name

### [Required][Project] 定义工序

1. 创建项目架构描述, 定义系统、进程、进程内的组件模块
    ```kotlin
    /**
     * @see com/thoughtworks/projects/rental/onboarding/architecture/application/PopularizationApplication.kt
     */
    object PopularizationApplication : Container(
        // 进程编号 —— 用于区分各进程的工序
        id = "32",
        // 进程间分层 —— 按职责划分，可自定义
        layer = Responsibility.ApplicationService,
        // 技术栈描述
        techStack = listOf(
            TechStack("Spring", "使用Spring生态搭建后端服务"),
            TechStack("Kotlin", "vs Java，语法简洁、并发支持更好"),
        ),
        // 进程职责
        responsibility = "推广服务应用服务: 向前端服务/应用提供推广服务和与充值服务相关的功能接口",
        // 负责人（背锅侠）
        owner = listOf(Li4)
    ) {
        // 进程内组件
        object Interceptor : Component(
            // 进程内分层
            layer = CommonComponentLayer.Authentication,
            // 技术栈描述
            techStack = listOf(
                TechStack("Spring Servlet"),
            ),
            // 组件职责
            responsibility = "拦截请求并验证发起方身份"
        )
    
        object ApiController : Component(...)
        object Service : Component(...)
        ...
    )
    ```  

2. 定义工序
    ```kotlin
    // inside Container
    override fun getProcesses(): List<ProcessDefBuilder> = listOf(
        // <进程内组件> <测试替身> with <测试工具> cost <效能基线> at { <工序描述> }
        Interceptor stub ApiController cost 10 at {
            """
                Interceptor和Controller依赖于Spring，需要启动整个容器参与测试；
                ```
                // Q1 组件测试，基于Spring Security Test
                ```
            """.trimIndent()
        },
        ApiController mock Service at { ... },
        Service mock Service at { ... },
        Service mock Client at { ... },
        Client spy MessageQueue.SQS with "Wiremock" at { ... },
        Client mock PrepaidService.ApiController with "Wiremock" at { ... },
    )
    ```

### [Required][Story] 编写故事，拆分任务

1. 创建Story, 填写参数
    ```kotlin
    /**
     * @see /Users/adliao/Repos/a-blackhorse/src/main/kotlin/com/thoughtworks/projects/rental/onboarding/baseline/Baseline001.kt
     */
    object StoryName: StoryOf(
        title       = String,                       // 标题~~
        estimation  = Estimation | Int,             // 估点
        cardId      = String,                       // 卡号 (jira id)
        cardType    = CardType.STORY,               // 类型
        configure   = lambda{},                     // 工序描述DSL
        tracking    = lambda{},                     // 效能追踪DSL
        tags        = List<Tag>,                    // (TODO) 标签管理
    )
    
    fun main() {
        StoryName.print()                          // BOOM!!! enjoy your popcorn!!!
    }
    ```

2. 按照DSL书写故事和拆分任务
    ```
    inScope { 文字描述 }
    outOfScope { 文字描述 }
    ac {
        description { 文字描述 }
        mockup { 图片地址 }
        link { 链接地址 }
        note { 备注信息 }
        flows ( example ) {
            <component> (交互信息) <next component> {
                // 多层依赖调用 e.g. A->B B->C C->B B-A
                <component> (交互信息) <next component>
            } 
        }
    }
    ```

3. 定义和使用API
    ```
    val addRefundEventApi = API("提交待退款请求", HttpMethod.POST, "/events") { request json }
    ...
    // in flows { }
    <component> call <another component> withApi addRefundEventApi.onSuccess()
    ```

### [Optional][Architecture] 进程间/进程内架构图
1. 创建进程并添加描述(见`[Required][Project] 定义工序`)
2. 创建项目架构
    ```kotlin
    object RentalSystemArchitecture : ArchitectureOf(
        // 架构版本号
        version = "4.1.1",
        // 本次架构变更的原因 
        changelogs = 
        """
            修改工序描述（主要是interceptor和controller如何集成和测试）;
            (重新生成缺失的架构图)
        """.trimIndent(),
        // 添加所有进程Container
        // frontend
        GeneralWebPortal,
        IndividualApp4Android,
        IndividualApp4IOS,
        OfficerApp4Android,
        OfficerApp4IOS,
        ManagementPortal,
        // bff
        MobileApiBff,
        WebApiBff,
        // application
        ManagementApplication,
        PopularizationApplication,
        RentalApplication,
        // core biz
        PopularizationService,
        PrepaidService,
        // domain
        AuthenticationService,
        RentalInfoMgmtService,
        UserManagementService
    )
    
    fun main() {
        RentalSystemArchitecture.print()    // BOOM!!
    }
    ```
### [Optional][Performance] 更新效能指标

1. 开卡、结卡、完成task... 通过Git change记录效能
    ```
    tracking = {
        decomposition(<拆卡人>, <拆卡时间>)
        development(<开发者>, <捡卡时间>)
        finish(<开发者>, <结卡时间>)
        ac {
            flow { 
                // process cost <实际花费时间> blocked <block 时间> by <block 原因> resolved <解决方案>
                process("1-1") cost 30 blocked 20 cause "studying" by BlockType.STUDY resolved "share to team member via session" 
            }
        }
    }
    ```
2. 输出效能报告: `src.main.kotlin.com.thoughtworks.blackhorse.reporter.PerformancePrinter#main()`

### [Optional][Repo] CI Automation

// TODO, 修改了架构文件的构造方式，需要refactor；此功能暂时关闭

编写了一些自动任务, 用于检测当前commits修改的文件, 并重新编译相关的Stories(见：`src/main/kotlin/com/thoughtworks/blackhorse/automation/AutoBuild.kt`)

- 本地：使用Git Diff, 计算修改文件
  > `cli/diffile.sh`
- CI：使用Pipeline获取修改文件, 并作为参数传递到gradle task`./gradlew rebuild`
  > Github actions: `.github/workflows/build.yml`

---

## Roadmaps

### Features

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

## Technical Design

// TODO updating
