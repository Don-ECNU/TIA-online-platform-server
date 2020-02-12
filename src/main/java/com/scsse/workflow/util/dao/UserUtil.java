package com.scsse.workflow.util.dao;

import com.scsse.workflow.constant.ErrorMessage;
import com.scsse.workflow.entity.model.User;
import com.scsse.workflow.handler.WrongUsageException;
import com.scsse.workflow.repository.UserRepository;
import com.scsse.workflow.util.mvc.RequestUtil;
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

    public User getLoginUser() {
        // This will throw null pointer exception
        return userRepository.findByOpenId(RequestUtil.getOpenId());
    }

    public Integer findUserIdByOpenid(String openId) throws WrongUsageException {
        User result = userRepository.findByOpenId(openId);
        if (result == null) {
            throw new WrongUsageException(ErrorMessage.USER_NOT_FOUND);
        }
        return result.getId();
    }

    public User getUserByUserId(Integer userId) throws WrongUsageException {
        User result = userRepository.findOne(userId);
        if (result == null) {
            throw new WrongUsageException(ErrorMessage.USER_NOT_FOUND);
        }
        return result;
    }

    public Integer getLoginUserId() throws WrongUsageException {
        User result = userRepository.findByOpenId(RequestUtil.getOpenId());
        // This will throw null pointer exception
        if (result == null)
            throw new WrongUsageException(ErrorMessage.USER_NOT_FOUND);
        return result.getId();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
