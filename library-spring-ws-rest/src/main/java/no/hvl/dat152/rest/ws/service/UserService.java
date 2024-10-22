/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderService orderService; 

	
	public List<User> findAllUsers(){
		
		List<User> allUsers = (List<User>) userRepository.findAll();
		
		return allUsers;
	}
	
	public User findUser(Long userid) throws UserNotFoundException {
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		return user;
	}
	
	
	public User saveUser(User user) {
		
		user = userRepository.save(user);
		return user;
		
	}
	
	public void deleteUser(Long id) throws UserNotFoundException {
		
		User user = findUser(id);
		userRepository.delete(user);
		
	}
	
	public User updateUser(User user, Long id) throws UserNotFoundException { 
		
		User usertoUpdate = findUser(id);
		long id1 = usertoUpdate.getUserid(); 
		user.setUserid(id);
		userRepository.save(user);
		
		return user;
	}
	
	public Set<Order> getUserOrders(Long userId) throws UserNotFoundException {
		
		User user = findUser(userId);
		return user.getOrders();
		
	}
	
	// TODO public Order getUserOrder(Long userid, Long oid)
	
	// TODO public void deleteOrderForUser(Long userid, Long oid)
	
	public User createOrdersForUser(Long userid, Order order) throws UserNotFoundException {
		
		Order o1 = orderService.saveOrder(order);
		User user = findUser(userid);
		
		Set<Order> orders = user.getOrders();
		
		orders.add(o1);
		user.setOrders(orders);
		
		updateUser(user, userid);
		
		return user;
	}
}
