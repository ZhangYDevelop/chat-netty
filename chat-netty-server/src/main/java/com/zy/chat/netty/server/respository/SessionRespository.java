package com.zy.chat.netty.server.respository;

import com.zy.chat.netty.sdk.model.MySession;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionRespository {

    private ConcurrentHashMap<String, MySession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 保存Session信息
     * @param session
     */
    public void  save(MySession session) {
        sessionMap.put(session.getAccount(), session);
    }

    public MySession get(String account) {
        return sessionMap.get(account);
    }

    public void  remove(String account) {
        sessionMap.remove(account);
    }

    public List<MySession> findAll() {
        return new LinkedList<>(sessionMap.values());
    }
}
