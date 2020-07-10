/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.zy.chat.netty.server.handler;


import com.zy.chat.netty.sdk.constant.ChatConstant;
import com.zy.chat.netty.sdk.hanlder.RequestHandler;
import com.zy.chat.netty.sdk.model.MySession;
import com.zy.chat.netty.sdk.model.SendBody;
import com.zy.chat.netty.server.service.SessionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;


/*
 * 断开连接，清除session
 * 
 */

@Component
public class SessionClosedHandler implements RequestHandler {

	@Resource
	private SessionService sessionService;
	
	@Override
	public void process(MySession ios, SendBody message) {
		Object quietly = ios.getAttribute(ChatConstant.KEY_QUIETLY_CLOSE);
		if (Objects.equals(quietly, true)) {
			return;
		}

		Object account = ios.getAttribute(ChatConstant.KEY_ACCOUNT);
		if (account == null) {
			return;
		}
		sessionService.remove(account.toString());
	}

}
