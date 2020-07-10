package com.zy.chat.netty.sdk.hanlder;

import com.zy.chat.netty.sdk.coder.MessageDecoder;
import com.zy.chat.netty.sdk.coder.MessageEncoder;
import com.zy.chat.netty.sdk.constant.ChatConstant;
import com.zy.chat.netty.sdk.model.HeartbeatResponse;
import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.sdk.model.SendBody;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * netty server 封装
 */
public class ChatNioSocketAcceptor {

    private Logger logger = LoggerFactory.getLogger(ChatNioSocketAcceptor.class);


    private final HashMap<String, RequestHandler> innerHandlerMap = new HashMap<>();

    private final ConcurrentHashMap<String,Channel> channelGroup = new ConcurrentHashMap<>();

    private final RequestHandler outerRequestHandler;

    private final ChannelHandler channelEventHandler = new FinalChannelEventHandler();

    /**
     * 服务端口
     */
    private Integer serverPort;

    /**
     * netty server boos 线程组
     */
    private EventLoopGroup boos;

    /**
     * netty server worker 线程组
     */
    private EventLoopGroup worker;


    /**
     *  读空闲时间(秒)
     */
    public static final int READ_IDLE_TIME = 150;

    /**
     *  写接空闲时间(秒)
     */
    public static final int WRITE_IDLE_TIME = 120;

    /**
     * 心跳响应 超时为30秒
     */
    public static final int PONG_TIME_OUT = 10;

    public ChatNioSocketAcceptor(Builder builder) {

        this.serverPort = builder.serverPort;
        this.outerRequestHandler = builder.outerRequestHandler;
    }

    public void bind() {
        //存储心跳处理handler
        innerHandlerMap.put(ChatConstant.CLIENT_HEARTBEAT, new HeartbeatHandler());

        //初始化ServerBootstrap
        boos = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boos, worker);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new ChunkedWriteHandler());
                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                ch.pipeline().addLast(new MessageEncoder());
                ch.pipeline().addLast(new MessageDecoder());
                ch.pipeline().addLast(channelEventHandler);
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.bind(serverPort).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channelFuture.channel().newSucceededFuture().addListener(future1 -> {
            String logBanner = "\n\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "*                   Websocket Server started on port {}.                         *\n" +
                    "*                                                                                   *\n" +
                    "*                                                                                   *\n" +
                    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
            logger.info(logBanner, serverPort);
        });

        channelFuture.channel().closeFuture().addListener(future1 -> {
           // 关闭Websocket Server 服务
           destory(boos, worker);
        });


    }

    /**
     * 销毁服务
     * @param boos
     * @param worker
     */
    private void destory(EventLoopGroup boos, EventLoopGroup worker) {
        if(boos != null && !boos.isShuttingDown() && !boos.isShutdown() ) {
            try {boos.shutdownGracefully();}catch(Exception ignore) {}
        }

        if(worker != null && !worker.isShuttingDown() && !worker.isShutdown() ) {
            try {worker.shutdownGracefully();}catch(Exception ignore) {}
        }
    }

    public static class Builder{

        private Integer serverPort;
        private RequestHandler outerRequestHandler;

        public Builder setAppPort(Integer appPort) {
            this.serverPort = appPort;
            return this;
        }

        /**
         * 设置应用层的sentBody处理handler
         */
        public Builder setOuterRequestHandler(RequestHandler outerRequestHandler) {
            this.outerRequestHandler = outerRequestHandler;
            return this;
        }

        public ChatNioSocketAcceptor build(){
            return new ChatNioSocketAcceptor(this);
        }

    }

    /**
     * 消息处理handler
     */
    @Sharable
    private class FinalChannelEventHandler extends SimpleChannelInboundHandler<Object> {

        /**
         * 该方法在有客户端连接的时候调用
         * @param channelHandlerContext
         * @param requetData
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object requetData) throws Exception {

            // 心跳响应直接返回，不再做处理
            if (requetData instanceof HeartbeatResponse) {
                return;
            }

            // 获取请求数据
            SendBody body = (SendBody)requetData;

            // 创建会话，保存channel
            MySession session = new MySession(channelHandlerContext.channel());

            RequestHandler handler = innerHandlerMap.get(body.getKey());

            /*
             * 如果有内置的特殊handler需要处理，则使用内置的
             */
            if (handler != null) {
                handler.process(session, body);
                return;
            }

            /*
             * 有业务层去处理其他的sentBody
             */
            outerRequestHandler.process(session, body);

        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            channelGroup.remove(ctx.channel().id().asShortText());

            MySession session = new MySession(ctx.channel());
            SendBody body = new SendBody();
            body.setKey(ChatConstant.CLIENT_CONNECT_CLOSED);
            outerRequestHandler.process(session, body);

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx){
            channelGroup.put(ctx.channel().id().asShortText(),ctx.channel());
        }
    }
}
