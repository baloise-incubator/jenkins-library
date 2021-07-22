import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.gitops.GitopsApi

def call(input) {
    if (!input) {
        input = [:]
    }
    Registry registry = new Registry(this)
    GitopsApi gitops = registry.getApi(GitopsApi.class)
    gitops.createComment(input.payload, input.comment, input.parentId)
}
