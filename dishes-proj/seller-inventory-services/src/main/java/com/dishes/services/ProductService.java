package com.dishes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dishes.dtos.AddDishRequest;
import com.dishes.dtos.ProductResponse;
import com.dishes.entities.Product;
import com.dishes.entities.Product.ProductStatus;
import com.dishes.jwt.JwtTokenUtil;
import com.dishes.entities.Seller;
import com.dishes.repositories.ProductRepository;
import com.dishes.repositories.SellerRespository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final SellerRespository sellerRespository;
    private final JwtTokenUtil token;

    public ProductService(ProductRepository productRepository, SellerRespository sellerRespository, JwtTokenUtil token/*, ShippingCompanyRepository shippingCompanyRepository*/) {
        this.productRepository = productRepository;
        this.sellerRespository=sellerRespository;
        this.token=token;
    }

    @Transactional
    public ProductResponse addDish(AddDishRequest dish, String authHeader) {
        Long sellerId=token.extractSellerId(authHeader.substring(7));
        Seller seller=sellerRespository.findById(sellerId).stream().findFirst()
        .orElseGet(()->{
            Seller newSeller=new Seller();
            newSeller.setId(sellerId);
            return sellerRespository.save(newSeller);
        });

        boolean exists = productRepository.existsByNameAndSeller(dish.getName(), seller);
        if (exists) {
            return null;
        }
        seller.setId(sellerId);

        Product newDish = new Product();
        newDish.setName(dish.getName());
        newDish.setAmount(dish.getAmount());
        newDish.setPrice(dish.getPrice());
        newDish.setStatus(dish.getAmount() > 0 ? ProductStatus.AVAILABLE : ProductStatus.SOLD_OUT);
        newDish.setSeller(seller);

        Product savedDProduct=productRepository.save(newDish);
        return convertToResponse(savedDProduct);
    }

    public List<ProductResponse> getProductsBySeller(String authHeader) {
        Long sellerId=token.extractSellerId(authHeader.substring(7));
        return productRepository.findBySellerId(sellerId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setAmount(product.getAmount());
        response.setPrice(product.getPrice());
        response.setStatus(product.getStatus().name());
        return response;
    }
}