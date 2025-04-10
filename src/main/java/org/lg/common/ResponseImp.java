package org.lg.common;

public class ResponseImp<T> implements Response {

    private T data;
    private ResponseStatus responseStatus;

    public ResponseImp() {}

    public ResponseImp(StatusCode statusCode, T data) {
        this.responseStatus = ResponseStatus.of(statusCode);
        this.data = data;
    }
    public ResponseImp(ResponseStatus responseStatus,T data){
        this.responseStatus=responseStatus;
        this.data=data;
    }
    public ResponseImp(int code,String message,T data){
        new ResponseImp(ResponseStatus.of(code,message),data);
    }


    public static <T> ResponseImp<T> success(T data) {
        return new ResponseImp<>(StatusCode.OK, data);
    }
    public static <T> ResponseImp<T> error(ResponseStatus status ){
        return new ResponseImp<>(status,null);
    }
    public static <T> ResponseImp<T> error(ResponseStatus status,T data ){
        return new ResponseImp<>(status,data);
    }

    public static <T> ResponseImp<T> error(StatusCode statusCode) {
        return new ResponseImp<>(statusCode, null);
    }

    // Getters and setters
    @Override
    public int getStatusCode() {
        return responseStatus.getCode();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.responseStatus=ResponseStatus.of(statusCode);
    }
    @Override
    public String getMessage() {
        return responseStatus.getMessage();
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
