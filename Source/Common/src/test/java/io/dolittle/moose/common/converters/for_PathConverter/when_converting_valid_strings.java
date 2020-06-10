// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.converters.for_PathConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.runner.RunWith;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import io.dolittle.moose.common.properties.converters.PathConverter;
import io.dolittle.moose.kubernetes.ingresses.Ingress.Path;

@RunWith(JavaSpecRunner.class)
public class when_converting_valid_strings {
    private PathConverter converter;
    private Path converted;

    Establish context = () -> converter = new PathConverter();

    class when_converting_a_simple_path {
        Because of = () -> converted = converter.convert("/dolittle");

        It should_have_the_right_value = () -> assertEquals("/dolittle", converted.getValue());
    }

    class when_converting_a_longer_path {
        Because of = () -> converted = converter.convert("/dolittle/moose/io");

        It should_have_the_right_value = () -> assertEquals("/dolittle/moose/io", converted.getValue());
    }

    class when_converting_a_path_with_escaped_characters {
        Because of = () -> converted = converter.convert("/dolittle%20moose/io");

        It should_have_the_right_value = () -> assertEquals("/dolittle%20moose/io", converted.getValue());
    }
}