package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.infra.enumeration.EnumUserGender;
import com.symptomaster.ws.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nikolay on 29/06/16.
 */
public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt(1));
        user.setEmail(rs.getString(2));
        user.setPassword(rs.getString(3));
        user.setSalt(rs.getString(4));
        user.setEncryptionSalt(rs.getString(5));
        user.setFirstName(rs.getString(6));
        user.setMiddleName(rs.getString(7));
        user.setLastName(rs.getString(8));
        user.setGender(EnumUserGender.getById(rs.getInt(9)));
        user.setDob(rs.getDate(10));
        return user;
    }
}
