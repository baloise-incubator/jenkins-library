package com.baloise.jenkinslibrary.gitops

import com.baloise.jenkinslibrary.gitops.internal.BitbucketWebhookPushEvent
import com.baloise.jenkinslibrary.gitops.internal.WebhookPushEvent
import groovy.json.JsonSlurper
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class WebhookPushEventTest {

    @Test
    void bitbucket_parseCommentEvent() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PushEvent.json'));

        def commentEvent = new BitbucketWebhookPushEvent(json)

        assertThat(commentEvent.org()).isEqualTo("DPL")
        assertThat(commentEvent.repo()).isEqualTo("example-team-non-prod")
        assertThat(commentEvent.isMasterBranch()).isTrue()
    }
}
