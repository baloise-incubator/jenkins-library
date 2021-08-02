package com.baloise.jenkinslibrary.container

interface ContainerApi {

    def build(String registry, String repository, List tags, String dockerFileName, String path, Boolean cacheLayers, List buildArgs, Boolean pullAlways)

}
