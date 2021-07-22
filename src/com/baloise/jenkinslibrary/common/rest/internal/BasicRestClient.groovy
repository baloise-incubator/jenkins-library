package com.baloise.jenkinslibrary.common.rest.internal

import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.common.rest.RestClientApi
import com.baloise.jenkinslibrary.common.rest.HttpHeaders

class BasicRestClient implements RestClientApi, Serializable {
    def steps

    BasicRestClient(Registry registry) {
        this.steps = registry.getSteps()
    }

    def get(String apiUrl, HttpHeaders httpHeaders = null) {
        this.apiCall(apiUrl, "GET", httpHeaders)
    }

    def post(String apiUrl, String jsonPayload = '', HttpHeaders httpHeaders = null) {
        this.apiCall(apiUrl, "POST", jsonPayload, httpHeaders)
    }

    def put(String apiUrl, String jsonPayload = '', HttpHeaders httpHeaders = null) {
        this.apiCall(apiUrl, "PUT", jsonPayload, httpHeaders)
    }

    def delete(String apiUrl, HttpHeaders httpHeaders = null) {
        this.apiCall(apiUrl, "DELETE", httpHeaders)
    }

    private apiCall(String apiUrl, String httpMethod, String jsonPayload = '', HttpHeaders httpHeaders) {
        return steps.httpRequest(
                acceptType: 'APPLICATION_JSON',
                contentType: 'APPLICATION_JSON',
                httpMode: httpMethod,
                requestBody: jsonPayload,
                url: apiUrl,
                customHeaders: httpHeaders ? httpHeaders.getHeaders() : []
        )
    }
}