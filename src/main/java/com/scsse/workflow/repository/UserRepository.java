package com.scsse.workflow.repository;

import com.scsse.workflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Alfred Fu
 * Created on 2019-02-19 20:06
 */
public interface UserRepository extends JpaRepository<User,Integer> {
}