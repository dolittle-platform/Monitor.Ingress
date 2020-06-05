// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.errors;

/**
 * Exception that gets thrown when a request to the Kubernetes Api Server fails.
 */
public class KubernetesRequestFailed extends Exception {
    private static final long serialVersionUID = 8108582062213060599L;

    /**
     * Initializes a new instance of the {@link IngressObserver} class.
     * @param action A {@link String} that describes the action that was attemted.
     * @param code The HTTP Status Code that was returned by the Api Server.
     */
    public KubernetesRequestFailed(String action, int code) {
        super(String.format("Request to %s failed, but could not deserialize response from Api Server. Code was %d.", action, code));
    }
    
    /**
     * Initializes a new instance of the {@link IngressObserver} class.
     * @param action A {@link String} that describes the action that was attemted.
     * @param reason A {@link String} that describes the reason for the error.
     * @param message A {@link String} that describes in more detail why the action failed.
     */
    public KubernetesRequestFailed(String action, String reason, String message) {
        super(String.format("Request to %s failed. Reason: %s. Error: %s.", action, reason, message));
    }
}