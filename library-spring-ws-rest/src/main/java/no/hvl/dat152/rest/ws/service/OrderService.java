/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Order saveOrder(Order order) {
		
		order = orderRepository.save(order);
		
		return order;
	}
	
	public Order findOrder(Long id) throws OrderNotFoundException {
		
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
		
		return order;
	}
	
	public void deleteOrder(Long id) { 
		
		orderRepository.delete(orderRepository.getReferenceById(id));
		
	}
	
	public List<Order> findAllOrders() {
		
		List<Order> orders = (List<Order>) orderRepository.findAll();
		return orders;
		
	}
	
	public List<Order> findByExpiryDate(LocalDate expiry, Pageable page) {
		
		Page<Order> orders = orderRepository.findByExpiryBefore(expiry, page);
		List<Order> orderAsList = orders.getContent();
		System.out.println(orderAsList.toString());
		
		return orderAsList;
		
	}
	
	public Order updateOrder(Order order, Long id) throws OrderNotFoundException {
		
		Order orderToUpdate = findOrder(id);
		order.setId(orderToUpdate.getId());
		
		return orderRepository.save(order);
		
	}

	public List<Order> findOrdersByExpiredDate(LocalDate expiryDate, int limit, int offset) {
		
		return orderRepository.findOrderByExpiry(expiryDate, 10, 0);
	}
	
}
