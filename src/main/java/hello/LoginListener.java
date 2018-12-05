package hello;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

@Configuration
public class LoginListener {

	private final static Logger LOG = LoggerFactory.getLogger(LoginListener.class);

	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessListener() {

		return (AuthenticationSuccessEvent event) -> {
			String user = event.getAuthentication().getName();
			String roles = event.getAuthentication().getAuthorities().stream()
					.map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));
			LOG.info("User {} logged in. Roles: {}", user, roles);
		};
	}
}