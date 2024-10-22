/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Role;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.RoleRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class AdminUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		
		return user;
	}
	
	public User deleteUserRole(Long id, String role) {
		
	    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        boolean roleRemoved = user.getRoles().removeIf(r -> r.getName().equalsIgnoreCase(role));

        if (!roleRemoved) {
            throw new RuntimeException("Role " + role + " not found for user " + id);
        }

        return userRepository.save(user);
    }
			

	public User updateUserRole(Long id, Role role) {
		
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		
		user.getRoles().clear();
		user.addRole(role);
		
		return userRepository.save(user);
			
		
	}
	
	public User findUser(Long id) throws UserNotFoundException {
		
		User user = userRepository.findById(id)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+id+" not found"));
		
		return user;
	}
}
