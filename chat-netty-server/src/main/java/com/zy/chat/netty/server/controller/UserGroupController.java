package com.zy.chat.netty.server.controller;


import com.zy.chat.netty.server.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/userGroup")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping("/getUserGroupListUserName")
    public ResponseEntity getUserGroupListUserName(String userName) {
        return ResponseEntity.ok(userGroupService.getUserGroupList(userName));
    }
}
