// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Data
@Slf4j
public class PingStatus {

    private Boolean summary = Boolean.TRUE;
    private HashMap<String, Boolean> pingList = new HashMap<>();

    public synchronized void updateStatus(String host, Boolean status) {
        log.debug("Updating status for host: {}, status: {}", host, status);
        pingList.put(host, status);
        updateInternalStatus();
    }

    private void updateInternalStatus() {
        if (pingList.values().stream().anyMatch(aBoolean -> aBoolean.equals(Boolean.FALSE)))
        {
            summary = Boolean.FALSE;
        } else {
            summary = Boolean.TRUE;
        }
    }

    public synchronized void clearStatus() {
        pingList.clear();
    }

    public void print() {
        log.info("*** START STATUS ***");
        pingList.forEach( (s, aBoolean) -> {
            log.info("host: {}, status: {}", s, aBoolean);
        });
        log.info("*** END STATUS, size: {} ***", pingList.size());
    }
}
