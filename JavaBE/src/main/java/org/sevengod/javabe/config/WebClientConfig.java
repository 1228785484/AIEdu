package org.sevengod.javabe.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        // 创建自定义的连接提供者，支持长连接
        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        // 配置 HTTP 客户端
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                .responseTimeout(Duration.ofMinutes(5))  // SSE 需要更长的超时时间
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(300, TimeUnit.SECONDS))  // 5分钟读超时
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)))  // 1分钟写超时
                .wiretap(true);  // 启用调试日志

        // 配置更大的内存缓冲区
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))  // 16MB 缓冲区
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)  // 默认接受 SSE
                .filter((request, next) -> {
                    // 添加请求响应过滤器，用于调试
                    return next.exchange(request)
                            .doOnError(error -> 
                                System.err.println("Error during request: " + error.getMessage()));
                })
                .build();
    }
}