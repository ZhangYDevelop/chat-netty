package com.zy.chat.netty.server.service;

import com.zy.chat.netty.sdk.model.MySession;
import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.List;

/**
 * 会话管理服务接口
 */
public interface SessionService {

    /**
     * 保存群ChannelGroup
     * @param account
     */
    void saveSessionGourp(String account,Channel channel) ;

    /**
     * 根据群组名称获取ChannelGroup
     * @param groupCode
     * @return
     */
     DefaultChannelGroup getSessionGroup(String groupCode);

    /**
     * 根据账号获取会话
     * @param account
     * @return
     */
    MySession get(String account);

    /**
     * 客户端连接是创建会话
     * @param newSession
     * @return
     */
    void save(MySession newSession);

    /**
     * 客户端下线，移除会话
     * @param accout
     */
    void remove(String accout);

    List<MySession> list();
}
