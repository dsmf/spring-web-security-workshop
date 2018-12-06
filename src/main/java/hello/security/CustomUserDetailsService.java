package hello.security;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hello.persistence.dao.UserRepository;
import hello.persistence.model.User;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

	protected final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		LOG.info("Loading user by username \"{}\".", username);

		Optional<User> optUser = userRepo.findByUsername(username);

		if (!optUser.isPresent()) {
			throw new UsernameNotFoundException(username + " not found");
		}

		User user = optUser.get();

		UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
		builder.password(user.getPassword());
		builder.authorities(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList())
				.toArray(new String[user.getRoles().size()]));

		return builder.build();
	}

}