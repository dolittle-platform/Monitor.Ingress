// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.k8s.service;

import io.dolittle.monitor.ingress.ping.model.PingHost;
import io.dolittle.monitor.ingress.ping.properties.MonitorProperties;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class KubernetesService {

    private final ExtensionsV1beta1Api extV1beta1Api;
    private final MonitorProperties properties;
    private final CoreV1Api coreV1Api;

    @Autowired
    public KubernetesService(MonitorProperties properties, ApiClient apiClient) {
        this.properties = properties;
        Configuration.setDefaultApiClient(apiClient);

        extV1beta1Api = new ExtensionsV1beta1Api();
        coreV1Api = new CoreV1Api();
    }
    public ExtensionsV1beta1Api getExtV1beta1Api() {
        return extV1beta1Api;
    }

    public CoreV1Api getCoreV1Api() {
        return coreV1Api;
    }

    public List<PingHost> getAllHostToPingFromIngress() {
        List<PingHost> hosts = new ArrayList<>();
        try {
            ExtensionsV1beta1IngressList extensionsV1beta1IngressList = extV1beta1Api.listIngressForAllNamespaces(null, null, null, properties.getIngressSelector(), null, null, null, null, null);
            if (extensionsV1beta1IngressList != null) {

                List<ExtensionsV1beta1IngressSpec> specs = extensionsV1beta1IngressList.getItems().stream().map(ExtensionsV1beta1Ingress::getSpec).collect(Collectors.toList());
                specs.forEach(ingressSpec -> {
                    PingHost pingHost = new PingHost();

                    ExtensionsV1beta1IngressRule ingressRule = ingressSpec.getRules().get(0);
                    pingHost.setHost(ingressRule.getHost());
                    pingHost.setPath(ingressRule.getHttp().getPaths().get(0).getPath());
                    log.debug("Ingress host: {}, path: {}", pingHost.getHost(), pingHost.getPath());

                    List<ExtensionsV1beta1IngressTLS> tls = ingressSpec.getTls();
                    pingHost.setTls(tls != null);
                    log.debug("Adding ping URL: {}", pingHost.getURL());
                    hosts.add(pingHost);
                });
            }
        } catch (ApiException e) {
            log.error("Unable to list ingress for all namespaces", e);
        }
        return hosts;
    }

    public ExtensionsV1beta1Ingress createIngress( String namespace, ExtensionsV1beta1Ingress ingress) {
        ExtensionsV1beta1Ingress namespacedIngress=null;

        try {
            namespacedIngress = extV1beta1Api.createNamespacedIngress(namespace, ingress, null, null, null);
        } catch (ApiException e) {
            log.error("Error in creating ingress: {} in namespace: {}, msg: {}", ingress.getMetadata().getName(), namespace, e.getResponseBody());
        }
        return namespacedIngress;
    }

    public V1Service createService(String namespace, V1Service service) {

        V1Service namespacedService= null;
        try {
            namespacedService = coreV1Api.createNamespacedService(namespace, service, null, null, null);
        } catch (ApiException e) {
            log.error("Error in creating service: {} in namespace: {}, msg: {}", service.getMetadata().getName(), namespace, e.getResponseBody());
        }
        return namespacedService;
    }

    public Boolean existsService(String serviceName, String namespace) {
        V1Service service = getService(serviceName, namespace);
        return service != null;
    }

    public V1Namespace getNamespace(String namespace) {
        V1Namespace v1Namespace= null;
        try {
            v1Namespace = coreV1Api.readNamespace(namespace, null, null, null);
        } catch (ApiException e) {
            if (e.getCode() != 404) {
                log.error("Error reading namespace: {}, msg: {}", namespace, e.getResponseBody());
            }
        }
        return v1Namespace;
    }

    public List<ExtensionsV1beta1Ingress> getIngressByLabel(String namespace, String labelSelector) {
        ExtensionsV1beta1IngressList ingressList = new ExtensionsV1beta1IngressList();
        try {
            ingressList = extV1beta1Api.listNamespacedIngress(namespace, null, null, null, null, labelSelector, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error in listing ingress with label: {} in Namespace: {}, msg: {}", labelSelector, namespace, e.getResponseBody());
        }
        return ingressList.getItems();
    }

    public ExtensionsV1beta1Ingress getIngress(String ingressName, String namespace) {
        ExtensionsV1beta1Ingress ingress = null;
        try {
            ingress = extV1beta1Api.readNamespacedIngress(ingressName, namespace, null, null, null);

        } catch (ApiException e) {
            if (e.getCode() != 404) {
                log.error("Error in reading ingress: {} in Namespace: {}, msg: {}", ingressName, namespace, e.getResponseBody());
            }
        }
        return ingress;
    }

    public V1Service getService(String serviceName, String namespace) {
        V1Service service = null;
        try {
            service = coreV1Api.readNamespacedService(serviceName, namespace, null, null, null);
        } catch (ApiException e) {
            if (e.getCode() != 404) {
                log.error("Error in reading service: {} in Namespace: {}, msg: {}", serviceName, namespace, e.getResponseBody());
            }
        }
        return service;
    }

    public V1Service updateService(V1Service service, String namespace ) {
        V1Service namespacedService= null;
        try {
            namespacedService = coreV1Api.replaceNamespacedService(service.getMetadata().getName(), namespace, service, null, null,null);
        } catch (ApiException e) {
            log.error("Error in updating service: {} in namespace: {}, msg: {}", service.getMetadata().getName(), namespace, e.getResponseBody());
        }
        return namespacedService;
    }
}
