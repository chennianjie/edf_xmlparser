package common.exception;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/27/2019
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }
}
