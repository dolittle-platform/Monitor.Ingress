// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Hold the results of all the hosts that is pinged.
 */
@Component
@Data
@Slf4j
public class PingStatus {

    private final MeterRegistry _registry;
    private Boolean _monitorStatus = Boolean.TRUE;
    private HashMap<String, Boolean> _hostStatusList = new HashMap<>();

    @Autowired
    public PingStatus(MeterRegistry registry) {
        _registry = registry;
    }

    /**
     * Returns False if a single host in the list has a failed ping
     * @return True -> Success for all hosts, False -> Fail on a single host
     */
    public Boolean getStatus() {
        if (!_monitorStatus) {
            print();
        }
        return _monitorStatus;
    }

    /**
     * Store the result of pinged host
     * @param host Pinged host
     * @param status True -> Success, False -> Fail
     */
    public synchronized void updateHostStatus(String host, Boolean status) {
        log.debug("Updating status for host: {}, status: {}", host, status);
        _registry.counter("uptime.ping", Tags.of("host", host)).increment();
        _hostStatusList.put(host, status);
        updateMonitorStatus();
    }

    /**
     * Clears the internal list that stores the results of pinged hosts
     */
    public synchronized void reset() {
        _hostStatusList.clear();
    }

    private void updateMonitorStatus() {
        if (_hostStatusList.values().stream().anyMatch(aBoolean -> aBoolean.equals(Boolean.FALSE)))
        {
            _monitorStatus = Boolean.FALSE;
        } else {
            _monitorStatus = Boolean.TRUE;
        }
    }

    private void print() {
        log.info("*** START STATUS ***");
        _hostStatusList.forEach( (s, aBoolean) -> log.info("host: {}, status: {}", s, aBoolean));
        log.info("*** END STATUS, size: {} ***", _hostStatusList.size());
    }
}
