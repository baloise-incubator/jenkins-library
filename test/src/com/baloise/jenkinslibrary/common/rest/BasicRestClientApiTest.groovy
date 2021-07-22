package com.baloise.jenkinslibrary.common.rest

import com.baloise.jenkinslibrary.JenkinsMock
import com.baloise.jenkinslibrary.common.Registry
import com.baloise.jenkinslibrary.common.rest.internal.BasicRestClient
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class BasicRestClientApiTest {

    RestClientApi basicRestService
    def mockedJenkins

    @Before
    void setUp() {
        mockedJenkins = JenkinsMock.create()
        basicRestService = new BasicRestClient(new Registry(mockedJenkins))
    }

    @Test
    void restservice_get_noCustomHeaders() {
        def httpParams = basicRestService.get("https://mockedUrl.tld")

        assertThat(httpParams["httpMode"]).isEqualTo('GET')
        assertThat(httpParams["acceptType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["contentType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["requestBody"]).isEqualTo('')
        assertThat(httpParams["url"]).isEqualTo('https://mockedUrl.tld')
        assertThat(httpParams["customHeaders"]).isEmpty()
    }

    @Test
    void restservice_get_customHeaders() {
        def httpParams = basicRestService.get("https://mockedUrl.tld", new HttpHeaders("someauthstring"))

        assertThat(httpParams["customHeaders"]).isEqualTo([[name: "authorization", value: "someauthstring"]])
    }

    @Test
    void restservice_post() {
        def httpParams = basicRestService.post("https://mockedUrl.tld", "mybody")

        assertThat(httpParams["httpMode"]).isEqualTo('POST')
        assertThat(httpParams["acceptType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["contentType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["requestBody"]).isEqualTo('mybody')
        assertThat(httpParams["url"]).isEqualTo('https://mockedUrl.tld')
        assertThat(httpParams["customHeaders"]).isEmpty()
    }

    @Test
    void restservice_put() {
        def httpParams = basicRestService.put("https://mockedUrl.tld", "mybody")

        assertThat(httpParams["httpMode"]).isEqualTo('PUT')
        assertThat(httpParams["acceptType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["contentType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["requestBody"]).isEqualTo('mybody')
        assertThat(httpParams["url"]).isEqualTo('https://mockedUrl.tld')
        assertThat(httpParams["customHeaders"]).isEmpty()
    }

    @Test
    void restservice_delete() {
        def httpParams = basicRestService.delete("https://mockedUrl.tld")

        assertThat(httpParams["httpMode"]).isEqualTo('DELETE')
        assertThat(httpParams["acceptType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["contentType"]).isEqualTo('APPLICATION_JSON')
        assertThat(httpParams["requestBody"]).isEmpty()
        assertThat(httpParams["url"]).isEqualTo('https://mockedUrl.tld')
        assertThat(httpParams["customHeaders"]).isEmpty()
    }
}
