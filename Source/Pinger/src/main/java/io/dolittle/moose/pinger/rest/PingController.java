// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.rest;

import io.dolittle.moose.pinger.component.KeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "dolittle/ingress", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
@Slf4j
public class PingController {

    private final KeyManager _keyManager;

    public PingController(KeyManager keyManager) {
        this._keyManager = keyManager;
    }

    /**
     * Endpoint for receiving requests
     * @param httpServletRequest
     * @param challenge
     * @return
     * @see io.dolittle.moose.pinger.component.PingManager
     */
    @RequestMapping(value = "/ping/{challenge}")
    public ResponseEntity<String> Ping(HttpServletRequest httpServletRequest, @PathVariable String challenge) {
        var remoteHost = httpServletRequest.getRemoteHost();
        log.debug("Ping request received: {}, challenge-key: {}", remoteHost, challenge);

        if (!_keyManager.updateChallengeKeyWhenPingIsReceived(challenge)) {
            log.warn("Received ping from an unauthorized source: {}", remoteHost);
        }

        return ResponseEntity.ok("ok");
    }


}
