package com.capstone.authorfollow.data.types;

public class NetworkResponse<T> {
    private boolean success;
    private T response;

    public NetworkResponse(String errorMessage, T response) {
        this.response = response;
        if (null == errorMessage) {
            this.success = true;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResponse() {
        return response;
    }
}
