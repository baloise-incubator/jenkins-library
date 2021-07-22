package com.baloise.jenkinslibrary.common.rest

class HttpHeaders implements Serializable {

    List headers = [];

    HttpHeaders(String authorization) {
        this.headers.add([name: "authorization", value: authorization])
    }
}