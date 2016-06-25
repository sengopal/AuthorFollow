package com.capstone.authorfollow.data.types;

/**
 * Created by sengopal on 6/24/16.
 */
public class NetworkResponse<T> {
    private boolean success;
    private String errorMessage;
    private T response;

    public NetworkResponse(String errorMessage, T response) {
        this.errorMessage = errorMessage;
        this.response = response;
        if (null == errorMessage) {
            this.success = true;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getResponse() {
        return response;
    }
}
