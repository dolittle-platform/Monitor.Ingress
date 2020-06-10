// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.converters.for_AnnotationConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.runner.RunWith;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import io.dolittle.moose.common.properties.converters.AnnotationConverter;
import io.dolittle.moose.kubernetes.Annotation;

@RunWith(JavaSpecRunner.class)
public class when_converting_valid_strings {
    private AnnotationConverter converter;
    private Annotation converted;

    Establish context = () -> converter = new AnnotationConverter();

    class when_converting_with_name {
        Because of = () -> converted = converter.convert("hawking");

        It should_have_the_name_as_key = () -> assertEquals("hawking", converted.getKey());
        It should_have_an_empty_value = () -> assertEquals("", converted.getValue());
    }

    class when_converting_with_prefix_and_name {
        Because of = () -> converted = converter.convert("stephen.io/hawking");

        It should_have_the_name_as_key = () -> assertEquals("stephen.io/hawking", converted.getKey());
        It should_have_an_empty_value = () -> assertEquals("", converted.getValue());
    }

    class when_converting_with_name_and_value {
        Because of = () -> converted = converter.convert("hawking:radiation");

        It should_have_the_name_as_key = () -> assertEquals("hawking", converted.getKey());
        It should_have_an_empty_value = () -> assertEquals("radiation", converted.getValue());
    }

    class when_converting_with_everything {
        Because of = () -> converted = converter.convert("stephen.io/hawking:radiation");

        It should_have_the_name_as_key = () -> assertEquals("stephen.io/hawking", converted.getKey());
        It should_have_an_empty_value = () -> assertEquals("radiation", converted.getValue());
    }
}