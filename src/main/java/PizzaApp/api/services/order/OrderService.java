package PizzaApp.api.services.order;

import PizzaApp.api.entity.order.Order;
import PizzaApp.api.entity.dto.order.OrderCreatedOnDTO;
import PizzaApp.api.entity.dto.order.OrderDTO;

public interface OrderService {

	Long createOrUpdate(Order order);

	Order findById(Long id);

	OrderDTO findDTOByIdAndTel(String id, String orderContactTel);

	OrderCreatedOnDTO findCreatedOnById(Long id);

	void deleteById(Long id);

}
