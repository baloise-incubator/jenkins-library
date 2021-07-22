package com.baloise.jenkinslibrary.gitops

interface GitopsApi {

    void createComment(Map webhookChangeEventPayload, String comment, Integer parentId)

    Map createPreview(String organisation, String repository, String previewId, String gitHash)

    void createPrPreview(Map webhookChangeEventPayload, Integer parentId)

    void deletePreview(String organisation, String repository, String previewId)

    void deletePrPreview(Map webhookChangeEventPayload)

    void syncApps(Map webhookChangeEventPayload, String rootOrganisation, String rootRepositoryName)

    void deploy(String organisation, String repositoryName, String file, yamlPatches, Boolean singleCommit, Boolean createPullRequest, String commitMessage)
}
