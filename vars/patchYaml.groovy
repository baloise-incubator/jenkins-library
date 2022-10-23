import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.gitops.DeploymentResult
import com.baloise.jenkinslibrary.gitops.GitopsApi

DeploymentResult call(input) {
    if (!input) {
        input = [:]
    }
    Registry registry = new Registry(this)
    GitopsApi gitops = registry.getApi(GitopsApi)

    gitops.deploy(input.stage, input.organisation, input.repositoryName,
            input.file, input.yamlPatches, input.singleCommit,
            input.createPullRequest, input.commitMessage, input.jira)
}