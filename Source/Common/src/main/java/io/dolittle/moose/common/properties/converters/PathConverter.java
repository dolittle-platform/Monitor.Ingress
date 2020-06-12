// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.properties.converters;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.dolittle.moose.kubernetes.ingresses.Ingress.Path;

/**
 * A Spring {@link Converter} that converts {@link String} property values to
 * {@link io.dolittle.moose.kubernetes.Annotation}.
 */
@Component
@ConfigurationPropertiesBinding
public class PathConverter implements Converter<String, Path> {
    private static Predicate<String> validator = Pattern.compile("^(?:\\/(?:[a-zA-Z0-9$\\-_\\.\\+]|%[a-fA-F0-9]{2})*)+$").asPredicate();

    @Override
    public Path convert(String source) {
        if (source == null) {
            throw new PathPropertyIsInvalid("value is null", source);
        }

        if (!validator.test(source)) {
            throw new PathPropertyIsInvalid("value is not a valid path", source);
        }

        return new Path(source);
    }    
}