package com.zy.chat.netty.server.service.impl;

import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.server.respository.SessionRespository;
import com.zy.chat.netty.server.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRespository respository;


    /**
     * 根据账号获取会话
     * @param account
     * @return
     */
    @Override
    public MySession get(String account) {
        return respository.get(account);
    }

    /**
     * 客户端连接是创建会话
     * @param newSession
     * @return
     */
    @Override
    public void save(MySession newSession) {
        respository.save(newSession);
    }

    /**
     * 客户端下线，移除会话
     * @param accout
     */
    @Override
    public void remove(String accout) {
        respository.remove(accout);
    }

    /**
     * 查询在线用户
     * @return
     */
    @Override
    public List<MySession> list() {
        return respository.findAll();
    }
}
