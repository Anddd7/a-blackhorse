package com.thoughtworks.blackhorse.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.streams.toList

object ShellOperation {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun execute(cli: ShellCli, vararg parameters: Any, success: (List<String>) -> Unit = { it.forEach(log::info) }) {
        val cmd = cli.with(*parameters)

        log.info("Shell Execution: $cmd")

        val pr = Runtime.getRuntime().exec(cmd).apply { waitFor() }
        BufferedReader(InputStreamReader(pr.inputStream)).apply {
            success(lines().toList())
        }
        BufferedReader(InputStreamReader(pr.errorStream)).apply {
            var line: String?
            while (readLine().also { line = it } != null) log.error(line)
        }
    }

    private fun ShellCli.with(vararg parameters: Any) = target + " " + parameters.joinToString(" ")
}

enum class ShellCli(val target: String) {
    PANDOC("./cli/pandoc.sh"),
    DIFFILE("./cli/diffile.sh"),
}
