package com.scsse.workflow.util;

import com.scsse.workflow.entity.model.User;
import com.scsse.workflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alfred Fu
 * Created on 2019/9/16 7:55 下午
 */
@Component
public class UserUtil {
    private final UserRepository userRepository;

    @Autowired
    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Integer findUserIdByOpenid(String openId) {
        User result = userRepository.findByOpenid(openId);
        // This will throw null pointer exception
        // TODO(): throw custom exception
        return result.getUserId();
    }

    public Integer findLoginUserId() {
        User result = userRepository.findByOpenid(RequestUtil.getOpenId());
        // This will throw null pointer exception
        // TODO(): throw custom exception
        return result.getUserId();
    }
}