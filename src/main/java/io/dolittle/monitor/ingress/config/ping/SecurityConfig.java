// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.config.ping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${management.server.port}")
    private int managementPort;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .requestMatchers((HttpServletRequest request) -> managementPort == request.getLocalPort()).permitAll()
            .antMatchers("/dolittle/**").permitAll()
            .antMatchers("/**").denyAll();
    }
}
