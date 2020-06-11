// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.service;

import io.dolittle.moose.pinger.component.KeyManager;
import io.dolittle.moose.pinger.model.PingHost;
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

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * A service to ping hosts
 * The service is dependent on {@link KeyManager} to verify a successful ping to a host
 */
@Service
@Slf4j
public class RequestService {

    private final KeyManager _keyManager;

    @Autowired
    public RequestService(KeyManager keyManager) {
        this._keyManager = keyManager;
    }

    @Async
    public CompletableFuture<HashMap<String, Boolean>> pingHost(PingHost pingHost) {
        HashMap<String, Boolean> result = new HashMap<>();

        var host = pingHost.getHost();
        result.put(host, Boolean.FALSE);

        var challengeKey = _keyManager.addChallengeKeyBeforePingRequest();
        var url = pingHost.getURL() + "/" + challengeKey;

        var restTemplate = RESTUtil.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "Dolittle/Moose");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        try {
            log.info("Pinging: {}, challenge-key: {}", url, challengeKey);
            response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCodeValue() == 200) {
                result.put(host, _keyManager.verifyChallengeKeyAfterResponse(challengeKey));
            }

        } catch (RestClientException e) {
            log.error("Error pinging: " + url);
            log.debug("Stack trace", e);
            return CompletableFuture.completedFuture(result);
        }

        return CompletableFuture.completedFuture(result);
    }

}
