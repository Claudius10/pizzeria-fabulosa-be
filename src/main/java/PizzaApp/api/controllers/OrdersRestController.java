package PizzaApp.api.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PizzaApp.api.entity.Address;
import PizzaApp.api.entity.Customer;
import PizzaApp.api.entity.Order;
import PizzaApp.api.services.OrdersService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class OrdersRestController {

	private OrdersService ordersService;

	public OrdersRestController(OrdersService ordersService) {
		this.ordersService = ordersService;
	}

	@PostMapping("/order")
	public Order createOrder(@RequestBody Order order) {
		order.setId((long) 0);
		ordersService.createOrder(order);
		return order;
	}

	@PutMapping("/order")
	public Order updateOrder(@RequestBody Order order) {
		ordersService.createOrder(order);
		return order;
	}

	@GetMapping("/orders")
	public List<Order> getOrders() {
		return ordersService.getOrders();
	}

	@GetMapping("/order/{id}")
	public Order findOrderById(@PathVariable Long id) {
		return ordersService.findOrderById(id);
	}

	@GetMapping("/orders/{storeName}")
	public List<Order> getOrdersByStore(@PathVariable String storeName) {
		return ordersService.getOrdersByStore(storeName);
	}

	@GetMapping("/orders/customer/{customerId}")
	public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
		return ordersService.getOrdersByCustomer(customerId);
	}

	@DeleteMapping("/order/{id}")
	public void deleteOrderById(@PathVariable Long id) {
		ordersService.deleteOrderById(id);
	}

	////////////////

	@GetMapping("/customers")
	public List<Customer> getCustomer() {
		return ordersService.getCustomers();
	}

	@GetMapping("/address")
	public List<Address> getAddress() {
		return ordersService.getAddress();
	}

}
