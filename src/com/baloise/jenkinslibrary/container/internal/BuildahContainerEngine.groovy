package com.baloise.jenkinslibrary.container.internal

import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.common.Variables
import com.baloise.jenkinslibrary.container.ContainerApi
import groovy.json.JsonSlurper

class BuildahContainerEngine implements ContainerApi, Serializable {

    def steps
    private Variables variables

    BuildahContainerEngine(Registry registry) {
        this.variables = registry.getVariables()
        this.steps = registry.getSteps()
    }

    def build(String registry, String repository, List tags, String dockerFileName, String path, Boolean cacheLayers, List buildArgs, Boolean pullAlways) {
        if (registry == null) {
            registry = variables.REGISTRY_URL
        }
        if (dockerFileName == null) {
            dockerFileName = "Dockerfile"
        }

        if (path == null) {
            path = "."
        }

        if (tags == null) {
            tags = ['latest']
        }
        if (cacheLayers == null) {
            cacheLayers = false
        }

        if (buildArgs == null) {
            buildArgs = []
        }

        if (pullAlways == null) {
            pullAlways = true
        }

        def buildArgString = ""
        buildArgs.each {
            buildArgString += " --build-arg $it"
        }

        def tagString = ""
        tags.each {
            tagString += " -t ${registry}/${repository}:${it}"
        }
        steps.container(name: 'buildah') {
            steps.withVault(vaultSecrets: [[path: 'secret/data/registry.baloise.dev/username', secretValues: [[envVar: "registryUsername", vaultKey: 'data']]],
                                           [path: 'secret/data/registry.baloise.dev/password', secretValues: [[envVar: "registryPassword", vaultKey: 'data']]]]) {
                def username = new JsonSlurper().parseText(steps.env.registryUsername)["registry.baloise.dev/username"]
                def password = new JsonSlurper().parseText(steps.env.registryPassword)["registry.baloise.dev/password"]
                steps.withEnv(["REGISTRY_USERNAME=${username}", "REGISTRY_PASSWORD=${password}", "REGISTRY_URL=${registry}"]) {
                    steps.sh 'set +x; buildah login -u $REGISTRY_USERNAME -p $REGISTRY_PASSWORD $REGISTRY_URL'
                }
            }
            steps.sh "buildah bud --layers=${cacheLayers} --pull-always=${pullAlways} -f ${dockerFileName} ${tagString} ${path} ${buildArgString}"
            tags.each {
                steps.sh "buildah push ${registry}/${repository}:${it}"
            }
        }
    }
}
