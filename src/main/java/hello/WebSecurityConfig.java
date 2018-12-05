package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		createAdminUser(manager);

		return manager;
	}

	private void createUser(InMemoryUserDetailsManager manager) {
		manager.createUser(User.withUsername("user")
				// Password "user" encoded with the old Standard encoding (SHA-256)
				.password("$2a$10$opaTlVRWtftWBrIKFtjq2OWm58NyTfNnhtXTzLGqj5FNT9Drl2TTq").roles("USER").build());
	}

	private void createAdminUser(InMemoryUserDetailsManager manager) {
		manager.createUser(User.withUsername("admin")
				// Password "admin" encoded with BCrypt:
				.password("$2a$10$ih2w5IGJQ0PrNJTQuSiuDuHIHaIGNmhTZV42ugf8ehP5y1WauXIcC").roles("ADMIN").build());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}