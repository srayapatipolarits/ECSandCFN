package com.sp.web.product;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Product;
import com.sp.web.model.ProductStatus;
import com.sp.web.model.ProductType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author daxabraham
 *
 *         The Mongo DB implementation of Product Repository.
 */
@Repository
public class MongoProductRepository implements ProductRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.product.ProductRepository#getProductsByType(com.sp.web.model .ProductType)
   */
  @Override
  public List<Product> findByType(ProductType productType) {
    return mongoTemplate.find(
        query(where("productType").is(productType).andOperator(
            where("status").is(ProductStatus.Active))), Product.class);
  }

  @Override
  public Product findById(String productId) {
    return mongoTemplate.findById(productId, Product.class);
  }

  @Override
  public Product getHiringProduct() {
    return mongoTemplate.findOne(
        query(where("name").is(Constants.HIRING_PRODUCT_NAME).andOperator(
            where("status").is(ProductStatus.Active))), Product.class);
  }

  @Override
  public List<Product> findAllProductsById(List<String> productIdList) {
    return mongoTemplate.find(query(where("id").in(productIdList)), Product.class);
  }

  @Override
  public Product findByIdValidated(String productId) {
    return Optional.ofNullable(findById(productId)).orElseThrow(
        () -> new InvalidRequestException("No product found for id:" + productId));
  }

  @Override
  public List<Product> getAllProducts() {
    return mongoTemplate.findAll(Product.class);
  }
}