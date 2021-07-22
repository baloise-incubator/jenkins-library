package com.baloise.jenkinslibrary.gitops.internal;

interface WebhookPullRequestEvent extends Serializable {

    String org()

    String repo()

    String prId()

    String branch()

}
