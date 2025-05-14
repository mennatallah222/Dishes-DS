package com.dishes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dishes.dtos.AddDishRequest;
import com.dishes.dtos.ProductResponse;
import com.dishes.dtos.ProductSoldResponse;
import com.dishes.dtos.UpdateProductRequest;
import com.dishes.entities.Product;
import com.dishes.entities.Product.ProductStatus;
import com.dishes.jwt.JwtTokenUtil;
import com.dishes.entities.Seller;
import com.dishes.entities.SoldProduct;
import com.dishes.repositories.ProductRepository;
import com.dishes.repositories.SellerRespository;
import com.dishes.repositories.SoldProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final SoldProductRepository soldProductRepository;
    private final SellerRespository sellerRespository;
    private final JwtTokenUtil token;

    public ProductService(ProductRepository productRepository, SellerRespository sellerRespository, JwtTokenUtil token, SoldProductRepository soldProductRepository) {
        this.soldProductRepository=soldProductRepository;
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
        Product newDish = new Product();
        newDish.setName(dish.getName());
        newDish.setAmount(dish.getAmount());
        newDish.setPrice(dish.getPrice());
        newDish.setImageUrl(dish.getImageUrl());
        newDish.setStatus(dish.getAmount()>0?ProductStatus.AVAILABLE:ProductStatus.SOLD_OUT);
        newDish.setSeller(seller);

        Product savedDProduct=productRepository.save(newDish);
        return mapToProductResponse(savedDProduct);
    }

    public List<ProductResponse> getProductsBySeller(String authHeader) {
        Long sellerId=token.extractSellerId(authHeader.substring(7));
        return productRepository.findBySellerId(sellerId).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public List<ProductSoldResponse> getSoldProducts(String authHeader) {
        Long sellerId = token.extractSellerId(authHeader.substring(7));
        return soldProductRepository.findBySellerId(sellerId).stream().map(this::mapToSoldResponse).toList();
    }

    public List<ProductResponse> getAvailableProducts(String authHeader) {
        Long sellerId = token.extractSellerId(authHeader.substring(7));
        return productRepository.findBySellerIdAndStatus(sellerId, ProductStatus.AVAILABLE).stream().map(this::mapToProductResponse).toList();
    }

    public ProductResponse updateDish(Long dishId, UpdateProductRequest request, String authHeader) {
        Long sellerId = token.extractSellerId(authHeader.substring(7));
        Product existingDish = productRepository.findByIdAndSellerId(dishId, sellerId)
                .orElse(null);
        
        if (existingDish == null) {
            return null;
        }        
        boolean nameExists = productRepository.existsByNameAndSellerAndIdNot(
                request.getName(), 
                existingDish.getSeller(), 
                dishId);
        
        if (nameExists) {
            return null;
        }
        
        existingDish.setName(request.getName());
        existingDish.setAmount(request.getAmount());
        existingDish.setPrice(request.getPrice());
        existingDish.setStatus(request.getAmount() > 0 ? 
                ProductStatus.AVAILABLE : ProductStatus.SOLD_OUT);
        
        Product updatedProduct = productRepository.save(existingDish);
        return mapToProductResponse(updatedProduct);
    }


    private ProductSoldResponse mapToSoldResponse(SoldProduct item) {
        ProductSoldResponse response = new ProductSoldResponse();
        response.setName(item.getProductName());
        response.setPrice(item.getPrice());
        response.setCustomerName(item.getCustomerName());
        response.setCustomerEmail(item.getCustomerEmail());
        response.setCompanyName(item.getShippingCompany());
        response.setImageUrl(item.getImageUrl());

        return response;
    }


    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setAmount(product.getAmount());
        response.setPrice(product.getPrice());
        response.setStatus(product.getStatus().name());
        response.setImageUrl(product.getImageUrl());
        return response;
    }

}