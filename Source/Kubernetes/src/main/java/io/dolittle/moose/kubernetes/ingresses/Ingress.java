// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.dolittle.moose.kubernetes.Annotation;
import io.dolittle.moose.kubernetes.Annotations;
import io.dolittle.moose.kubernetes.AnnotationsAlreadyContainAnnotationWithKey;
import io.dolittle.moose.kubernetes.INamespaceResource;
import io.dolittle.moose.kubernetes.Label;
import io.dolittle.moose.kubernetes.Labels;
import io.dolittle.moose.kubernetes.LabelsAlreadyContainLabelWithKey;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Represents a Kubernetes Ingress.
 */
@Value
@Accessors(prefix = { "_" })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ingress implements INamespaceResource {
    private final @NonNull Namespace _namespace;
    private final @NonNull IngressName _name;
    private final @NonNull Labels _labels;
    private final @NonNull Annotations _annotations;
    private final @NonNull Iterable<TlsSecret> _tls;
    private final @NonNull Iterable<HostRule> _rules;

    /**
     * Initializes a new instance of the {@link Ingress} class.
     * @param namespace The {@link Namespace} of the {@link Ingress}.
     * @param name The {@link IngressName} of the {@link Ingress}.
     */
    public Ingress(@NonNull Namespace namespace, @NonNull IngressName name) {
        _namespace = namespace;
        _name = name;
        _labels = Labels.empty();
        _annotations = Annotations.empty();
        _tls = Collections.emptyList();
        _rules = Collections.emptyList();
    }

    /**
     * Creates a copy of the current {@link Ingress} adding the given set of {@link Label}.
     * @param labels The list of {@link Label} to add.
     * @return A new {@link Ingress} with the added set of {@link Label}.
     * @throws LabelsAlreadyContainLabelWithKey If the {@link Ingress} already contains a {@link Label} with the same key as any of the given labels.
     */
    public Ingress withLabels(Label... labels) throws LabelsAlreadyContainLabelWithKey {
        if (labels.length == 0) return this;
        return new Ingress(_namespace, _name, _labels.with(labels), _annotations, _tls, _rules);
    }

    /**
     * Creates a copy of the current {@link Ingress} adding the given set of {@link Annotation}.
     * @param annotations The list of {@link Annotation} to add.
     * @return A new {@link Ingress} with the added set of {@link Annotation}.
     * @throws AnnotationsAlreadyContainAnnotationWithKey If the {@link Ingress} already contains a {@link Annotation} with the same key as any of the given annotations.
     */
    public Ingress withAnnotations(Annotation... annotations) throws AnnotationsAlreadyContainAnnotationWithKey {
        if (annotations.length == 0) return this;
        return new Ingress(_namespace, _name, _labels, _annotations.with(annotations), _tls, _rules);
    }

    /**
     * Creates a copy of the current {@link Ingress} adding the given set of {@link TlsSecret}.
     * @param tlss The list of {@link TlsSecret} to add.
     * @return A new {@link Ingress} with the added set of {@link TlsSecret}.
     */
    public Ingress withTls(TlsSecret... tlss) {
        if (tlss.length == 0) return this;
        var newTls = Arrays.asList(tlss);
        _tls.forEach(newTls::add);
        return new Ingress(_namespace, _name, _labels, _annotations, newTls, _rules);
    }

    /**
     * Creates a copy of the current {@link Ingress} adding the given set of {@link HostRule}.
     * @param tlss The list of {@link HostRule} to add.
     * @return A new {@link Ingress} with the added set of {@link HostRule}.
     */
    public Ingress withHosts(HostRule... rules) {
        if (rules.length == 0) return this;
        var newRules = Arrays.asList(rules);
        _rules.forEach(newRules::add);
        return new Ingress(_namespace, _name, _labels, _annotations, _tls, newRules);
    }

    /**
     * Converts the {@link Ingress} to a Kubernetes {@link ExtensionsV1beta1Ingress}.
     * @return The converted {@link ExtensionsV1beta1Ingress}.
     */
    public ExtensionsV1beta1Ingress toKubernetes() {
        return new ExtensionsV1beta1Ingress()
            .apiVersion("extensions/v1beta1")
            .kind("Ingress")
            .metadata(new V1ObjectMeta()
                .namespace(_namespace.getValue())
                .name(_name.getValue())
                .annotations(_annotations.toKubernetes())
                .labels(_labels.toKubernetes()))
            .spec(new ExtensionsV1beta1IngressSpec()
                .tls(TlsSecret.toKubernetes(_tls))
                .rules(HostRule.toKubernetes(_rules)));
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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TlsSecret {
        private final @NonNull Iterable<Hostname> _hosts;
        private final @NonNull SecretName _name;

        /**
         * Initializes a new instance of the {@link TlsSecret} class.
         * @param name The {@link SecretName} in the same {@link Namespace} as the {@link Ingress} that holds the server certificate to use for HTTPS.
         */
        public TlsSecret(@NonNull SecretName name) {
            _hosts = Collections.emptyList();
            _name = name;
        }

        /**
         * Creates a copy of the current {@link TlsSecret} adding the given set of {@link Hostname}.
         * @param hostnames The list of {@link Hostname} to add.
         * @return A new {@link TlsSecret} with the added set of {@link Hostname}.
         */
        public TlsSecret withHostnames(Hostname... hostnames) {
            var hosts = Arrays.asList(hostnames);
            _hosts.forEach(hosts::add);
            return new TlsSecret(hosts, _name);
        }

        /**
         * Converts the {@link TlsSecret} to a Kubernetes {@link ExtensionsV1beta1IngressTLS}.
         * @return The converted {@link ExtensionsV1beta1IngressTLS}.
         */
        public ExtensionsV1beta1IngressTLS toKubernetes() {
            var tls = new ExtensionsV1beta1IngressTLS();
            tls.setSecretName(_name.getValue());
            _hosts.forEach((hostname) -> tls.addHostsItem(hostname.getValue()));
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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class HostRule {
        private final @NonNull Hostname _host;
        private final @NonNull Iterable<PathRule> _paths;

        /**
         * Initializes a new instance of the {@link HostRule} class.
         * @param host The {@link Hostname} to serve with this {@link Ingress}.
         */
        public HostRule(@NonNull Hostname host) {
            _host = host;
            _paths = Collections.emptyList();
        }

        /**
         * Creates a copy of the current {@link HostRule} adding the given set of {@link PathRule}.
         * @param rules The list of {@link PathRule} to add.
         * @return A new {@link HostRule} with the added set of {@link PathRule}.
         */
        public HostRule withPaths(PathRule... rules) {
            var paths = Arrays.asList(rules);
            _paths.forEach(paths::add);
            return new HostRule(_host, paths);
        }

        /**
         * Converts the {@link HostRule} to a Kubernetes {@link ExtensionsV1beta1IngressRule}.
         * @return The converted {@link ExtensionsV1beta1IngressRule}.
         */
        public ExtensionsV1beta1IngressRule toKubernetes() {
            var http = new ExtensionsV1beta1HTTPIngressRuleValue();
            _paths.forEach((path) -> http.addPathsItem(path.toKubernetes()));
            return new ExtensionsV1beta1IngressRule()
                .host(_host.getValue())
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
        private final Path _path;
        private final ServiceName _name;
        private final Port _port;

        /**
         * Converts the {@link PathRule} to a Kubernetes {@link ExtensionsV1beta1HTTPIngressPath}.
         * @return The converted {@link ExtensionsV1beta1HTTPIngressPath}.
         */
        public ExtensionsV1beta1HTTPIngressPath toKubernetes() {
            return new ExtensionsV1beta1HTTPIngressPath()
                .path(_path.getValue())
                .backend(new ExtensionsV1beta1IngressBackend()
                    .serviceName(_name.getValue())
                    .servicePort(_port));
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
        private final @NonNull String _value;
    }

    /**
     * Represents a Kubernetes Ingress Path.
     */
    @Value
    public static class Path {
        private final @NonNull String _value;
    }
}
