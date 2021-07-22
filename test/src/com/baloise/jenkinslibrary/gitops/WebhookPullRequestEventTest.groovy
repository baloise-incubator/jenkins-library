package com.baloise.jenkinslibrary.gitops

import com.baloise.jenkinslibrary.gitops.internal.BitbucketWebhookPullRequestEvent
import com.baloise.jenkinslibrary.gitops.internal.WebhookPullRequestEvent
import groovy.json.JsonSlurper
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class WebhookPullRequestEventTest {

    @Test
    void bitbucket_parsePrEvent() {
        def json = new JsonSlurper().parse(getClass().getResource('/bitbucket/PrEvent.json'));

        def pushEvent = new BitbucketWebhookPullRequestEvent((json))

        assertThat(pushEvent.org()).isEqualTo("demo")
        assertThat(pushEvent.repo()).isEqualTo("demo-app")
        assertThat(pushEvent.prId()).is(44)
        assertThat(pushEvent.branch()).isEqualTo("Feat/cool")
    }
}
