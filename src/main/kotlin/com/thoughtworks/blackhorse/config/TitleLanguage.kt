package com.thoughtworks.blackhorse.config

enum class TitleLanguage {
    EN, ZH;

    companion object {
        fun getProcessTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Process"
            ZH -> "工序"
        }

        fun getDiagramTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Diagram"
            ZH -> "时序图"
        }

        fun getTasksTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Tasks"
            ZH -> "任务列表"
        }

        fun getExampleTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Example"
            ZH -> "示例"
        }

        fun getProcessesTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Processes"
            ZH -> "工序拆分"
        }

        fun getArchitectureTitle() = when (ProjectContextHolder.get().language) {
            EN -> "Related Architecture"
            ZH -> "进程内架构设计"
        }
    }
}
