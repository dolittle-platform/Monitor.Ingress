// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.actuator;

import io.dolittle.moose.pinger.component.PingManager;
import io.dolittle.moose.pinger.model.PingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Endpoint(id = "hosts")
public class HostsEndpoint {

    private final PingManager pingManager;

    @Autowired
    public HostsEndpoint(PingManager pingManager) {
        this.pingManager = pingManager;
    }

    @ReadOperation
    PingStatus status() {
        log.debug("Providing management interface hosts status");
        return pingManager.getStatus();
    }
}