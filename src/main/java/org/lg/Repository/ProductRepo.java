package org.lg.Repository;

import org.lg.Model.Product;
import org.lg.common.Response;

import java.util.List;

public interface ProductRepo {
    public Response<Product> addProduct(Product product);
    public Response<Product> updateProduct(Product product);
    public Response<Boolean> deleteProduct(String id);
    public Response<List<Product>> getAllProducts();
    public Response<Product> findProductById(String id);
    public Response<Object> updateProducts(List<Product> list);
    public Response<Boolean> updateProductInventory(String uuid,float updateQuantity);

}
