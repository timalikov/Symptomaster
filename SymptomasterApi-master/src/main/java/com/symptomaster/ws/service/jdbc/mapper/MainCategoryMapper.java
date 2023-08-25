package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.ws.models.MainCategory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nikolay on 30/06/16.
 */
public class MainCategoryMapper implements RowMapper<MainCategory> {
    @Override
    public MainCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
        MainCategory category = new MainCategory();
        category.setCategoryId(rs.getInt(1));
        category.setCategoryName(rs.getString(2));
        return category;
    }
}
