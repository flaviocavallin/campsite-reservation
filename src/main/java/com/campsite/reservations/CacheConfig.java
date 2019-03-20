package com.campsite.reservations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(value = "app.cache.enable", havingValue = "true", matchIfMissing = true)
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		return new CaffeineCacheManager();
	}

}