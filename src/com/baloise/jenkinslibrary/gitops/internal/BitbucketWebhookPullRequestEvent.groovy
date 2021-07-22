package com.baloise.jenkinslibrary.gitops.internal;

class BitbucketWebhookPullRequestEvent implements WebhookPullRequestEvent {

    Map payload;

    BitbucketWebhookPullRequestEvent(Map payload) {
        this.payload = payload
    }

    String org() {
        return payload["pullRequest"]["fromRef"]["repository"]["project"]["key"]
    }

    String repo() {
        return payload["pullRequest"]["fromRef"]["repository"]["slug"]
    }

    String prId() {
        return payload["pullRequest"]["id"]
    }

    String branch() {
        return payload["pullRequest"]["fromRef"]["displayId"]
    }
}
