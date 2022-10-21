package com.baloise.jenkinslibrary.common

class Variables {

    // Those credential ID's need to be present on Jenkins
    // Manage Jenkins -> Manage Credentials
    String REGISTRY_URL
    String GIT_PROVIDER_URL
    String GIT_PROVIDER
    String TECHNICAL_GIT_USER = "Baloise Incubator"
    String TECHNICAL_GIT_EMAIL = "incubator@baloise.dev"
    String VAULT_CREDENTIAL_ID = "vault_token"
    String VAULT_URL = "http://vault:8200"

    Variables(steps) {
        // Those environment variables need to be set on Jenkins
        // Manage Jenkins -> Configure Jenkins -> Global properties
        REGISTRY_URL = steps.env.REGISTRY_URL
        GIT_PROVIDER_URL = steps.env.GIT_PROVIDER_URL
        GIT_PROVIDER = steps.env.GIT_PROVIDER
    }
}
