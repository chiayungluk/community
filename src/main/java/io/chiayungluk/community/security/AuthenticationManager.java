package io.chiayungluk.community.security;

import io.chiayungluk.community.AppConfig;
import io.chiayungluk.community.JwtUtil;
import io.chiayungluk.community.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

	private final String jwtSecret;

	public AuthenticationManager(AppConfig appConfig) {
		this.jwtSecret = appConfig.getJwtSecret();
	}
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		try {
			String authToken = authentication.getCredentials().toString();
			UserDto userDto = JwtUtil.verifyJWTToken(authToken, jwtSecret);
			UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getId(), null);
			SecurityContextHolder.getContext().setAuthentication(auth);
			return Mono.just(auth);
		} catch (Exception e) {
			log.warn("Verify JWT token failed", e);
			return Mono.empty();
		}
	}
}