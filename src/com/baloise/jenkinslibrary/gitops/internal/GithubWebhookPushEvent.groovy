package com.baloise.jenkinslibrary.gitops.internal;

class GithubWebhookPushEvent implements WebhookPushEvent {

    Map payload;

    GithubWebhookPushEvent(Map payload) {
        this.payload = payload
    }

    String org() {
        return payload["organization"]["login"]
    }

    String repo() {
        return payload["repository"]["name"]
    }

    boolean isMasterBranch() {
        return "refs/heads/master".equals(payload["ref"]) | "refs/heads/main".equals(payload["ref"])
    }

}
