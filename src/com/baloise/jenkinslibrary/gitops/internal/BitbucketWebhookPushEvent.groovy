package com.baloise.jenkinslibrary.gitops.internal;

class BitbucketWebhookPushEvent implements WebhookPushEvent {

    Map payload;

    BitbucketWebhookPushEvent(Map payload) {
        this.payload = payload
    }

    String org() {
        return payload["repository"]["project"]["key"]
    }

    String repo() {
        return payload["repository"]["slug"]
    }

    boolean isMasterBranch() {
        return "refs/heads/master".equals(payload["changes"][0]["refId"])
    }

}
