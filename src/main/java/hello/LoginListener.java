package hello;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    
	private final static Logger LOG = LoggerFactory.getLogger(LoginListener.class);

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event)
    {
		String user = event.getAuthentication().getName();
		String roles = event.getAuthentication().getAuthorities().stream()
				.map(authority -> authority.getAuthority()).collect(Collectors.joining(", "));
		LOG.info("User {} logged in. Roles: {}", user, roles);
    }
}