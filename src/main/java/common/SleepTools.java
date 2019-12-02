package common;

import java.util.concurrent.TimeUnit;

public class SleepTools {

    /**
     * by s
     * @param seconds
     */
    public static final void s(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * by ms
     * @param ms
     */
    public static final void ms(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
