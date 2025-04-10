package org.lg.common;

public interface Response<T> {
    public int getStatusCode();
    public String getMessage();
    public T getData();
    public ResponseStatus getResponseStatus();
}
