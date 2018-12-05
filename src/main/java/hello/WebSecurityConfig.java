package hello;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/home").permitAll().anyRequest().authenticated().and().formLogin()
				.permitAll().and().logout().permitAll();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		createUser(manager);
		createUser2(manager);
		createAdminUser(manager);

		return manager;
	}

	private void createUser(InMemoryUserDetailsManager manager) {
		manager.createUser(User.withUsername("user")
				// Password "user" encoded with the old Standard encoding (SHA-256)
				.password("1d00a70ff28919558daa88e111ebed7b109e16d2cd780d564327b9605ba0a7176398a1dc79b9e711")
				.roles("USER").build());
	}

	private void createUser2(InMemoryUserDetailsManager manager) {
		manager.createUser(User.withUsername("user2")
				// Password "user2" encoded with BCrypt:
				.password("{bcrypt}$2a$10$NFDEndN1lwDkgWhtV7E1QucMLLEqZqCGZ5WiV93nj.VJHew44/Si.").roles("USER")
				.build());
	}

	private void createAdminUser(InMemoryUserDetailsManager manager) {
		manager.createUser(User.withUsername("admin")
				// Password "admin" encoded with SCrypt:
				.password(
						"{scrypt}$e0801$sXh5I14ykub9Ing7Gl/qE2QGcTx5kviow6mzJioGhSP1DBg72GL1DU/+iC1HaPoIzZX2zFXfusy1VAXZ95xkPg==$b0/xUWnD6p7Te92cCGwEsgG3C89R4SJjWYzc3I7mh8Y=")
				.roles("ADMIN").build());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		// Spring Security Version 5.0 introduces the concept of password encoding
		// delegation.
		// Now, we can use different encodings for different passwords.
		// Spring recognizes the algorithm by an identifier in curly braces prefixing
		// the encoded password.
		// If the password hash has no prefix, the delegation process uses a default
		// encoder.
		// Hence, by default, we get the StandardPasswordEncoder.
		// That makes it compatible with the default configuration of previous Spring
		// Security versions.
		// More information on the new password storage in Spring Security 5:
		// https://www.baeldung.com/spring-security-5-password-storage

		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		encoders.put("scrypt", new SCryptPasswordEncoder()); // requires bouncycastle dependency

		// Set the password encoders and use BCrypt as the new default for encoding
		// passwords
		DelegatingPasswordEncoder passworEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);

		// Use old SHA-256 password encoder for passwords without prefix
		PasswordEncoder defaultEncoder = new StandardPasswordEncoder();
		passworEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);

		return passworEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// The following code enables us to migrate passwords with old standard
		// encoding.
		// By default, extracting the password in clear text wouldn’t be possible
		// because
		// Spring Security deletes it as soon as possible.
		// Hence, we need to configure Spring so that it keeps the cleartext version of
		// the password.
		// Additionally, we need to register our encoding delegation:
		auth.eraseCredentials(false).userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());

		// The actual migration is done in PasswordEncodingMigration.
		// More information can be found under:
		// https://www.baeldung.com/spring-security-5-password-storage
	}

}