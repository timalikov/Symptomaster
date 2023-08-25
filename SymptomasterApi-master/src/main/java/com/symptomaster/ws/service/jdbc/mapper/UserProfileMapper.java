package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.infra.enumeration.EnumUserGender;
import com.symptomaster.ws.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getInt(1));
        user.setEmail(rs.getString(2));
        user.setFirstName(rs.getString(3));
        user.setMiddleName(rs.getString(4));
        user.setLastName(rs.getString(5));
        user.setGender(EnumUserGender.getById(rs.getInt(6)));
        user.setDob(rs.getDate(7));

        return user;
    }
}
