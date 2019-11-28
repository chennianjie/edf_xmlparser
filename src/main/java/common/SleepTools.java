package common;

import java.util.concurrent.TimeUnit;

public class SleepTools {

    /**
     * 按s睡眠
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
     * 按ms睡眠
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
