// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.service;

import io.dolittle.moose.pinger.component.KeyManager;
import io.dolittle.moose.pinger.model.PingHost;
import io.dolittle.moose.pinger.model.Response;
import io.dolittle.moose.pinger.util.PingConstants;
import io.dolittle.moose.pinger.util.RESTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RequestService {

    private final KeyManager keyManager;

    @Autowired
    public RequestService(KeyManager keyManager) {

        this.keyManager = keyManager;
    }

    @Async
    public CompletableFuture<HashMap<String, Boolean>> pingHost(PingHost pingHost) {
        HashMap<String, Boolean> result = new HashMap<>();
        result.put(pingHost.getHost(), Boolean.FALSE);

        String url = pingHost.getURL();

        String challengeKey = keyManager.addChallengeKey(pingHost.getHost());

        RestTemplate restTemplate = RESTUtil.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(PingConstants.CHALLENGE_KEY, challengeKey);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Response> exchange;
        try {
            log.info("Pinging: {}, challenge-key: {}", url, challengeKey);
            exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Response.class);

        } catch (RestClientException e) {
            log.error("Error pinging: "+ url);
            log.debug("Stack trace", e);
            return CompletableFuture.completedFuture(result);
        }

        Response response = exchange.getBody();
        assert response != null;

        if (!verifyResponseKey(response, pingHost.getHost())) {
            log.warn("*** UNABLE TO VERIFY -> host: {}, url: {}, challenge-key: {} ***", pingHost.getHost(), pingHost.getURL(), challengeKey);
            return CompletableFuture.completedFuture(result);
        }

        result.put(pingHost.getHost(), Boolean.TRUE);

        return CompletableFuture.completedFuture(result);
    }

    private Boolean verifyResponseKey(Response response, String host) {
        Response.Status status = response.getStatus();
        String responsekey = response.getResponsekey();

        log.debug("Got response: Status - {}, Response-key: {}", status, responsekey);

        if (status.equals(Response.Status.OK)) {
            if (responsekey == null || responsekey.isEmpty()) {
                return false;
            }
            return keyManager.verifyResponseKey(host, responsekey);
        }
        return false;
    }
}
