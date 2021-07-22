import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.gitops.GitopsApi

def call(input) {
    if (!input) {
        input = [:]
    }
    Registry registry = new Registry(this)
    GitopsApi gitops = registry.getApi(GitopsApi.class)
    gitops.deletePrPreview(input.payload)
}
