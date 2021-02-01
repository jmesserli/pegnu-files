package nu.peg.web.files.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
            .antMatchers("/createFolder").hasAuthority(SecurityUtil.Role.MAKE_DIRECTORY)
            .antMatchers("/uploadFile").hasAuthority(SecurityUtil.Role.UPLOAD)
            .antMatchers("/deleteFile").hasAuthority(SecurityUtil.Role.DELETE)
            .anyRequest().permitAll()
            .and()
            .oauth2Login()
    }
}
