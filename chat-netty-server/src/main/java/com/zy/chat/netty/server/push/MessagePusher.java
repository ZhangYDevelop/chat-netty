package com.zy.chat.netty.server.push;

import com.zy.chat.netty.sdk.model.Message;

public interface MessagePusher {

    /*
     * 向用户发送消息
     *
     * @param msg
     */
    public void push(Message msg);
}
