/**
 * 
 */
package auto.provider.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author BlackStone
 *
 */

@Component
public class JedisUtil {
	/** 
	 * @ClassName: JedisUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年8月24日 下午6:01:24 
	 * 
	 */
	
	Logger log=Logger.getLogger(getClass());
	
	@Value("${redis.host}")private String hostName;
	@Value("${redis.port}")private String port;
	@Value("${redis.password}")private String password;
	@Value("${redis.maxActive}")private String maxTotal;
	@Value("${redis.maxIdle}")private String  maxIdle;
	@Value("${redis.maxWaitMillis}")private String  maxWaitMillis;
	@Value("${redis.timeout}")private String timeout;
	private boolean testOnBorrow=true;
	
	private static Jedis jedis;
	private static JedisPool pool;

	private JedisPool getPoolInstance(){
		if(null==pool){
			JedisPoolConfig cf=new JedisPoolConfig();
            // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
            cf.setMaxTotal(Integer.parseInt(maxTotal));  
            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
            cf.setMaxIdle(Integer.parseInt(maxIdle));  
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
            cf.setMaxWaitMillis(Integer.parseInt(maxWaitMillis));  
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
            cf.setTestOnBorrow(testOnBorrow); 
         
            pool = new JedisPool(cf, hostName,Integer.parseInt(port),Integer.parseInt(timeout));  
			
		}
		
		return pool;
	}
	private void jedisInit(){
		
		if(null==jedis){
			jedis=getPoolInstance().getResource();
		}
		
		//log.info("password:"+password+" "+password.length());
		if(password!=null&&password.length()>0)jedis.auth(password);

	}

	//String  operation
	
	public void set(String key,String v){
		jedisInit();
		jedis.set(key, v);
		
	}
	
	public String get(String key){
		jedisInit();
		return jedis.get(key);
	}
	
	public void del(String key){
		jedisInit();
		jedis.del(key);
		
	}
	//Map opertation
	public void setM(String key,Map<String,String> map){
		jedisInit();
		jedis.hmset(key, map);
		
		
	}
	public List<String> getM(String key,String...fields){
		jedisInit();
		return jedis.hmget(key,fields);
		
		
	}
	
	public void delFields(String key,String...fields){
		jedisInit();
		jedis.hdel(key, fields);
	}
	
	public Set<String> hasFields(String key){
		jedisInit();
		return jedis.hkeys(key);
	}
	
	public List<String> hasFieldValues(String key){
		jedisInit();
		return jedis.hvals(key);
	}
	
	
	
	
	//list operation;
	
	
	
	//others
	
	public Set<String> getKeys(String pattern){
		jedisInit();
		 return jedis.keys(pattern);
		
	}
	
	public Long getKeylen(String key){
		jedisInit();
		return jedis.hlen(key);
	}
	
	public boolean exists(String key){
		jedisInit();
		return jedis.exists(key);
	}
	
	
}
