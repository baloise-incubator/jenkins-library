package com.baloise.jenkinslibrary.gitops.internal;

class GithubWebhookPullRequestEvent implements WebhookPullRequestEvent {

    Map payload;

    GithubWebhookPullRequestEvent(Map payload) {
        this.payload = payload
    }

    String org() {
        return payload["organization"]["login"]
    }

    String repo() {
        return payload["repository"]["name"]
    }

    String prId() {
        return payload["pullRequest"]["id"]
    }

    String branch() {
        return payload["pullRequest"]["branch"]
    }
}