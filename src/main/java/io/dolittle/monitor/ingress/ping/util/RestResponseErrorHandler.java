// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.ping.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Slf4j
public class RestResponseErrorHandler implements ResponseErrorHandler {
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return errorHandler.hasError(clientHttpResponse);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        log.error("Response: {} {}", clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusText());
        if(clientHttpResponse.getStatusCode().is4xxClientError()) return;

        throw new RestClientException(clientHttpResponse.getStatusText());
    }
}
