package my.learning.config;

import my.learning.domain.enums.SocialType;
import my.learning.oauth.ClientResources;
import my.learning.oauth.CustomOAuth2Provider;
import my.learning.oauth.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
//import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static my.learning.domain.enums.SocialType.KAKAO;

//@EnableOAuth2Client // 스프링 시큐리티 1.5 버전에만 필요
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Qualifier("oauth2ClientContext")
//    @Autowired
//    private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 스프링에서 기본적으로 사용하는 문자인코딩 필터
        CharacterEncodingFilter filter = new CharacterEncodingFilter();

        http.authorizeRequests()
            .antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**").permitAll()
            .antMatchers("/kakao").hasAuthority(KAKAO.getRoleType())
            .anyRequest().authenticated()
                .and()
            .oauth2Login()
            .defaultSuccessUrl("/loginSuccess")
            .failureUrl("/loginFailure")
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
//            .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
            .csrf().disable();
    }

    /**
     * 본래 스프링 시큐리티 2.0 부터는 자동으로 설정되는 부분들이지만 카카오는 oauth2 규격과 맞지않는
     * 인증 프로퍼티들을 사용하므로, 자동으로 설정되는 다른 부분들(구글, 페이스북 등등)도 다시 불러와서
     * 카카오 설정과 함께 수동으로 다시 재조립 하여 빈으로 등록하는 과정이 필요하다.
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
        OAuth2ClientProperties oAuth2ClientProperties,
        @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId
    ) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
            .map(c -> getRegistration(oAuth2ClientProperties, c))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
            .clientId(kakaoClientId)
            .clientSecret("secret") // 필요없는 값이지만 null 일 경우 예외발생
            .jwkSetUri("uri") // 필요없는 값이지만 null 일 경우 예외발생
            .build()
        );

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
        if (client.equals("google")) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                .clientId(registration.getClientId())
                .clientSecret(registration.getClientSecret())
                .scope("email", "profile")
                .build();
        }

        return null;
    }

    // 이하 스프링 시큐리티 1.5 버전
//    @Bean
//    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(filter);
//        registrationBean.setOrder(-100);
//
//        return registrationBean;
//    }
//
//    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
//        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
//
//        filter.setRestTemplate(template);
//        filter.setTokenServices(new UserTokenService(client, socialType));
//        filter.setAuthenticationSuccessHandler(((request, response, authentication) ->
//            response.sendRedirect("/" + socialType.getValue() + "/complete")
//        ));
//        filter.setAuthenticationFailureHandler(((request, response, exception) ->
//            response.sendRedirect("/error")
//        ));
//
//        return filter;
//    }
//
//    private Filter oauth2Filter() {
//        CompositeFilter compositeFilter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//
//        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
//        compositeFilter.setFilters(filters);
//
//        return compositeFilter;
//    }
//
//    @Bean
//    @ConfigurationProperties("kakao")
//    public ClientResources kakao() {
//        return new ClientResources();
//    }

}
