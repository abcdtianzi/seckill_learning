package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * Created by ting on 2017/10/17.
 */
public class RedisDao {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }


    //模式：根据此模式创造相应的值，进行相对应的反序列化
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        //redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //redis缓存存储数据都是二进制字节数组，并没有实现内部序列化操作(重视序列化)
                //get->byte[]->反序列化->Object(Seckill)
                //采用自定义序列化
                //protostuff:pojo（必须有get，set）
                byte[] bytes = jedis.get(key.getBytes());
                //从缓存获取到值
                if (bytes != null) {
                    //空对象，用来做待存放数据的对象容器
                    Seckill seckill = schema.newMessage();
                    //执行完这一句后，seckill被反序列化
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;

                }

            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    public String putSeckill(Seckill seckill) {
        //set object(Seckill) ->序列化->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getId();
                //LinkedBuffer.allocate：使用LinkedBuffer分配一块默认大小的buffer空间
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //设置超时缓存
                int timeout = 60 * 60; //缓存时间为1小时，以秒为单位

                //存入缓存，存入成功返回ok，存入失败返回错误信息
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
