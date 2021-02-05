package com.orgella.ordersmanagement.infrastructure.configuration

import com.orgella.ordersmanagement.infrastructure.configuration.security.AuthorizationFilter
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurity(private val env: Environment) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .addFilter(AuthorizationFilter(authenticationManager(), env))
        http.headers().frameOptions().disable()
    }
}