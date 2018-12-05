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
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        createUser(manager);
        createAdminUser(manager);
        
        return manager;
    }

    private void createUser(InMemoryUserDetailsManager manager) {
    	manager.createUser(User
    			.withUsername("user")
    			.password(encoder().encode("userPass"))
    			.roles("USER").build());
    }

	private void createAdminUser(InMemoryUserDetailsManager manager) {
		manager.createUser(User
          .withUsername("admin")
          // password "admin" already encoded with BCrypt:
          .password("$2a$10$tereo6Y/sLTECpBEFrKEqeUe1XveQVQONKxEpIygO4eTcqOKyuebS")
          .roles("ADMIN").build());
	}
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}