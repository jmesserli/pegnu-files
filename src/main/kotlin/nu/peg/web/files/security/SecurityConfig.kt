package nu.peg.web.files.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.formLogin()
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth_error")
                .and()
                .logout()
                .logoutUrl("/do-logout")
                .logoutSuccessUrl("/")
    }
}