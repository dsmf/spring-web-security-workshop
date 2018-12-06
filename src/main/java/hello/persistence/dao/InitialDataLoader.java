package hello.persistence.dao;

import java.util.Arrays;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import hello.persistence.model.Role;
import hello.persistence.model.User;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (alreadySetup) {
			return;
		}
		
		Role userRole = createRoleIfNotFound("ROLE_USER");
		Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
		
		User user = new User("user", "$2a$10$em2Xce5MKP8jl6R4UGDtDeZHKx509dpvVEsLwtUORY/MOyVVjdBBO", true, Arrays.asList(userRole));
		userRepository.save(user);
		
		User admin = new User("admin", "$2a$10$eAUK7iQDFpA4g/sTiTZjEes0xO/yTsuJy.A17NhKIryBmauMEzTue", true, Arrays.asList(adminRole));
		userRepository.save(admin);
		
		alreadySetup = true;

	}

	@Transactional
	private Role createRoleIfNotFound(String name) {

		Optional<Role> role = roleRepository.findByName(name);
		if (!role.isPresent()) {
			Role theRole = new Role(name);
			role = Optional.of(theRole);
			roleRepository.save(theRole);
		}
		return role.get();
	}

}