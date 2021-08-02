package com.baloise.jenkinslibrary.gitops.internal;

class GithubWebhookPullRequestCommentEvent extends GithubWebhookPullRequestEvent {

    GithubWebhookPullRequestCommentEvent(Map payload) {
        super(payload)
    }

    @Override
    String prId() {
        return this.payload["issue"]["number"]
    }
}
