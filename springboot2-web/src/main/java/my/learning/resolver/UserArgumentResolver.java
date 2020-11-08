package my.learning.resolver;

import my.learning.annotation.SocialUser;
import my.learning.domain.User;
import my.learning.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static my.learning.domain.enums.SocialType.KAKAO;

/**
 * 요청 메소드에 인자를 전달해주기 전, 해당 인자를 새롭게 생성해주는 역할을 하는 리졸버이다.
 * HandlerMethodArgumentResolver 를 직접 구현하여 커스텀 리졸버를 만들고,
 * 그 리졸버를 사용할 수 있도록 스프링 필터에 등록해야한다.
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SocialUser.class) != null &&
            parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpSession session =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");

        return getUser(user, session);
    }

    private User getUser(User user, HttpSession session) {
        if (user == null) {
            try {
                OAuth2AuthenticationToken authentication =
                    (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                Map<String, Object> map = authentication.getPrincipal().getAttributes();
                User convertUser = convertUser(authentication.getAuthorizedClientRegistrationId(), map);

                user = userRepository.findByEmail(convertUser.getEmail());
                if (user == null) {
                    user = userRepository.save(convertUser);
                }

                setRoleNotSame(user, authentication, map);
                session.setAttribute("user", user);
            } catch (ClassCastException e) {
                return user;
            }
        }

        return user;
    }

    private User convertUser(String authority, Map<String, Object> map) {
        if (KAKAO.getValue().equals(authority)) {
            return getKakaoUser(map);
        }

        return null;
    }

    private User getKakaoUser(Map<String, Object> map) {
        HashMap<String, String> propertyMap = (HashMap<String, String>) map.get("properties");
        return User.builder()
            .name(propertyMap.get("nickName"))
            .email(String.valueOf(map.get("kaccount_email")))
            .principal(String.valueOf(map.get("id")))
            .socialType(KAKAO)
            .createdDate(LocalDateTime.now())
            .build();
    }

    private void setRoleNotSame(User user, OAuth2AuthenticationToken authentication, Map<String, Object> map) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                map,
                "N/A",
                AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())
            ));
        }
    }

}
