package com.zy.chat.netty.sdk.coder;

import com.google.protobuf.InvalidProtocolBufferException;
import com.zy.chat.netty.sdk.model.SendBody;
import com.zy.chat.netty.sdk.model.Transportable;
import com.zy.chat.netty.sdk.model.proto.SentBodyProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接受消息解码
 */
public class MessageDecoder extends SimpleChannelInboundHandler<Object> {
    private final static String URI = "ws://localhost:%d";

    private static final ConcurrentHashMap<String, WebSocketServerHandshaker> handShakerMap = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {

        if (msg instanceof FullHttpRequest) {
            handleHandshakeRequest(ctx, (FullHttpRequest) msg);
        }
        if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHandshakeRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

        int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(String.format(URI,port), null, false);

        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);

        handShakerMap.put(ctx.channel().id().asLongText(), handShaker);

        handShaker.handshake(ctx.channel(), req);
    }



    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws InvalidProtocolBufferException {

        if (frame instanceof CloseWebSocketFrame) {
            handlerCloseWebSocketFrame(ctx, (CloseWebSocketFrame) frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            handlerPingWebSocketFrame(ctx, (PingWebSocketFrame) frame);
            return;
        }

        handlerBinaryWebSocketFrame(ctx, (BinaryWebSocketFrame) frame);
    }

    private void handlerCloseWebSocketFrame(ChannelHandlerContext ctx, CloseWebSocketFrame frame){
        WebSocketServerHandshaker handShaker = handShakerMap.get(ctx.channel().id().asLongText());
        handShaker.close(ctx.channel(), frame.retain());
    }

    private void handlerPingWebSocketFrame(ChannelHandlerContext ctx, PingWebSocketFrame frame){
        ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
    }

    private void handlerBinaryWebSocketFrame(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws InvalidProtocolBufferException {
        byte[] data = new byte[frame.content().readableBytes()];
        frame.content().readBytes(data);

        SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
        SendBody body = new SendBody();
        body.setKey(bodyProto.getKey());
        body.setTimestamp(bodyProto.getTimestamp());
        body.putAll(bodyProto.getDataMap());
        ctx.fireChannelRead(body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        LOGGER.warn("Exception caught",cause);
    }
}
