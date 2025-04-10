package org.lg.Repository;

import org.lg.Model.Order;
import org.lg.common.Response;

import java.util.List;
import java.util.Optional;

public interface OrderRepo {

    public Response<List<Order>> getAllOrder();
    public Response<Order> findByOrderId(String id);
    public Response<Boolean> deleteOrder(String id);
    public Response<Order> updateOrder(Order order);
    public Response<Order> doCheckOut(Order order);
    public Response<Boolean> isAbleToCheckOut(Order order);
}
