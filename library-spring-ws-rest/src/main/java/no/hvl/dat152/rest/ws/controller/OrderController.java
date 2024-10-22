/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@GetMapping("/orders")
   	public ResponseEntity<Object> getAllBorrowOrders (
   			@RequestParam(required = false) LocalDate expiry,
   			@RequestParam(defaultValue = "0") int page,
   			@RequestParam(defaultValue = "3") int size){
		
		Pageable pageable = PageRequest.of(page, size);
		
		if(expiry != null) {
			return new ResponseEntity<>(orderService.findByExpiryDate(expiry, pageable), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(orderService.findAllOrders(), HttpStatus.OK);
		}
	}
	//filter by expiry and paginate 
   	
   	
	@GetMapping("/orders/{id}")
	public ResponseEntity<Object> getBorrowOrder (@PathVariable("id") Long id) throws OrderNotFoundException{
		
		try {
			Order order = orderService.findOrder(id);
			return new ResponseEntity<>(order, HttpStatus.OK);
		
		} catch(OrderNotFoundException e) {
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	@PutMapping("/orders/{id}")
	public ResponseEntity<Object> updateOrder (@PathVariable("id") Long id, @RequestBody Order order) throws OrderNotFoundException {
		
		order = orderService.updateOrder(order, id);
		Link ordersLink = linkTo(methodOn(OrderController.class).deleteBookOrder(id)).withRel("Update_Return_or_cancel");
		order.add(ordersLink);
		
		return new ResponseEntity<>(order, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/orders/{id}")
	public ResponseEntity<Object> deleteBookOrder (@PathVariable("id") Long id) {
		
		orderService.deleteOrder(id);
		
		return new ResponseEntity<>("Order with id: '"+id+"' deleted!", HttpStatus.OK);
		
	}
	
}
