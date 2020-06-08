// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.kubernetes.ingresses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.Namespace;
import io.dolittle.moose.kubernetes.errors.ApiExceptions;
import io.dolittle.moose.kubernetes.errors.KubernetesRequestFailed;
import io.dolittle.moose.kubernetes.informers.ICanProvideInformers;
import io.kubernetes.client.informer.cache.Indexer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;

/**
 * An implementation of {@link IIngressApiClient}.
 */
@Component
public class IngressApiClient implements IIngressApiClient {
    private final ApiClient _apiClient;
    private final ExtensionsV1beta1Api _extensionsApi;
    private final Indexer<ExtensionsV1beta1Ingress> _indexer;

    /**
     * Initializes a new instance of the {@link IngressApiClient} class.
     * @param apiClient The {@link ApiClient} to use to interact with the Kubernetes Cluster.
     * @param informers The {@link ICanProvideInformers} to use to get Ingresses from a local index.
     */
    @Autowired
    public IngressApiClient(ApiClient apiClient, ICanProvideInformers informers) {
        _apiClient = apiClient;
        _extensionsApi = new ExtensionsV1beta1Api(apiClient);
        _indexer = informers.getIngressInformer().getIndexer();
    }

    @Override
    public boolean Exists(Namespace namespace, IngressName name) {
        return _indexer.getByKey(getKey(namespace, name)) != null;
    }

    @Override
    public boolean Exists(Ingress ingress) {
        return Exists(ingress.getNamespace(), ingress.getName());
    }

    @Override
    public Ingress Get(Namespace namespace, IngressName name) throws IngressDoesNotExist {
        var ingress = _indexer.getByKey(getKey(namespace, name));
        if (ingress == null) {
            throw new IngressDoesNotExist(namespace, name);
        }
        return Ingress.from(ingress);
    }

    @Override
    public Ingress Get(Ingress ingress) throws IngressDoesNotExist {
        return Get(ingress.getNamespace(), ingress.getName());
    }

    @Override
    public void Create(Ingress ingress) throws IngressAlreadyExists, KubernetesRequestFailed {
        try {
            _extensionsApi.createNamespacedIngress(ingress.getNamespace().getValue(), ingress.toKubernetes(), null, null, null);
        } catch (ApiException e) {
            if (ApiExceptions.IsAlreadyExists(e)) {
                throw new IngressAlreadyExists(ingress.getNamespace(), ingress.getName());
            }
            throw ApiExceptions.CreateRequestFailed(e, _apiClient, "create ingress");
        }
    }

    private String getKey(Namespace namespace, IngressName name) {
        return String.format("%s/%s", namespace.getValue(), name.getValue());
    }
}