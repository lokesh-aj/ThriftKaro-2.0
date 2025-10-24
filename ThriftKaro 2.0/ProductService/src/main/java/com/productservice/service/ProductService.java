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

	public Product getProductById(String id) {
		return productRepository.findById(id)
				.orElse(null);
	}

	@Transactional
	public Product updateStock(String id, Integer quantity) {
		Product product = productRepository.findById(id).orElse(null);
		if (product == null) {
			return null;
		}
		product.setStock(quantity);
		return product;
	}

	@Transactional
	public boolean deleteProduct(String id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}

	public List<Product> getProductsByShopId(String shopId) {
		return productRepository.findByShopId(shopId);
	}
} 