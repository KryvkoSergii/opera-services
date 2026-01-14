package ua.com.goit.clearbreath.analysis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnalysisApplication

fun main(args: Array<String>) {
	runApplication<ua.com.goit.clearbreath.analysis.AnalysisApplication>(*args)
}
