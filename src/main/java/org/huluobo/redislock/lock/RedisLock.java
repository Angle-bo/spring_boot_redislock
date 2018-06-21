package org.huluobo.redislock.lock;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLock {

	private String key;
	private boolean lock = false;

	private final StringRedisTemplate redisClient;
	private final RedisConnection redisConnection;

	/**
	 * @param purpose
	 *            锁前缀
	 * @param key
	 *            锁定的ID等东西
	 */
	public RedisLock(String purpose, String key, StringRedisTemplate redisClient) {
		if (redisClient == null) {
			throw new IllegalArgumentException("redisClient 不能为null!");
		}
		this.key = purpose + "_" + key + "_redis_lock";
		this.redisClient = redisClient;
		this.redisConnection = redisClient.getConnectionFactory().getConnection();
	}

	/**
	 * 
	 * @param expire
	 *            设置锁超时时间
	 * @param unit
	 * @return
	 */
	public boolean lock(long expire, final TimeUnit unit) {
		try {
			// 锁不存在的话，设置锁并设置锁过期时间，即加锁
			if (this.redisClient.opsForValue().setIfAbsent(this.key, "1")) {
				this.redisClient.expire(key, expire, unit);// 设置锁失效时间, 防止永久阻塞
				this.lock = true;
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public void closeConnection() {
		if (!this.redisConnection.isClosed()) {
			this.redisConnection.close();
		}
	}

	/** 释放锁 */
	public void unlock() {
		if (this.lock) {
			redisClient.delete(key);
		}
	}

	public boolean isLock() {
		return lock;
	}
}
