package com.zy.chat.netty.sdk.hanlder;

import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.sdk.model.SendBody;

/**
 * 请求处理接口
 */
public interface RequestHandler {

    /**
     * 处理收到客户端从长链接发送的数据
     * @param session
     * @param body
     */
    void process(MySession session, SendBody body);
}
