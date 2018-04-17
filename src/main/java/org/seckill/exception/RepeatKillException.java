package org.seckill.exception;

/**
 * spring回滚策略只接受运行期异常
 *重复秒杀异常（运行期异常）
 * Created by ting on 2017/9/5.
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
