package com.thoughtworks.blackhorse.config

enum class TitleLanguage {
    EN, ZH;
}

enum class Titles(private val en: String, private val zh: String) {
    Process(
        "Process",
        "工序"
    ),
    Diagram(
        "Diagram",
        "时序图"
    ),
    Tasks(
        "Tasks",
        "任务列表"
    ),
    Example(
        "Example",
        "示例"
    ),
    Processes(
        "Processes",
        "工序拆分"
    ),
    Architecture(
        "Related Architecture",
        "进程内架构设计"
    )
    ;

    override fun toString() = when (ProjectContextHolder.get().language) {
        TitleLanguage.EN -> en
        TitleLanguage.ZH -> zh
    }
}
