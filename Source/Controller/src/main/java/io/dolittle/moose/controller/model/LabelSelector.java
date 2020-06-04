// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.controller.model;


import lombok.Data;

@Data
public class LabelSelector {
    private String labelKey;
    private String labelValue;

    public String getSelector() {
        return labelKey + "=" + labelValue;
    }
}
