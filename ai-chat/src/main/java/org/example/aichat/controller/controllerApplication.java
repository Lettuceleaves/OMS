package org.example.aichat.controller;

import org.reactivestreams.Publisher;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;

@RestController
@RequestMapping("/ai")
public class controllerApplication {
    private final ChatClient chatClient;

    public controllerApplication(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/fluxchat")
    public Flux<ServerSentEvent<String>> generateFlux(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .map(token -> {
                    // 在这里可以对 token 进行处理，例如转换为字符串
                    String processedToken = token.toString(); // 假设 token 已经是字符串，这里直接返回
                    // 如果需要，可以在这里添加日志
                    System.out.println("Processed token: " + processedToken + " at " + System.currentTimeMillis());
                    return ServerSentEvent.<String>builder().data(processedToken).build();
                });
    }
}