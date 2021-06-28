
def call(input) {
    if (!input) {
        input = [:]
    }
    this.container(name: 'buildah') {
        this.withVault(configuration: [timeout: 60, vaultCredentialId: 'vault_token', vaultUrl: 'http://vault:8200'], vaultSecrets: [[path: '/secret/registry', secretValues: [[envVar: 'USERNAME', vaultKey: 'username'], [envVar: 'PASSWORD', vaultKey: 'password']]]]) {
            this.sh 'buildah login -u $USERNAME -p $PASSWORD registry.baloise.dev'
        }
        this.sh 'buildah bud -f $(pwd)/Dockerfile -t registry.baloise.dev/' + input.repository
        this.sh 'buildah push registry.baloise.dev/' + input.repository
    }
}
