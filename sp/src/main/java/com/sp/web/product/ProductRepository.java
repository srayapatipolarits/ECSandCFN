package com.sp.web.product;

import com.sp.web.model.Product;
import com.sp.web.model.ProductType;

import java.util.List;

/**
 * This is the repository class for products.
 * 
 * @author Dax Abraham
 */
@Deprecated
public interface ProductRepository {

  /**
   * Finds all the products by the given product type.
   * 
   * @param productType
   *          - the product type
   * 
   * @return - the list of products for the given product type
   */
  @Deprecated
  List<Product> findByType(ProductType productType);

  /**
   * Finds the product by the given product id.
   * 
   * @param productId
   *          - product id
   * @return the product if found
   */
  @Deprecated
  Product findById(String productId);

  /**
   * finds the product for the given product id but also validates the response for null.
   * 
   * @param productId
   *          - product id
   * @return
   *      the product
   */
  @Deprecated
  Product findByIdValidated(String productId);

  /**
   * Gets the hiring product configured in the system.
   * 
   * @return - the hiring product
   */
  @Deprecated
  Product getHiringProduct();

  /**
   * Gets all the products from the provided proudct id's in the product list.
   * 
   * @param productIdList
   *          - list of product id's
   * @return the list of products found
   */
  @Deprecated
  List<Product> findAllProductsById(List<String> productIdList);

  /**
   * Get all the products in the repository.
   * 
   * @return
   *      - the list of products
   */
  @Deprecated
  List<Product> getAllProducts();
}
