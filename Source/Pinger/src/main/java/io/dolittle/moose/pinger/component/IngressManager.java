// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.component;

import io.dolittle.moose.kubernetes.service.KubernetesService;
import io.dolittle.moose.pinger.model.PingHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class IngressManager {

    private final Set<PingHost> hostList = new HashSet<>();
    private final KubernetesService k8Service;

    @Autowired
    public IngressManager(KubernetesService kubernetesService) {
        log.info("Ingress Manager instantiated.");
        this.k8Service = kubernetesService;
        listAllMonitoredIngress();
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    @Async
    public void listAllMonitoredIngress() {
        log.debug("Listing all monitored ingresses in cluster");

        List<PingHost> hostsToPing = new ArrayList<PingHost>(); //k8Service.getAllHostToPingFromIngress();
        updateHostList(hostsToPing);

        log.info("Found {} ingresses in cluster", hostsToPing.size());
    }

    private synchronized void updateHostList(List<PingHost> ingList) {
        hostList.clear();
        hostList.addAll(ingList);
    }

    public synchronized List<PingHost> getHostList() {
        return new ArrayList<>(hostList);
    }
}
