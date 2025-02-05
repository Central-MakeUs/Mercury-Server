package com.cmc.mercury.global.oauth.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(hidden = true)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : @userRepository.findById(T(Long).valueOf(#this.username)).orElse(null)")
public @interface AuthUser {
}
