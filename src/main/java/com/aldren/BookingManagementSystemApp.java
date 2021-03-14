package com.aldren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class BookingManagementSystemApp
{
    private RedisServer redisServer;

    public static void main( String[] args )
    {
        SpringApplication.run(BookingManagementSystemApp.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }

}
