package com.baloise.jenkinslibrary.gitops

import com.baloise.jenkinslibrary.JenkinsMock
import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.gitops.internal.GitopsService
import groovy.json.JsonSlurper
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GitopsServiceTest {

    Map mockedJenkins
    String executedCommand
    GitopsApi gitops

    @Before
    void setUp() {
        mockedJenkins = JenkinsMock.create()
        mockedJenkins.sh = { cmd -> executedCommand = cmd }
        gitops = new GitopsService(new Registry(mockedJenkins))
    }

    @Test
    void createComment() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.createComment(json, "awesomeMockedComment")

        assertThat(executedCommand).contains('add-pr-comment')
        assertThat(executedCommand).contains('--git-provider-url https://bitbucket.baloise.dev')
        assertThat(executedCommand).contains('--text "awesomeMockedComment"')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --pr-id "12"')
    }

    @Test
    void createComment_parentId() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.createComment(json, "awesomeMockedComment", 12)

        assertThat(executedCommand).contains('add-pr-comment')
        assertThat(executedCommand).contains('--git-provider-url https://bitbucket.baloise.dev')
        assertThat(executedCommand).contains('--text "awesomeMockedComment"')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --pr-id "12"')
        assertThat(executedCommand).contains('--parent-id 12')
    }

    @Test
    void createPrPreview() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.createPrPreview(json)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('create-pr-preview')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --pr-id "12"')
    }

    @Test
    void createPreview() {
        List<String> otherCommands = []
        mockedJenkins.sh = { cmd -> if (cmd.contains("gitopscli ")) executedCommand += cmd else otherCommands += cmd }
        mockedJenkins.pwd = { input -> input.tmp ? '/tmp/dir' : '/notmp/dir'}
        mockedJenkins.readYaml = { input -> input.file == '/tmp/gitopscli-preview-info.yaml' ? [
            "previewId": "DUMMY_ID",
            "previewIdHash": "DUMMY_HASH",
            "routeHost": "DUMMY_ROUTE",
            "namespace": "DUMMY_NAMESPACE"
        ] : [:] }

        def previewInfo = gitops.createPreview("org", "repo", "previewId", "9748e498155f782eddf48d35f6d4a1676f08d196")

        assertThat(previewInfo).containsKeys("id", "idHash", "route", "namespace")
        assertThat(previewInfo.id).isEqualTo("DUMMY_ID")
        assertThat(previewInfo.idHash).isEqualTo("DUMMY_HASH")
        assertThat(previewInfo.route).isEqualTo("DUMMY_ROUTE")
        assertThat(previewInfo.namespace).isEqualTo("DUMMY_NAMESPACE")

        assertThat(otherCommands).hasSize(1)
        assertThat(otherCommands[0].toString()).isEqualTo("rm -f /tmp/gitopscli-preview-info.yaml")

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('create-preview')
        assertThat(executedCommand).contains('--organisation "org" --repository-name "repo"')
        assertThat(executedCommand).contains('--preview-id "previewId"')
        assertThat(executedCommand).contains('--git-hash "9748e498155f782eddf48d35f6d4a1676f08d196"')
    }

    @Test
    void createPrPreview_parentIdCreatePr() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.createPrPreview(json, 12)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('create-pr-preview')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --pr-id "12"')
        assertThat(executedCommand).contains('--parent-id 12')
    }

    @Test
    void deletePreview() {
        gitops.deletePreview("org", "repo", "previewId")

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('delete-preview')
        assertThat(executedCommand).contains('--organisation "org" --repository-name "repo" --preview-id "previewId"')
    }

    @Test
    void deletePrPreview() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.deletePrPreview(json)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('delete-pr-preview')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --branch "Feat/cool"')
    }

    @Test
    void deletePrPreview_withPR() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        gitops.deletePrPreview(json)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('delete-pr-preview')
        assertThat(executedCommand).contains('--organisation "demo" --repository-name "demo-app" --branch "Feat/cool"')
    }

    @Test
    void syncApps() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PushEvent.json'));

        gitops.syncApps(json, "rootOrg", "rootRepo")

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('sync-apps')
        assertThat(executedCommand).contains('--organisation "DPL" --repository-name "example-team-non-prod" --root-organisation "rootOrg" --root-repository-name "rootRepo"')
    }

    @Test
    void deploy() {
        gitops.deploy("someotherorg", "example-non-prod", "demo-app-test/values.yaml", ["generic.image.tag": "v1.0.0"], false, true, null)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('deploy')
        assertThat(executedCommand).contains('--organisation "someotherorg" --repository-name "example-non-prod" --file "demo-app-test/values.yaml"')
        assertThat(executedCommand).contains('--values "{\\"generic.image.tag\\":\\"v1.0.0\\"}"')
        assertThat(executedCommand).contains('--create-pr --auto-merge')
        assertThat(executedCommand).doesNotContain('--single-commit')
        assertThat(executedCommand).doesNotContain('--commit-message')
    }

    @Test
    void deploy_withDefaults() {
        gitops.deploy(null, "example-non-prod", "demo-app-test/values.yaml", ["generic.image.tag": "v1.0.0"], null, null, null)

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('deploy')
        assertThat(executedCommand).contains('--organisation "DPL" --repository-name "example-non-prod" --file "demo-app-test/values.yaml"')
        assertThat(executedCommand).contains('--values "{\\"generic.image.tag\\":\\"v1.0.0\\"}"')
        assertThat(executedCommand).doesNotContain('--create-pr --auto-merge')
        assertThat(executedCommand).contains('--single-commit')
        assertThat(executedCommand).doesNotContain('--commit-message')
    }

    @Test
    void deploy_withCommitMessage() {
        gitops.deploy(null, "example-non-prod", "demo-app-test/values.yaml", ["generic.image.tag": "v1.0.0"], null, null, "Deploy demo release v1.0.0")

        assertBasicGitopsCliCommands()
        assertThat(executedCommand).contains('deploy')
        assertThat(executedCommand).contains('--organisation "DPL" --repository-name "example-non-prod" --file "demo-app-test/values.yaml"')
        assertThat(executedCommand).contains('--values "{\\"generic.image.tag\\":\\"v1.0.0\\"}"')
        assertThat(executedCommand).doesNotContain('--create-pr --auto-merge')
        assertThat(executedCommand).contains('--single-commit')
        assertThat(executedCommand).contains('--commit-message "Deploy demo release v1.0.0"')
    }

    void assertBasicGitopsCliCommands() {
        assertThat(executedCommand).contains('--git-user "Baloise Incubator" --git-email "incubator@baloise.dev"')
        assertThat(executedCommand).contains('--git-provider-url https://bitbucket.baloise.dev')
    }
}
