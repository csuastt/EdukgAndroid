/***
 * This Class stores the result for login activity.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.data;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    /***
     * This Function transfers the result to string.
     * @param
     * @return returns the result in a string form
     */
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    /***
     * This class is a inner-linked class which shows the successful result.
     * @param
     * @return returns the result in a string form
     */
    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    /***
     * This class is a inner-linked class which shows the error result
     * @param
     * @return returns the result in a string form
     */
    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}