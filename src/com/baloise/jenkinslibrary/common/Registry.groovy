package com.baloise.jenkinslibrary.common


import com.baloise.jenkinslibrary.common.logging.LoggingApi
import com.baloise.jenkinslibrary.common.logging.internal.JenkinsConsoleLogger
import com.baloise.jenkinslibrary.common.rest.RestClientApi
import com.baloise.jenkinslibrary.common.rest.internal.BasicRestClient
import com.baloise.jenkinslibrary.container.ContainerApi
import com.baloise.jenkinslibrary.container.internal.BuildahContainerEngine
import com.baloise.jenkinslibrary.gitops.GitopsApi
import com.baloise.jenkinslibrary.gitops.internal.GitopsService
import com.cloudbees.groovy.cps.NonCPS

class Registry {

    def apiRegistry = new HashMap()
    def steps
    Variables variables

    Registry(steps) {
        this.steps = steps
        this.variables = new Variables(steps)
        // Registration order must respect dependencies
        registerApi(LoggingApi.class, new JenkinsConsoleLogger(this))
        registerApi(RestClientApi.class, new BasicRestClient(this))
        registerApi(GitopsApi.class, new GitopsService(this))
        registerApi(ContainerApi.class, new BuildahContainerEngine(this))
    }

    @NonCPS
    <T> void registerApi(Class<T> apiClass, T impl) {
        apiRegistry.put(apiClass, impl)
    }

    @NonCPS
    <T> T getApi(Class<T> apiClass) {
        return (T) this.apiRegistry.get(apiClass)
    }
}
