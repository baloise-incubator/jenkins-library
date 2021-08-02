import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.container.ContainerApi

def call(input) {
    if (!input) {
        input = [:]
    }
    Registry registry = new Registry(this)
    ContainerApi container = registry.getApi(ContainerApi.class)
    container.build(input.registry, input.repository, input.tags, input.dockerFileName, input.path, input.cacheLayers, input.buildArgs, input.pullAlways)
}
