// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.converters.for_PathConverter;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.runner.RunWith;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import io.dolittle.moose.common.Catch;
import io.dolittle.moose.common.properties.converters.PathConverter;
import io.dolittle.moose.common.properties.converters.PathPropertyIsInvalid;

@RunWith(JavaSpecRunner.class)
public class when_converting_invalid_strings {
    private PathConverter converter;
    private Exception exception;

    Establish context = () -> {
        converter = new PathConverter();
        exception = null;
    };

    class when_converting_a_null {
        Because of = () -> exception = Catch.exception(() -> converter.convert(null));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(PathPropertyIsInvalid.class));
    }

    class when_converting_an_empty_string {
        Because of = () -> exception = Catch.exception(() -> converter.convert(""));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(PathPropertyIsInvalid.class));
    }
    
    class when_converting_a_full_url {
        Because of = () -> exception = Catch.exception(() -> converter.convert("http://dolittle.io/ping"));
    
        It should_throw_an_exception = () -> assertThat(exception, instanceOf(PathPropertyIsInvalid.class));
    }

    class when_converting_a_string_without_the_leading_slash {
        Because of = () -> exception = Catch.exception(() -> converter.convert("ping/path"));
    
        It should_throw_an_exception = () -> assertThat(exception, instanceOf(PathPropertyIsInvalid.class));
    }
}