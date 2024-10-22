/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.OrderService;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(){
		
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty())
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable("id") Long id) throws UserNotFoundException, OrderNotFoundException{
		
		User user = userService.findUser(id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);	
		
	}
	
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@RequestBody User user) {
		
		user = userService.saveUser(user);
		
		return new ResponseEntity<>(user, HttpStatus.CREATED);
		
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser (@RequestBody User user, 
						@PathVariable("id") Long id) throws UserNotFoundException {
		
		user = userService.updateUser(user, id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser (@PathVariable("id") Long id) throws UserNotFoundException {
		
		userService.deleteUser(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	@GetMapping("/users/{id}/orders")
	public ResponseEntity<Object> getUserOrders (@PathVariable("id") Long id) throws UserNotFoundException {
		
		Set<Order> orders = userService.getUserOrders(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	
	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
	 public EntityModel<Order> getUserOrder(@PathVariable Long userId, @PathVariable Long orderId) throws OrderNotFoundException {
	        // Fetch order by ID from the service
	        Order order = orderService.findOrder(orderId);
	        return EntityModel.of(order,
	                linkTo(methodOn(UserController.class).getUserOrder(userId, orderId)).withSelfRel());
	 }

	// TODO - deleteUserOrder (@Mappings, URI, and method)
	
	@PostMapping("/users/{id}/orders")
	public EntityModel<Order> createUserOrder (@PathVariable("id") Long userId, @RequestBody Order newOrder) 
			throws UserNotFoundException, OrderNotFoundException {
		
		Order savedOrder = orderService.saveOrder(newOrder);
		
		//	+ HATEOAS links
		EntityModel<Order> resource = EntityModel.of(savedOrder); 
		
		resource.add(linkTo(methodOn(UserController.class).getUserOrder(userId, savedOrder.getId())).withSelfRel());
		
		resource.add(linkTo(methodOn(UserController.class).getUserOrders(userId)).withRel("user-orders"));
		
		resource.add(linkTo(methodOn(UserController.class).updateUser(null, userId)).withRel("update-user"));
		
		resource.add(linkTo(methodOn(UserController.class).getUser(userId)).withRel("get-user"));
		
		return resource;
	}
	
}
