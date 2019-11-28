package exception;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/27/2019
 */
public class FeedException extends RuntimeException {

    public FeedException() {
    }

    public FeedException(String message) {
        super(message);
    }
}
