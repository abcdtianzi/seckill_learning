package org.seckill.dto;

/**
 * Created by ting on 2017/9/17.
 */
//封装json结果
public class SeckillResult<T> {

    //success为true取data，success为false取error
    private boolean success;
    private T data;

    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public SeckillResult(boolean success, T data) {
        this.data = data;
        this.success = success;

    }

    public SeckillResult( boolean success, String error) {
        this.error = error;
        this.success = success;
    }


}
