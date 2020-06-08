// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.errors;

import com.google.gson.reflect.TypeToken;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Status;

/**
 * Helper methods for checking well known Kubernetes Api Server errors, and parsing others.
 */
public class ApiExceptions {
    // public static final int BAD_REQUEST = 400;
    // public static final int UNAUTHORIZED = 401;
    // public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    // public static final int METHOD_NOT_ALLOWED = 405;
    // public static final int NOT_ACCEPTABLE = 406;
    public static final int ALREADY_EXISTS = 409;
    // public static final int REQUEST_ENTITY_TOO_LARGE = 413;
    // public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    // public static final int CONFLICT = 409;
    // public static final int GONE = 410;
    // public static final int EXPIRED = 410;
    // public static final int INVALID = 422;
    // public static final int TOO_MANY_REQUESTS = 429;
    // public static final int UNKNOWN = 500;
    // public static final int SERVER_TIMEOUT = 500;
    // public static final int INTERNAL_ERROR = 500;
    // public static final int SERVICE_UNAVAILABLE = 503;
    // public static final int TIMEOUT = 504;

    /**
     * Checks whether or not the given exception was caused by a request to get a Resource that does not exist.
     * @param e The {@link ApiException} that was thrown by the {@link ApiClient}.
     * @return {@literal true} if the {@link ApiException} was cased by a 'not found' error, {@literal false} if not.
     */
    public static boolean IsDoesNotExist(ApiException e) {
        return e.getCode() == NOT_FOUND;
    }

    /**
     * Checks whether or not the given exception was caused by a request to create a Resource that already exists.
     * @param e The {@link ApiException} that was thrown by the {@link ApiClient}.
     * @return {@literal true} if the {@link ApiException} was cased by a 'already exists' error, {@literal false} if not.
     */
    public static boolean IsAlreadyExists(ApiException e) {
        return e.getCode() == ALREADY_EXISTS;
    }

    /**
     * Parses the returned status from the Kubernetes Api Server (if any) and returns a more detailed exception.
     * @param e The {@link ApiException} that was thrown by the {@link ApiClient}.
     * @param apiClient The {@link ApiClient} that will be used to deserialize the returned {@link V1Status}.
     * @param action A {@link String} that describes the action that was attemted.
     * @return A {@link KubernetesRequestFailed} exception that contains the details of the error that occured.
     */
    public static KubernetesRequestFailed CreateRequestFailed(ApiException e, ApiClient apiClient, String action) {
        if (e.getResponseBody() == null) {
            return new KubernetesRequestFailed(action, e.getCode());
        }
        try {
            var status = (V1Status)apiClient.getJSON().deserialize(e.getResponseBody(), new TypeToken<V1Status>(){}.getType());
            return new KubernetesRequestFailed(action, status.getReason(), status.getMessage());
        } catch (Exception ex) {
            return new KubernetesRequestFailed(action, e.getCode());
        }
    }
}