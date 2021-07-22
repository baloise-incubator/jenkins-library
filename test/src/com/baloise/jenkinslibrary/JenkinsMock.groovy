package com.baloise.jenkinslibrary

class JenkinsMock {

    static create() {
        def jenkins = [:]
        jenkins.echo = { text -> println "echo: " + text }
        jenkins.sh = { cmd -> println "executed shell cmd: " + cmd}
        jenkins.ansiColor = { type, func -> func() }

        jenkins.git = { input -> println input }
        jenkins.error = { text -> println text }
        jenkins.withEnv = { array, func -> func() }
        jenkins.withVault = { array, func -> func() }
        jenkins.container = { array, func -> func() }
        jenkins.tool = { name -> return "/opt/maven" }
        jenkins.pwd = { -> return "/var/lib/jenkins/workspace/folder/job" }
        jenkins.scm = null
        jenkins.stage = { text, func -> println(text); func() }
        jenkins.wrap = { map, func -> func() }
        jenkins.answerInput = { input -> println "answer input" }

        jenkins.log = []

        jenkins.env = [:]
        jenkins.env.GIT_PROVIDER_URL = "https://bitbucket.baloise.dev"
        jenkins.env.GIT_PROVIDER = "bitbucket"
        jenkins.env.CHARTMUSEUM_URL = "https://charts.shapp.os1.baloise.dev"
        jenkins.env.REGISTRY_URL = "quay.baloise.dev"
        jenkins.env.JENKINS_URL = "https://ci.baloise.dev/"
        jenkins.env.BUILD_USER_ID = "test"
        jenkins.env.BUILD_NUMBER = 12
        jenkins.env.BUILD_URL = "${jenkins.env.JENKINS_URL}job/foo/12"
        jenkins.env.JOB_NAME = "job/foo"
        jenkins.env.BUILD_TAG = "jenkins-${jenkins.env.JOB_NAME}-${jenkins.env.BUILD_NUMBER}"
        jenkins.env.BRANCH_NAME = "feat/branchname"
        jenkins.env.getProperty = { name -> return name }
        jenkins.currentBuild = [:]

        jenkins.USERNAME = "mockedUsername"
        jenkins.PASSWORD = "mockedPassword"
        jenkins.TOKEN = "mockedToken"


        jenkins.readYaml = { input ->
            jenkins.fileSystem.readYaml(input.file)
        }
        jenkins.writeFile = {
            file -> jenkins.fileSystem.writeFile(file)
        }

        jenkins.readFile = { path -> println "file read: " + path; return jenkins.fileSystem.readFile(path) }
        jenkins.fileExists = { name -> return jenkins.fileSystem.fileExists(name) }
        jenkins.sleep = { input -> jenkins.sleepMock.addSleep(input.time, input.unit) }
        jenkins.httpRequest = { map -> return map }

        return jenkins
    }


}
