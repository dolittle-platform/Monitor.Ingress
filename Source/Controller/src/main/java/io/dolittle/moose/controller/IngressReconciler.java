// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.controller;

import io.dolittle.moose.controller.service.PropertyService;
import io.dolittle.moose.kubernetes.service.KubernetesService;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IngressReconciler implements Reconciler {

    private final KubernetesService k8s;
    private final PropertyService property;

    @Autowired
    public IngressReconciler(KubernetesService kubernetesService, PropertyService propertyService) {
        property = propertyService;
        this.k8s = kubernetesService;
        log.debug("Created Ingress reconciler");
    }

    @Override
    public Result reconcile(Request request) {

        String reqNamespace = request.getNamespace();
        String reqName = request.getName();

        log.debug("START - Reconciling ingress: {} in namespace: {} ", reqName, reqNamespace);

        String labelSelector = property.controller().getSelector();
        List<ExtensionsV1beta1Ingress> pingIngressList = k8s.getIngressByLabel(reqNamespace, labelSelector);

        if (pingIngressList.isEmpty()) {
            log.debug("No ingress with label: {} in namespace: {}", labelSelector, reqNamespace);
            //Nothing to do
        } else {
            // 1 or many ingresses left in namespace

            // 1 service of type External must be in the namespace
            if (!k8s.existsService(property.service().getName(), reqNamespace)) {
                //create service
                create_Service_Uptime_Service(reqNamespace);
            }

            // For each ingress with label uptime:ping there must be an ingress with label uptime:External in NS
            pingIngressList.forEach(this::reconcile_Ingress_Uptime_External);

        }

        log.debug("DONE - Reconciling ingress: {}, in namespace: {}", reqNamespace, reqName);
        return new Result(false);
    }

    private void reconcile_Ingress_Uptime_External(ExtensionsV1beta1Ingress parentIngress) {
        String ingressName = parentIngress.getMetadata().getName() + property.ingress().getNameSuffix();
        String namespace = parentIngress.getMetadata().getNamespace();

        ExtensionsV1beta1Ingress ingress = k8s.getIngress(ingressName, namespace);
        if (ingress == null) {
            //Missing an ingress with label:external for existing ingress with label uptime:Ping
            ingress = create_Ingress_Uptime_External(parentIngress);
            if (ingress != null) {
                log.info("Created ingress: {} in namespace: {}", ingress.getMetadata().getName(), namespace);
                addOwnerReferenceToService(ingress, namespace);
            }
        }
    }

    private void create_Service_Uptime_Service(String namespace){
        //Create a service of type External in the NS that points to an external svc

        //Fetching namespace to use labels from it to populate labels in service
        V1Namespace v1Namespace = k8s.getNamespace(namespace);
        if (v1Namespace == null) {
            log.error("Unable to attain labels from namespace: {}", namespace);
            log.error("Aborting creation of service");
            return;
        }

        Map<String, String> nsLabels = v1Namespace.getMetadata().getLabels();
        nsLabels.put(property.service().getLabelKey(), property.service().getLabelValue());

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta()
                .namespace(namespace)
                .labels(nsLabels)
                .name(property.service().getName());

        V1ServicePort v1ServicePort = new V1ServicePort().port(property.service().getPort());

        V1ServiceSpec v1ServiceSpec = new V1ServiceSpec()
                .type(property.service().getType())
                .externalName(property.service().getExternalName())
                .ports(Collections.singletonList(v1ServicePort));

        V1Service service = new V1Service().kind(property.service().getKind()).metadata(v1ObjectMeta).spec(v1ServiceSpec);
        V1Service v1Service = k8s.createService(namespace, service);

        if (v1Service != null) {
            log.info("Created service: {} in namespace: {}", v1Service.getMetadata().getName(), namespace);
        }

    }

    private ExtensionsV1beta1Ingress create_Ingress_Uptime_External(ExtensionsV1beta1Ingress parentIngress){
        //Create an ingress in the NS that points to svc of type external in NS

        String namespace = parentIngress.getMetadata().getNamespace();
        String ingressName = parentIngress.getMetadata().getName() + property.ingress().getNameSuffix();

        Map<String, String> labels = parentIngress.getMetadata().getLabels();
        labels.put(property.ingress().getLabelKey(), property.ingress().getLabelValue());

        //Set ownerReference for Garbage Collection when parent is deleted
        V1OwnerReference v1OwnerReference = getV1OwnerReference(parentIngress, true);

        //Set Metadata
        V1ObjectMeta objectMeta = new V1ObjectMeta().name(ingressName)
                .namespace(namespace)
                .labels(labels)
                .annotations(parentIngress.getMetadata().getAnnotations())
                .addOwnerReferencesItem(v1OwnerReference);

        List<ExtensionsV1beta1IngressRule> parentRules = parentIngress.getSpec().getRules();
        List<ExtensionsV1beta1IngressRule> childRules = new ArrayList<>();

        if (parentRules != null && !parentRules.isEmpty()) {
            //Add rules for every host in parent
            parentRules.forEach(rule ->{
                String host = rule.getHost();

                ExtensionsV1beta1HTTPIngressPath ingressPath = new ExtensionsV1beta1HTTPIngressPath()
                        .path(property.ingress().getPath())
                        .backend(new ExtensionsV1beta1IngressBackend()
                        .serviceName(property.service().getName())
                        .servicePort(new IntOrString(property.service().getPort())));

                ExtensionsV1beta1IngressRule ingressRule = new ExtensionsV1beta1IngressRule()
                        .host(host)
                        .http(new ExtensionsV1beta1HTTPIngressRuleValue()
                        .paths(Collections.singletonList(ingressPath)));

                childRules.add(ingressRule);
            });
        }

        ExtensionsV1beta1IngressSpec ingressSpec = new ExtensionsV1beta1IngressSpec()
                .tls(parentIngress.getSpec().getTls())
                .rules(childRules);

        ExtensionsV1beta1Ingress ingress = new ExtensionsV1beta1Ingress().apiVersion(property.ingress().getApiVersion()).kind(property.ingress().getKind()).metadata(objectMeta).spec(ingressSpec);

        return k8s.createIngress(namespace, ingress);
    }

    private V1OwnerReference getV1OwnerReference(ExtensionsV1beta1Ingress ingress, Boolean controller) {
        return new V1OwnerReference()
                .controller(controller)
                .kind(property.ingress().getKind())
                .apiVersion(property.ingress().getApiVersion())
                .name(ingress.getMetadata().getName())
                .uid(ingress.getMetadata().getUid());
    }

    private void addOwnerReferenceToService(ExtensionsV1beta1Ingress ingress, String namespace) {
        V1Service service = k8s.getService(property.service().getName(), namespace);
        if (service != null) {
            log.debug("Adding OwnerReference: {} to service: {}", ingress.getMetadata().getName(), service.getMetadata().getName());

            V1OwnerReference v1OwnerReference = getV1OwnerReference(ingress, false);
            service.getMetadata().addOwnerReferencesItem(v1OwnerReference);

            if (service.getMetadata().getOwnerReferences().size() > 1) {
                //Removing duplicates entries
                List<V1OwnerReference> uniqueList = service.getMetadata().getOwnerReferences()
                        .stream()
                        .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(V1OwnerReference::getUid))), ArrayList::new));
                service.getMetadata().setOwnerReferences(uniqueList);
            }

            V1Service updateService = k8s.updateService(service, namespace);
            if (updateService != null) {
                log.info("Updated service: {}, in namespace: {}", updateService.getMetadata().getName(), namespace);
            }
        }
    }

}
