// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties.converters;

import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.Annotation;

/**
 * A Spring {@link Converter} that converts {@link String} property values to {@link Annotation}.
 */
@Component
@ConfigurationPropertiesBinding
public class AnnotationConverter implements Converter<String, Annotation> {
    private static Pattern pattern = Pattern.compile("^([a-zA-Z0-9\\-\\.]+\\/)?([a-zA-Z0-9]+)(:(.*))?$");

    @Override
    public Annotation convert(String source) {
        if (source == null) {
            throw new AnnotationPropertyIsInvalid("value is null", source);
        }

        var matcher = pattern.matcher(source);
        if (!matcher.matches()) {
            throw new AnnotationPropertyIsInvalid("value is not valid", source);
        }

        var prefix = matcher.group(1);
        if (prefix == null) {
            prefix = "";
        } else {
            if (prefix.length() > 254) {
                throw new AnnotationPropertyIsInvalid("prefix is too long", source);
            }
            if (prefix.equals("k8s.io/") || prefix.equals("kubernetes.io/")) {
                throw new AnnotationPropertyIsInvalid("prefix is reserved for Kubernetes core components", source);
            }
        }
        
        var name = matcher.group(2);
        if (name == null || name.length() < 1) {
            throw new AnnotationPropertyIsInvalid("name is required", source);
        }
        if (name.length() > 63) {
            throw new AnnotationPropertyIsInvalid("name is too long", source);
        }

        var value = matcher.group(4);
        if (value == null) {
            value = "";
        }

        return new Annotation(prefix+name, value);
    }
    
}