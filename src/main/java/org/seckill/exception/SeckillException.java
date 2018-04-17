package org.seckill.exception;

/**秒杀香港异常
 * Created by ting on 2017/9/5.
 */
public class SeckillException extends  RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
