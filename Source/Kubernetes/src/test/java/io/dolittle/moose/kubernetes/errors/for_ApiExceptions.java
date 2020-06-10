// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.errors;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.runner.RunWith;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;

@RunWith(JavaSpecRunner.class)
public class for_ApiExceptions {
    private ApiException e;
    private ApiClient apiClient;
    private String action;
    private KubernetesRequestFailed ex;
    
    Establish context = () -> {
        apiClient = new ApiClient();
        action = "create ingress";
    };
    
    class when_creating_exception_without_json {
        Establish context = () -> e = new ApiException(500, "Internal Server Error");

        Because of = () -> ex = ApiExceptions.CreateRequestFailed(e, apiClient, action);

        It should_have_the_correct_message = () -> assertEquals("Request to create ingress failed, but could not deserialize response from Api Server. Code was 500.", ex.getMessage());
    }

    class when_creating_exception_with_invalid_json {
        Establish context = () -> {
            var headers = new HashMap<String, List<String>>();
            var responseBody = "/*";
            e = new ApiException(409, headers, responseBody);
        };

        Because of = () -> ex = ApiExceptions.CreateRequestFailed(e, apiClient, action);

        It should_have_the_correct_message = () -> assertEquals("Request to create ingress failed, but could not deserialize response from Api Server. Code was 409.", ex.getMessage());
    }

    class when_creating_exception_with_valid_json {
        Establish context = () -> {
            var headers = new HashMap<String, List<String>>();
            var responseBody = "{"
                             + "  \"kind\":\"Status\","
                             + "  \"apiVersion\":\"v1\","
                             + "  \"metadata\":{"
                             + "  },"
                             + "  \"status\":\"Failure\","
                             + "  \"message\":\"ingresses.extensions \\\"pinged-ingress\\\" already exists\","
                             + "  \"reason\":\"AlreadyExists\","
                             + "  \"details\":{"
                             + "    \"name\":\"pinged-ingress\","
                             + "    \"group\":\"extensions\","
                             + "    \"kind\":\"ingresses\""
                             + "  },"
                             + "  \"code\":409"
                             + "}";
            e = new ApiException(409, headers, responseBody);
        };

        Because of = () -> ex = ApiExceptions.CreateRequestFailed(e, apiClient, action);

        It should_have_the_correct_message = () -> assertEquals("Request to create ingress failed. Reason: AlreadyExists. Error: ingresses.extensions \"pinged-ingress\" already exists.", ex.getMessage());
    }
}