
package hello;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * This is a way to migrate old password encoding.
 * 
 * See https://www.baeldung.com/spring-security-5-password-storage
 */
@Configuration
public class PasswordEncodingMigration {

	private final static Logger LOG = LoggerFactory.getLogger(PasswordEncodingMigration.class);

	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessListener(
			final PasswordEncoder encoder) {

		return (AuthenticationSuccessEvent event) -> {

			String user = event.getAuthentication().getName();
			String roles = event.getAuthentication().getAuthorities().stream()
					.map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));
			LOG.info("User {} logged in. Roles: {}", user, roles);

			final Authentication auth = event.getAuthentication();

			if (auth instanceof UsernamePasswordAuthenticationToken && auth.getCredentials() != null) {

				// We retrieved the user password in clear text
				// from the provided authentication details
				final CharSequence clearTextPass = (CharSequence) auth.getCredentials();

				LOG.info("Clear text password for user {} is {}", user, clearTextPass);

				final String newPasswordHash;

				// Created a new password hash
				if (auth.getAuthorities().stream().map(authority -> authority.toString())
						.anyMatch(role -> role.equals("ROLE_ADMIN"))) {
					// using SCrypt for admin users
					newPasswordHash = "{scrypt}" + new SCryptPasswordEncoder().encode(clearTextPass);
				} else {
					// using the new default encoding as configured in
					// WebSecurityConfig#passwordEncoder()
					newPasswordHash = encoder.encode(clearTextPass);
				}

				// (in reality we might probably want to check whether the user credentials have
				// already been migrated e.g. by using a flag in the database)

				// Here we could store the new hash in the user credential database.
				// We just log it here because in the demo we have hardcoded credentials.
				LOG.info("New password hash {} for user {}", newPasswordHash, auth.getName());
			}

			// Removed the clear text password from the authentication token
			((UsernamePasswordAuthenticationToken) auth).eraseCredentials();
		};
	}
}