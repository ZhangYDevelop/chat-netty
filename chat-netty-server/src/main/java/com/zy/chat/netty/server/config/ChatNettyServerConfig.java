package com.zy.chat.netty.server.config;

import com.zy.chat.netty.sdk.constant.ChatConstant;
import com.zy.chat.netty.sdk.hanlder.ChatNioSocketAcceptor;
import com.zy.chat.netty.sdk.hanlder.RequestHandler;
import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.sdk.model.SendBody;
import com.zy.chat.netty.server.handler.BindHandler;
import com.zy.chat.netty.server.handler.SessionClosedHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;

@Configuration
public class ChatNettyServerConfig implements RequestHandler, ApplicationListener<ApplicationStartedEvent> {

    @Value("${server.socketport}")
    private Integer socketPort;

    @Resource
    private ApplicationContext applicationContext;

    private final HashMap<String,RequestHandler> appHandlerMap = new HashMap<>();

    @Bean
    public ChatNioSocketAcceptor chatNioSocketAcceptor() {
        return  new ChatNioSocketAcceptor.Builder()
                .setAppPort(socketPort)
                .setOuterRequestHandler(this).build();
    }

    /**
     * 有客户端连接或者断开的时候调用，
     * @param mySession
     * @param sendBody
     */
    @Override
    public void process(MySession mySession, SendBody sendBody) {

        RequestHandler handler = appHandlerMap.get(sendBody.getKey());

        handler.process(mySession, sendBody);
    }

    /**
     * 在SpringBoot应用启动完，利用 Sprig 的ApplicationEvent事件回掉再来启动ServerSocket
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationEvent) {
        appHandlerMap.put(ChatConstant.CLIENT_CONNECT_BIND, applicationContext.getBean(BindHandler.class));
        appHandlerMap.put(ChatConstant.CLIENT_CONNECT_CLOSED, applicationContext.getBean(SessionClosedHandler.class));
        applicationContext.getBean(ChatNioSocketAcceptor.class).bind();
    }
}
