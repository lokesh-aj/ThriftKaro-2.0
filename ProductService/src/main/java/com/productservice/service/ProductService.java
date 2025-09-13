package com.productservice.service;

import com.productservice.entities.Product;
import com.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(Long id) {
		return productRepository.findById(id)
				.orElse(null);
	}

	@Transactional
	public Product updateStock(Long id, Integer quantity) {
		Product product = productRepository.findById(id).orElse(null);
		if (product == null) {
			return null;
		}
		product.setStockQuantity(quantity);
		return product;
	}

	@Transactional
	public boolean deleteProduct(Long id) {
		Product product = productRepository.findById(id).orElse(null);
		if (product == null) {
			return false;
		}
		productRepository.delete(product);
		return true;
	}
} 