package hello;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
					.antMatchers("/", "/home").permitAll()
					.anyRequest().authenticated()
					.and()
				.formLogin()
				 	.loginPage("/login")
					.permitAll()
					.and()
				.logout()
					.permitAll();
	}

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication()
        		// The following is used as default and therefore not necessary to specify
                // but customization is possible with these methods.
				//.usersByUsernameQuery("select username,password,enabled from users where username = ?")
				//.authoritiesByUsernameQuery("select username,authority from authorities where username = ?")
               	.passwordEncoder(passwordEncoder())
               	.dataSource(dataSource);
    }
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}