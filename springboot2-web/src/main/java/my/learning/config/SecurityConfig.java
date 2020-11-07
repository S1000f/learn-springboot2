package my.learning.config;

import my.learning.domain.enums.SocialType;
import my.learning.oauth.ClientResources;
import my.learning.oauth.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static my.learning.domain.enums.SocialType.KAKAO;

@EnableOAuth2Client
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("oauth2ClientContext")
    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 스프링에서 기본적으로 사용하는 문자인코딩 필터
        CharacterEncodingFilter filter = new CharacterEncodingFilter();

        http.authorizeRequests()
            .antMatchers("/", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**").permitAll()
            .anyRequest().authenticated()
                .and()
            .headers().frameOptions().disable()
                .and()
            .exceptionHandling()
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
            .formLogin()
            .successForwardUrl("/board/list")
                .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
                .and()
            .addFilterBefore(filter, CsrfFilter.class)
            .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
            .csrf().disable();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(-100);

        return registrationBean;
    }

    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);

        filter.setRestTemplate(template);
        filter.setTokenServices(new UserTokenService(client, socialType));
        filter.setAuthenticationSuccessHandler(((request, response, authentication) ->
            response.sendRedirect("/" + socialType.getValue() + "/complete")
        ));
        filter.setAuthenticationFailureHandler(((request, response, exception) ->
            response.sendRedirect("/error")
        ));

        return filter;
    }

    private Filter oauth2Filter() {
        CompositeFilter compositeFilter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
        compositeFilter.setFilters(filters);

        return compositeFilter;
    }

    @Bean
    @ConfigurationProperties("kakao")
    public ClientResources kakao() {
        return new ClientResources();
    }

}
