package com.zy.chat.netty.server.service.impl;

import com.zy.chat.netty.server.dto.UserGroup;
import com.zy.chat.netty.server.respository.SessionRespository;
import com.zy.chat.netty.server.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private SessionRespository respository;

    @Override
    public List<UserGroup> getUserGroupList(String userName) {
        return respository.getUserGroupList(userName);
    }
}
