// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.ping.component;

import io.dolittle.monitor.ingress.ping.model.PingHost;
import io.dolittle.monitor.ingress.ping.model.PingStatus;
import io.dolittle.monitor.ingress.ping.service.RequestService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PingManager {

    private final IngressManager ingressManager;
    private final RequestService requestService;
    private final MeterRegistry registry;
    private final PingStatus pingStatus = new PingStatus();

    @Autowired
    public PingManager(RequestService requestService, IngressManager ingressManager, MeterRegistry registry) {
        this.ingressManager = ingressManager;
        this.requestService = requestService;
        this.registry = registry;
        log.info("Ping Manager instantiated.");
    }

    @Scheduled(cron = "* */5 * * * ?")
    public void doPing() {
        List<PingHost> hostList = ingressManager.getHostList();
        pingStatus.clearStatus();
        hostList.forEach(pingHost -> requestService.pingHost(pingHost).thenAcceptAsync(map -> {
            registry.counter("uptime.ping", Tags.of("host", pingHost.getHost())).increment();
            Boolean status = map.get(pingHost.getHost());
            pingStatus.updateStatus(pingHost.getHost(), status);
            log.debug("Done pinging: {}", pingHost.getURL());
        }));
    }

    public PingStatus getStatus() {
        return pingStatus;
    }

    public Boolean getSummary() {
        Boolean status = pingStatus.getSummary();
        if (!status) {
            printStatus();
        }
        return status;
    }

    public void printStatus() {
        pingStatus.print();
    }
}
