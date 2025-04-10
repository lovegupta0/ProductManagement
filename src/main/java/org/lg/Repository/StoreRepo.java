package org.lg.Repository;

import org.lg.Model.Service;
import org.lg.Model.Store;
import org.lg.common.Response;


import java.util.Optional;

public interface StoreRepo {
    public Response<Object> Authenticate(String username, String password);
    public Response<Store> getUser();
    public Service getService();
    public void logout();
}
