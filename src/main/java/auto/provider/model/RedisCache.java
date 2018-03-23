package auto.provider.model;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


public class RedisCache {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

   
    /**
	 * @return the redisTemplate
	 */
	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	/**
	 * @param redisTemplate the redisTemplate to set
	 */
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
     * 添加缓存数据
     * @param key
     * @param obj
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> boolean putCache(String key, T obj) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = SerializerUtil.serializeObj(obj);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(bkey, bvalue);
            }
        });
        return result;
    }

    /**
     * 添加缓存数据，设定缓存失效时间
     * @param key
     * @param obj
     * @param expireTime
     * @param <T>
     * @throws Exception
     */
    public <T> void putCacheWithExpireTime(String key, T obj, final long expireTime){
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = SerializerUtil.serializeObj(obj);
        redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
    }

    /**
     * 根据key取缓存数据
     * @param key
     * @param <T>
     * @return
     * @throws Exception
     */
   
	public <T> T getCache(final String key) {
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes());
                
                
            }
        });
        if (result == null) {
            return null;
        }
        return (T) SerializerUtil.deserializeObj(result);
    }
	
	/**
	 * 
	* @time:2017年8月31日下午2:11:32
	* @Description: 根据key清除缓存
	* @return void    返回类型 
	* @throws
	 */
	public <T> void delCache(final String key){
        redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(key.getBytes());
                return true;
            }
        });

		
	}
	
}