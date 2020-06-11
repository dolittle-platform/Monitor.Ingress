// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.actuator;

import io.dolittle.moose.pinger.component.PingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Endpoint(id = "hosts")
public class HostsEndpoint {

    private final PingStatus _pingStatus;

    @Autowired
    public HostsEndpoint(PingStatus pingStatus) {
        _pingStatus = pingStatus;
    }

    @ReadOperation
    PingStatus status() {
        log.debug("Providing management interface hosts status");
        return _pingStatus;
    }
}