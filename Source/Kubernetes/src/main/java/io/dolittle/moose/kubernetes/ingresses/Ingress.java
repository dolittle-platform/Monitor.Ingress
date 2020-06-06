// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.dolittle.moose.kubernetes.Annotations;
import io.dolittle.moose.kubernetes.INamespaceResource;
import io.dolittle.moose.kubernetes.Labels;
import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.secrets.SecretName;
import io.dolittle.moose.kubernetes.services.Port;
import io.dolittle.moose.kubernetes.services.ServiceName;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1HTTPIngressPath;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1HTTPIngressRuleValue;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressBackend;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressRule;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressSpec;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressTLS;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Value;

/**
 * Represents a Kubernetes Ingress.
 */
@Value
public class Ingress implements INamespaceResource{
    Namespace namespace;
    IngressName name;
    Labels labels;
    Annotations annotations;
    Iterable<TlsSecret> tls;
    Iterable<HostRule> rules;

    /**
     * Converts the {@link Ingress} to a Kubernetes {@link ExtensionsV1beta1Ingress}.
     * @return The converted {@link ExtensionsV1beta1Ingress}.
     */
    public ExtensionsV1beta1Ingress toKubernetes() {
        return new ExtensionsV1beta1Ingress()
            .apiVersion("extensions/v1beta1")
            .kind("Ingress")
            .metadata(new V1ObjectMeta()
                .namespace(namespace.getValue())
                .name(name.getValue())
                .annotations(annotations.toKubernetes())
                .labels(labels.toKubernetes()))
            .spec(new ExtensionsV1beta1IngressSpec()
                .tls(TlsSecret.toKubernetes(tls))
                .rules(HostRule.toKubernetes(rules)));
    }

    /**
     * Converts a Kubernetes {@link ExtensionsV1beta1Ingress} to an {@link Ingress}.
     * @param ingress The {@link ExtensionsV1beta1Ingress} to copy values from.
     * @return The converted {@link Ingress}.
     */
    public static Ingress from(ExtensionsV1beta1Ingress ingress) {
        return new Ingress(
            new Namespace(ingress.getMetadata().getNamespace()),
            new IngressName(ingress.getMetadata().getName()),
            Labels.from(ingress.getMetadata()),
            Annotations.from(ingress.getMetadata()),
            TlsSecret.from(ingress.getSpec().getTls()),
            HostRule.from(ingress.getSpec().getRules()));
    }

    /**
     * Represents a Kubernetes Ingress TLS.
     */
    @Value
    public static class TlsSecret {
        Iterable<Hostname> hosts;
        SecretName name;

        /**
         * Converts the {@link TlsSecret} to a Kubernetes {@link ExtensionsV1beta1IngressTLS}.
         * @return The converted {@link ExtensionsV1beta1IngressTLS}.
         */
        public ExtensionsV1beta1IngressTLS toKubernetes() {
            var tls = new ExtensionsV1beta1IngressTLS();
            tls.setSecretName(name.getValue());
            hosts.forEach((hostname) -> tls.addHostsItem(hostname.getValue()));
            return tls;
        }

        /**
         * Converts a set of {@link TlsSecret} to a set of of Kubernetes {@link ExtensionsV1beta1IngressTLS}.
         * @param tlsSecrets The {@link Iterable} of type {@link TlsSecret} to copy values from.
         * @return The converted {@link List} of type {@link ExtensionsV1beta1IngressTLS}.
         */
        public static List<ExtensionsV1beta1IngressTLS> toKubernetes(Iterable<TlsSecret> tlsSecrets) {
            var tlss = new ArrayList<ExtensionsV1beta1IngressTLS>();
            tlsSecrets.forEach((tlsSecret) -> tlss.add(tlsSecret.toKubernetes()));
            return tlss;
        }

        /**
         * Converts a Kubernetes {@link ExtensionsV1beta1IngressTLS} to an {@link TlsSecret}.
         * @param ingress The {@link ExtensionsV1beta1IngressTLS} to copy values from.
         * @return The converted {@link TlsSecret}.
         */
        public static TlsSecret from(ExtensionsV1beta1IngressTLS tls) {
            return new TlsSecret(
                tls.getHosts().stream().map((host) -> new Hostname(host))::iterator,
                new SecretName(tls.getSecretName()));
        }

        /**
         * Converts a set of Kubernetes {@link ExtensionsV1beta1IngressTLS} to set of {@link TlsSecret}.
         * @param tlss The {@link List} of type {@link ExtensionsV1beta1IngressTLS} to copy values from.
         * @return The converted {@link Iterable} of type {@link TlsSecret}.
         */
        public static Iterable<TlsSecret> from(List<ExtensionsV1beta1IngressTLS> tlss) {
            if (tlss == null) return Collections.emptyList();
            return tlss.stream().map(TlsSecret::from)::iterator;
        }
    }

    /**
     * Represents a Kubernetes Ingress Rule.
     */
    @Value
    public static class HostRule {
        Hostname host;
        Iterable<PathRule> paths;

        /**
         * Converts the {@link HostRule} to a Kubernetes {@link ExtensionsV1beta1IngressRule}.
         * @return The converted {@link ExtensionsV1beta1IngressRule}.
         */
        public ExtensionsV1beta1IngressRule toKubernetes() {
            var http = new ExtensionsV1beta1HTTPIngressRuleValue();
            paths.forEach((path) -> http.addPathsItem(path.toKubernetes()));
            return new ExtensionsV1beta1IngressRule()
                .host(host.getValue())
                .http(http);
        }

        /**
         * Converts a set of {@link HostRule} to a set of of Kubernetes {@link ExtensionsV1beta1IngressRule}.
         * @param tlsSecrets The {@link Iterable} of type {@link HostRule} to copy values from.
         * @return The converted {@link List} of type {@link ExtensionsV1beta1IngressRule}.
         */
        public static List<ExtensionsV1beta1IngressRule> toKubernetes(Iterable<HostRule> hostRules) {
            var rules = new ArrayList<ExtensionsV1beta1IngressRule>();
            hostRules.forEach((hostRule) -> rules.add(hostRule.toKubernetes()));
            return rules;
        }

        /**
         * Converts a Kubernetes {@link ExtensionsV1beta1IngressRule} to an {@link HostRule}.
         * @param rule The {@link ExtensionsV1beta1IngressRule} to copy values from.
         * @return The converted {@link HostRule}.
         */
        public static HostRule from(ExtensionsV1beta1IngressRule rule) {
            return new HostRule(
                new Hostname(rule.getHost()),
                rule.getHttp().getPaths().stream().map(PathRule::from)::iterator);
        }

        /**
         * Converts a set of Kubernetes {@link ExtensionsV1beta1IngressRule} to set of {@link HostRule}.
         * @param rules The {@link List} of type {@link ExtensionsV1beta1IngressRule} to copy values from.
         * @return The converted {@link Iterable} of type {@link HostRule}.
         */
        public static Iterable<HostRule> from(List<ExtensionsV1beta1IngressRule> rules) {
            if (rules == null) return Collections.emptyList();
            return rules.stream().map(HostRule::from)::iterator;
        }
    }

    /**
     * Represents a Kubernetes Ingress Path specification.
     */
    @Value
    public static class PathRule {
        Path path;
        ServiceName name;
        Port port;

        /**
         * Converts the {@link PathRule} to a Kubernetes {@link ExtensionsV1beta1HTTPIngressPath}.
         * @return The converted {@link ExtensionsV1beta1HTTPIngressPath}.
         */
        public ExtensionsV1beta1HTTPIngressPath toKubernetes() {
            return new ExtensionsV1beta1HTTPIngressPath()
                .path(path.getValue())
                .backend(new ExtensionsV1beta1IngressBackend()
                    .serviceName(name.getValue())
                    .servicePort(port));
        }

        /**
         * Converts a Kubernetes {@link ExtensionsV1beta1HTTPIngressPath} to an {@link PathRule}.
         * @param rule The {@link ExtensionsV1beta1HTTPIngressPath} to copy values from.
         * @return The converted {@link PathRule}.
         */
        public static PathRule from(ExtensionsV1beta1HTTPIngressPath rule) {
            return new PathRule(
                new Path(rule.getPath()),
                new ServiceName(rule.getBackend().getServiceName()),
                Port.from(rule.getBackend().getServicePort()));
        }
    }

    /**
     * Represents a Kubernetes Ingress Hostname.
     */
    @Value
    public static class Hostname {
        String value;
    }

    /**
     * Represents a Kubernetes Ingress Path.
     */
    @Value
    public static class Path {
        String value;
    }
}
