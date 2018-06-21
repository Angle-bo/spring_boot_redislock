package org.huluobo.redislock.task;

import java.util.concurrent.TimeUnit;

import org.huluobo.redislock.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EveryDayTask {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Value("${server.port}")
	String port;

	String purpose = "everyDayLock";
	String key = "0";

	@Scheduled(cron = "0 55 13 * * ?")
	public void dayTask() {
		System.out.println("开始执行每日任务");
		RedisLock redisLock = new RedisLock(purpose, key, redisTemplate);
		boolean isLock = redisLock.lock(50000L, TimeUnit.MILLISECONDS);

		if (isLock) {
			try {
				System.out.println(port + "获取到锁，执行任务。");
				Thread.sleep(3000L);
				return;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				redisLock.unlock();
				System.out.println("任务完毕，释放锁。");
			}
		} else {
			System.out.println(port + "未获取到锁。");
		}
		
		redisLock.closeConnection();
		System.out.println("任务完毕，释放链接。");

	}

}
