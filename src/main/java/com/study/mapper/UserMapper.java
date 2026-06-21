package com.study.mapper;
import com.study.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);
    @Insert("INSERT INTO user (username, password, nickname, email, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, 'user', 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")// 插入后，MyBatis 自动把数据库生成的 id 回填到 user 对象
    int insert(User user);
}