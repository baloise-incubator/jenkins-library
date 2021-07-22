package com.baloise.jenkinslibrary.common.rest

interface RestClientApi {

    def post(String apiUrl, String jsonPayload, HttpHeaders httpHeaders)

    def delete(String apiUrl, HttpHeaders httpHeaders)

    def put(String apiUrl, String jsonPayload, HttpHeaders httpHeaders)

    def get(String apiUrl, HttpHeaders httpHeader)

}