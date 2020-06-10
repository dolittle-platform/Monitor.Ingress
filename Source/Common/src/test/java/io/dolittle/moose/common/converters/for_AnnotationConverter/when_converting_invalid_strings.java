// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common.converters.for_AnnotationConverter;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.runner.RunWith;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import io.dolittle.moose.common.Catch;
import io.dolittle.moose.common.properties.converters.AnnotationConverter;
import io.dolittle.moose.common.properties.converters.AnnotationPropertyIsInvalid;

@RunWith(JavaSpecRunner.class)
public class when_converting_invalid_strings {
    private AnnotationConverter converter;
    private Exception exception;

    Establish context = () -> {
        converter = new AnnotationConverter();
        exception = null;
    };

    class when_converting_null {
        Because of = () -> exception = Catch.exception(() -> converter.convert(null));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_empty_string {
        Because of = () -> exception = Catch.exception(() -> converter.convert(""));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_with_k8sio_prefix {
        Because of = () -> exception = Catch.exception(() -> converter.convert("k8s.io/annotation"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_with_kubernetesio_prefix {
        Because of = () -> exception = Catch.exception(() -> converter.convert("kubernetes.io/annotation:value"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_without_name {
        Because of = () -> exception = Catch.exception(() -> converter.convert(":value"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_with_prefix_but_without_name {
        Because of = () -> exception = Catch.exception(() -> converter.convert("dolittle.io/:value"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_with_a_really_long_prefix {
        Because of = () -> exception = Catch.exception(() -> converter.convert("lorem.ipsum.dolor.sit.amet.consectetur.adipiscing.elit.pellentesque.nec.ipsum.at.eros.pellentesque.tincidunt.mauris.vel.volutpat.sapien.nec.pharetra.est.integer.porttitor.eu.quam.non.pharetra.integer.ex.ligula.eleifend.vitae.odio.quis.ornare.vehicula.dolor/annotation"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }

    class when_converting_with_a_really_long_name {
        Because of = () -> exception = Catch.exception(() -> converter.convert("TheQuickBrownFoxJumpsOverTheLazyDogThenTripsAndFallsOverInAFunnyWay:value"));

        It should_throw_an_exception = () -> assertThat(exception, instanceOf(AnnotationPropertyIsInvalid.class));
    }
}