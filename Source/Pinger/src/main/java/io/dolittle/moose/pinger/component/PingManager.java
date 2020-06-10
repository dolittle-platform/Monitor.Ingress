// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.component;

import io.dolittle.moose.pinger.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Ping manager is responsible for periodically pinging a list of hosts
 */
@Component
@Slf4j
public class PingManager {

    private final IngressManager _ingressManager;
    private final RequestService _requestService;
    private final PingStatus _pingStatus;

    @Autowired
    public PingManager(RequestService requestService, IngressManager ingressManager, PingStatus pingStatus) {
        this._ingressManager = ingressManager;
        this._requestService = requestService;
        _pingStatus = pingStatus;
        log.info("Ping Manager instantiated.");
    }

    @Scheduled(cron = "* */5 * * * ?")
    public void doPing() {
        var hostList = _ingressManager.getHostsList();
        _pingStatus.reset();
        hostList.forEach(pingHost -> _requestService.pingHost(pingHost).thenAcceptAsync(map -> {
            var host = pingHost.getHost();
            var status = map.get(host);
            _pingStatus.updateHostStatus(host, status);
            log.debug("Done pinging: {}", pingHost.getURL());
        }));
    }

}
