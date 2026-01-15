package com.smartlearning.service;

import com.smartlearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据学号查找用户
     * @param studentId 学号
     * @return 用户
     */
    User findByStudentId(String studentId);
}