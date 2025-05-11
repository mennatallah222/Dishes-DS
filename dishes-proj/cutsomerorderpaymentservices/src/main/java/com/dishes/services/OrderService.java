package com.dishes.services;

import org.springframework.stereotype.Service;

import com.dishes.dto.AddOrderDTO;
import com.dishes.dto.OrderResponse;
import com.dishes.entities.Order;
import com.dishes.entities.ShippingCompany;
import com.dishes.repositories.OrderRepository;
import com.dishes.repositories.ShippingCompanyRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {
    private final ShippingCompanyRepository shippingCompanyRepository;
    private final OrderRepository orderRepository;

    public OrderService(ShippingCompanyRepository shippingCompanyRepository, OrderRepository orderRepository) {
        this.shippingCompanyRepository=shippingCompanyRepository;
        this.orderRepository=orderRepository;
    }

    @Transactional
    public OrderResponse addDish(AddOrderDTO order, String authHeader){


        ShippingCompany shippingCompany = shippingCompanyRepository.findByUniqueName(order.getShippingCompany().trim().toLowerCase())
                .orElseGet(() ->{
                    ShippingCompany newCompany=new ShippingCompany();
                    newCompany.setName(order.getShippingCompany().trim());
                    return shippingCompanyRepository.save(newCompany);
                });





                
        Order newOrder = new Order();
        newOrder.setShippingCompany(shippingCompany);
        newOrder.setPrice(order.getTotal());

        Order savedDProduct=orderRepository.save(newOrder);
        return convertToResponse(savedDProduct);
    }

    private OrderResponse convertToResponse(Order product) {
        OrderResponse response = new OrderResponse();
        response.setId(product.getId());
        response.setTotal(product.getTotal());
        response.setStatus(product.getStatus().name());
        response.setShippingCompany(product.getShippingCompany().getName());
        return response;
    }
}
