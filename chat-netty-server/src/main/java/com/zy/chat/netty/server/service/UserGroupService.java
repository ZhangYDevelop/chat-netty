package com.zy.chat.netty.server.service;

import com.zy.chat.netty.server.dto.UserGroup;

import java.util.List;

public interface UserGroupService {

    /**
     * 根据用户Id获取群组信息
     * @param userName
     * @return
     */
    List<UserGroup> getUserGroupList(String userName);
}
