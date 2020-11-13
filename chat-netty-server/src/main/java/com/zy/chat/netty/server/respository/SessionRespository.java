package com.zy.chat.netty.server.respository;

import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.server.dto.UserGroup;
import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SessionRespository {

    /**
     * 用户群组信息
     */
    private  List<UserGroup> userGroups = new ArrayList<>();

    /**
     * 用户会话信息
     */
    private ConcurrentHashMap<String, MySession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 群组会话信息
     */
    private ConcurrentHashMap<String, DefaultChannelGroup> sessionGourp  = new ConcurrentHashMap<>();

    @PostConstruct
    public  void  init() {
        // 初始化用户群组信息
        UserGroup group = new UserGroup();
        group.setId("1");
        group.setGroupCode("qwert");
        group.setGroupName("宇哥三人组");
        group.setUserId(Long.valueOf(2));
        group.setUserName("beauty");
        group.setGroupAvatar("/thumbnail/temp/group.jpg");
        userGroups.add(group);

        group = new UserGroup();
        group.setId("2");
        group.setGroupCode("qwert");
        group.setGroupName("宇哥三人组");
        group.setUserId(Long.valueOf(3));
        group.setUserName("tony");
        group.setGroupAvatar("/thumbnail/temp/group.jpg");
        userGroups.add(group);

        group = new UserGroup();
        group.setId("3");
        group.setGroupCode("qwert");
        group.setGroupName("宇哥三人组");
        group.setUserName("baby");
        group.setGroupAvatar("/thumbnail/temp/group.jpg");
        group.setUserId(Long.valueOf(4));
        userGroups.add(group);
    }


    /**
     * 保存群ChannelGroup
     * @param groupName
     * @param channel
     */
    public void saveSessionGourp(String groupName, Channel channel) {
        if (!sessionGourp.containsKey(groupName)) {
            DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            channels.add(channel);
            sessionGourp.put(groupName, channels);
        } else  {
            sessionGourp.get(groupName).add(channel);
        }

    }

    /**
     * 根据群组名称获取ChannelGroup
     * @param groupCode
     * @return
     */
    public DefaultChannelGroup getSessionGroup(String groupCode) {
        return sessionGourp.get(groupCode);
    }
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

    public List<UserGroup> getUserGroupList(String userName) {
        return userGroups.stream().filter(item -> item.getUserName().equals(userName.toString())).collect(Collectors.toList());
    }
}
