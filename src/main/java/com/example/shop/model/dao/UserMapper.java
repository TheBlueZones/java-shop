package com.example.shop.model.dao;

import com.example.shop.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository/*告诉IDE，cancle the redline in UserserviceImpl*/
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String userName);

    /*tow paramter shuoud add annotation*/

     User selectLogin(@Param("userName") String userName, @Param("password") String password);
}