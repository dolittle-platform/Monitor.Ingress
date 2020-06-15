// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties;

import io.dolittle.moose.common.properties.ping.PingIngressProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Context_Configuration_PingIngressProperties.class})
public class When_binding_PingIngressProperties {

    @Autowired
    private PingIngressProperties _pingIngressProperties;

    @Test
    public void when_binding_all_fields_are_set() {
        Assert.assertNotNull("Annotation is null!", _pingIngressProperties.getAnnotation());
        Assert.assertEquals("dolittle.io/testAnnotation", _pingIngressProperties.getAnnotation().getKey());
        Assert.assertEquals("/dolittle/ingress/testPath", _pingIngressProperties.getPath().getValue());
    }
}
