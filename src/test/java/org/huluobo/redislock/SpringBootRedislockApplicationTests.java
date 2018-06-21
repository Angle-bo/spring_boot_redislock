package org.huluobo.redislock;

import java.util.concurrent.TimeUnit;

import org.huluobo.redislock.lock.RedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedislockApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	String purpose = "everyDayLock";
	String key = "0";

	@Test
	public void contextLoads() throws InterruptedException {

		RedisLock redisLock = new RedisLock(purpose, key, redisTemplate);
		boolean isLock = redisLock.lock(50000L, TimeUnit.MILLISECONDS);

		if (isLock) {
			try {
				System.out.println("获取到锁，执行任务。");
				return;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				redisLock.unlock();
				redisLock.closeConnection();
				System.out.println("任务完毕，释放锁，关闭链接。");
			}
		}
	}

}
