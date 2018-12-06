package hello.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import hello.persistence.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

	Optional<Role> findByName(String name);
	
}
