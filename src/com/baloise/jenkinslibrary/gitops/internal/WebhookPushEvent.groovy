package com.baloise.jenkinslibrary.gitops.internal;

interface WebhookPushEvent extends Serializable {

    String org()

    String repo()

    boolean isMasterBranch()

}
