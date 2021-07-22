import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.gitops.GitopsApi

def call(input) {
    if (!input) {
        input = [:]
    }
    Registry registry = new Registry(this)
    GitopsApi gitops = registry.getApi(GitopsApi.class)
    gitops.deletePreview(input.organisation, input.repository, input.previewId)
}
