package hello.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import hello.persistence.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByUsername(String username);
	
}
