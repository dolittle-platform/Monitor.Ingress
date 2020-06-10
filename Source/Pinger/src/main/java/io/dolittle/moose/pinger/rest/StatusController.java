// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.rest;

import io.dolittle.moose.pinger.component.PingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "dolittle/uptime", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
public class StatusController {

    private final PingStatus _pingStatus;

    @Autowired
    public StatusController(PingStatus pingStatus) {
        _pingStatus = pingStatus;
    }

    @RequestMapping(value = "/status")
    public ResponseEntity<String> Status() {
        var status = _pingStatus.getStatus();
        if (status) {
            return new ResponseEntity<>("{ \"status\":\"OK\" }", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
