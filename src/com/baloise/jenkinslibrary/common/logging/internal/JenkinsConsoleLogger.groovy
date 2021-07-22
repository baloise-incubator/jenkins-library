package com.baloise.jenkinslibrary.common.logging.internal

import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.common.logging.LoggingApi

class JenkinsConsoleLogger implements LoggingApi, Serializable {

    def steps

    JenkinsConsoleLogger(Registry registry) {
        this.steps = registry.getSteps()
    }

    void printError(String message) {
        steps.ansiColor('xterm') {
            steps.echo "\u001B[1;91m [ERROR] ${message} \u001B[0m"
        }
    }


    void printInfo(String message) {
        steps.ansiColor('xterm') {
            steps.echo "\u001B[1;34m [INFO] ${message} [0m"
        }
    }

    void printWarn(String message) {
        steps.ansiColor('xterm') {
            steps.echo "\u001B[1;35m [WARN] ${message} \u001B[0m"
        }
    }
}
