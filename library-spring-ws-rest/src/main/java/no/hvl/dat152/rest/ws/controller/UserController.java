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

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
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
	public ResponseEntity<Set<Order>> getUserOrders (@PathVariable("id") Long id) throws UserNotFoundException {
		
		Set<Order> orders = userService.getUserOrders(id);
		
		if(orders == null || orders.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	
	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
	@GetMapping("users/{oid}/orders")
	 public EntityModel<Order> getUserOrder(@PathVariable Long userId, @PathVariable Long orderId) throws OrderNotFoundException {
	        // Fetch order by ID from the service
	        Order order = orderService.findOrder(orderId);
	        return EntityModel.of(order,
	                linkTo(methodOn(UserController.class).getUserOrder(userId, orderId)).withSelfRel());
	 }

	// TODO - deleteUserOrder (@Mappings, URI, and method)
	 @DeleteMapping("users/{uid}/orders/{oid}")
	 public ResponseEntity<Void> deleteUserOrder(@PathVariable("uid")Long userId, @PathVariable("oid")Long orderId) throws UserNotFoundException, OrderNotFoundException {
		 
		 userService.deleteOrderForUser(userId, orderId);
		 
		 return new ResponseEntity<>(HttpStatus.OK);
	 }
	
	@PostMapping("/users/{uid}/orders")
	public ResponseEntity<Object> createUserOrder (@PathVariable("uid") Long uid, @RequestBody Order order) 
			throws UserNotFoundException, OrderNotFoundException, BookNotFoundException {
		
		//	HATEOAS links
		Link viewuser = linkTo(methodOn(UserController.class).getUser(uid)).withRel("view user").withType("GET");
		
		User user = userService.createOrdersForUser(uid, order);
		
		for(Order norder : user.getOrders()) {
			Link viewOrders = linkTo(methodOn(UserController.class).getUserOrder(user.getUserid(), norder.getId()))
					.withRel("view order").withType("GET");
			norder.add(viewOrders);
			norder.add(viewuser);
		}
		
		return (ResponseEntity<Object>) new ResponseEntity<Object>(user.getOrders(), HttpStatus.CREATED); 
		
	}
	
}
