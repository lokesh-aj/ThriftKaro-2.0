package com.productservice.service;

import com.productservice.entities.Product;
import com.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product addProduct(Product product) {
		if (product.getCreatedAt() == null) {
			product.setCreatedAt(java.time.LocalDateTime.now());
		}
		return productRepository.save(product);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(String id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}

	@Transactional
	public Product updateProduct(String id, Product updatedProduct) {
		Optional<Product> existingProductOpt = productRepository.findById(id);
		if (existingProductOpt.isEmpty()) {
			return null;
		}
		
		Product existingProduct = existingProductOpt.get();
		
		// Update fields if provided
		if (updatedProduct.getName() != null) {
			existingProduct.setName(updatedProduct.getName());
		}
		if (updatedProduct.getDescription() != null) {
			existingProduct.setDescription(updatedProduct.getDescription());
		}
		if (updatedProduct.getCategory() != null) {
			existingProduct.setCategory(updatedProduct.getCategory());
		}
		if (updatedProduct.getTags() != null) {
			existingProduct.setTags(updatedProduct.getTags());
		}
		if (updatedProduct.getOriginalPrice() != null) {
			existingProduct.setOriginalPrice(updatedProduct.getOriginalPrice());
		}
		if (updatedProduct.getDiscountPrice() != null) {
			existingProduct.setDiscountPrice(updatedProduct.getDiscountPrice());
		}
		if (updatedProduct.getStock() != null) {
			existingProduct.setStock(updatedProduct.getStock());
		}
		if (updatedProduct.getImages() != null) {
			existingProduct.setImages(updatedProduct.getImages());
		}
		if (updatedProduct.getReviews() != null) {
			existingProduct.setReviews(updatedProduct.getReviews());
		}
		if (updatedProduct.getRatings() != null) {
			existingProduct.setRatings(updatedProduct.getRatings());
		}
		
		return productRepository.save(existingProduct);
	}

	@Transactional
	public Product updateStock(String id, Integer quantity) {
		Optional<Product> productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			return null;
		}
		Product product = productOpt.get();
		product.setStock(quantity);
		return productRepository.save(product);
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

	public List<Product> getProductsByCategory(String category) {
		return productRepository.findByCategory(category);
	}

	public List<Product> searchProducts(String searchTerm) {
		return productRepository.findByNameContainingIgnoreCase(searchTerm);
	}
}
