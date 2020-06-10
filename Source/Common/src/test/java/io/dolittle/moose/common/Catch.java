// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.common;

/**
 * A simple helper class to allow writing specifications that expects exceptions to be thrown.
 */
public class Catch {
    /**
     * Runs the given expression and returns any thrown exception.
     * @param expression The {@link Runnable} expression to run.
     * @return An {@link Exception} if one was thrown during execution of the expression, otherwise {@literal null}.
     */
    public static Exception exception(Runnable expression) {
        try {
            expression.run();
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }
}