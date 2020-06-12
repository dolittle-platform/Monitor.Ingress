// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.component;

import io.dolittle.moose.common.properties.ping.PingIngressProperties;
import io.dolittle.moose.kubernetes.ingresses.ICanObserveIngresses;
import io.dolittle.moose.pinger.model.PingHost;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ingress manager is responsible to aggregate a list of {@link PingHost} that will be used by {@link PingManager}.
 * It utilizes the observer pattern by subscribing to Ingress events. Events are filtered by {@link io.dolittle.moose.kubernetes.Annotation}
 * @see PingManager
 */
@Component
@Slf4j
public class IngressManager {

    private final Set<PingHost> _hostList = new HashSet<>();
    private final ICanObserveIngresses _ingressObserver;
    private final PingIngressProperties _pingIngressProperties;

    @Autowired
    public IngressManager(ICanObserveIngresses IngressObserver, PingIngressProperties pingIngressProperties) {
        _ingressObserver = IngressObserver;
        _pingIngressProperties = pingIngressProperties;
        log.info("Ingress Manager instantiated.");
        aggregateHosts();
    }

    /**
     * Returns a List of hosts
     * @return {@link List} of {@link PingHost}
     */
    public synchronized List<PingHost> getHostsList() {
        return new ArrayList<>(_hostList);
    }

    private void aggregateHosts() {
        log.debug("Aggregating hosts to be monitored");
        var observable = _ingressObserver.observeAllIngressesWithAnnotations(_pingIngressProperties.getAnnotation());
        Disposable subscribe = observable.subscribe(ingresses -> {
            //Run through each item and aggregate a List of PingHost
            List<PingHost> pingHosts = new ArrayList<>();
            ingresses.forEach(ingress -> {
                var tls = ingress.getTls();
                Set<String> tlsList = new HashSet<>();
                tls.forEach(tlsSecret -> {
                    var tlsHost = tlsSecret.getHosts();
                    tlsHost.forEach(hostname -> tlsList.add(hostname.getValue()));
                });

                var rules = ingress.getRules();
                rules.forEach(hostRule -> {
                    var pingHost = new PingHost();
                    var host = hostRule.getHost().getValue();
                    pingHost.setHost(host);
                    pingHost.setPath(hostRule.getPaths().iterator().next().getPath().getValue());
                    pingHost.setTls(tlsList.contains(host));

                    pingHosts.add(pingHost);
                });
            });
            updateHostList(pingHosts);
            log.info("Found {} ingresses in cluster", pingHosts.size());
        });

    }

    private synchronized void updateHostList(List<PingHost> ingList) {
        _hostList.clear();
        _hostList.addAll(ingList);
    }


}
