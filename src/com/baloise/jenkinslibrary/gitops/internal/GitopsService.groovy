package com.baloise.jenkinslibrary.gitops.internal

import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.common.Variables
import com.baloise.jenkinslibrary.gitops.GitopsApi
import groovy.json.JsonOutput

class GitopsService implements GitopsApi, Serializable {

    def steps

    def gitProviderArg
    def gitUserArg
    def gitEmailArg

    def createParentIdArg = { it ? "--parent-id ${it}" : "" }
    private Variables variables

    GitopsService(Registry registry) {
        this.variables = registry.getVariables()
        this.steps = registry.getSteps()
        this.gitProviderArg = "--git-provider ${variables.GIT_PROVIDER} --git-provider-url ${variables.GIT_PROVIDER_URL}"
        this.gitUserArg = "--git-user \"${variables.TECHNICAL_GIT_USER}\""
        this.gitEmailArg = "--git-email \"${variables.TECHNICAL_GIT_EMAIL}\""
    }

    private void performGitopsCommand(String command, String args) {
        steps.container(name: 'gitopscli') {
            steps.withVault(vaultSecrets: [[path: 'secret/data/github/username', secretValues: [[envVar: "USERNAME", vaultKey: 'data']]],
                                           [path: 'secret/data/github/token', secretValues: [[envVar: "TOKEN", vaultKey: 'data']]]]) {
                def gitopsCommand = 'gitopscli ' + command + ' --username $USERNAME --password $TOKEN ' + gitProviderArg + ' ' + args
                sh 'echo -n "$USERNAME/$PASSWORD" | base64 '
                return steps.sh(gitopsCommand)
            }
        }
    }

    void createComment(Map webhookChangeEventPayload, String comment, Integer parentId = null) {
        WebhookPullRequestEvent webhookEvent = createWebhookPullRequestEvent(webhookChangeEventPayload)
        String parentIdArg = createParentIdArg(parentId)
        def additionalArgs = """--organisation "${webhookEvent.org()}" \
--repository-name "${webhookEvent.repo()}" \
--pr-id "${webhookEvent.prId()}" \
--text "${comment}" \
$parentIdArg
"""
        performGitopsCommand("add-pr-comment", additionalArgs)
    }

    void createPrPreview(Map webhookChangeEventPayload, Integer parentId = null) {
        WebhookPullRequestEvent webhookEvent = createWebhookPullRequestEvent(webhookChangeEventPayload)
        String parentIdArg = createParentIdArg(parentId)
        def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${webhookEvent.org()}" \
--repository-name "${webhookEvent.repo()}" \
--pr-id "${webhookEvent.prId()}" \
$parentIdArg"""
        performGitopsCommand("create-pr-preview", additionalArgs)
    }

    Map createPreview(String organisation, String repository, String previewId, String gitHash) {
        def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${organisation}" \
--repository-name "${repository}" \
--preview-id "${previewId}" \
--git-hash "${gitHash}"
"""
        def previewInfoFile = "/tmp/gitopscli-preview-info.yaml"
        def previewInfo = [:]
        try {
            performGitopsCommand("create-preview", additionalArgs)
            previewInfo = steps.readYaml(file: previewInfoFile)
        } finally {
            steps.sh("rm -f $previewInfoFile")
        }
        return [
                "id"       : previewInfo.get("previewId"),
                "idHash"   : previewInfo.get("previewIdHash"),
                "route"    : previewInfo.get("routeHost"),
                "namespace": previewInfo.get("namespace"),
        ]
    }

    void deletePrPreview(Map webhookChangeEventPayload) {
        WebhookPullRequestEvent webhookEvent = createWebhookPullRequestEvent(webhookChangeEventPayload)
        def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${webhookEvent.org()}" \
--repository-name "${webhookEvent.repo()}" \
--branch "${webhookEvent.branch()}"
"""
        performGitopsCommand("delete-pr-preview", additionalArgs)
    }

    void deletePreview(String organisation, String repository, String previewId) {
        def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${organisation}" \
--repository-name "${repository}" \
--preview-id "${previewId}"
"""
        performGitopsCommand("delete-preview", additionalArgs)
    }

    WebhookPushEvent createWebhookPushEvent(Map payload) {
        switch (variables.GIT_PROVIDER) {
            case "bitbucket":
                return new BitbucketWebhookPushEvent(payload);
            case "github":
                return new GithubWebhookPushEvent(payload);
            default:
                throw new IllegalStateException("GIT_PROVIDER '" + variables.GIT_PROVIDER + "' is invalid. Must be 'bitbucket'")
        }
    }

    WebhookPullRequestEvent createWebhookPullRequestEvent(Map payload) {
        switch (variables.GIT_PROVIDER) {
            case "bitbucket":
                return new BitbucketWebhookPullRequestEvent(payload);
            case "github":
                def isIssueComment = payload["issue"]
                if (isIssueComment) {
                    return new GithubWebhookPullRequestCommentEvent(payload);
                } else {
                    return new GithubWebhookPullRequestEvent(payload);
                }
            default:
                throw new IllegalStateException("GIT_PROVIDER '" + variables.GIT_PROVIDER + "' is invalid. Must be 'bitbucket|github'")
        }
    }

    void syncApps(Map webhookChangeEventPayload, String rootOrganisation, String rootRepositoryName) {
        WebhookPushEvent commentEvent = createWebhookPushEvent(webhookChangeEventPayload);
        if (commentEvent.isMasterBranch()) {
            def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${commentEvent.org()}" \
--repository-name "${commentEvent.repo()}" \
--root-organisation "${rootOrganisation}" \
--root-repository-name "${rootRepositoryName}"
"""
            performGitopsCommand("sync-apps", additionalArgs)
        }
    }

    void deploy(String organisation, String repositoryName, String file, yamlPatches, Boolean singleCommit, Boolean createPullRequest, String commitMessage) {
        if (organisation == null) {
            organisation = "DPL"
        }

        if (createPullRequest == null) {
            createPullRequest = false
        }

        if (singleCommit == null) {
            singleCommit = true
        }

        def json = JsonOutput.toJson(yamlPatches)
        json = json.replace("\n", " ")
        json = json.replace("\"", "\\\"")
        def singleCommitArg = singleCommit ? "--single-commit" : ""
        def createPrArg = createPullRequest ? "--create-pr --auto-merge" : ""
        def commitMessageArg = commitMessage ? "--commit-message \"${commitMessage}\"" : ""
        def additionalArgs = """$gitUserArg \
$gitEmailArg \
--organisation "${organisation}" \
--repository-name "${repositoryName}" \
--file "${file}" \
--values "${json}" \
$singleCommitArg \
$createPrArg \
$commitMessageArg
"""
        performGitopsCommand("deploy", additionalArgs)
    }
}

