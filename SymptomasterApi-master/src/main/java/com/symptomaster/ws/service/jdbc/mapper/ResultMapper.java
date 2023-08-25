package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.ws.models.Result;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nikolay on 29/06/16.
 */
public class ResultMapper implements RowMapper<Result> {
    @Override
    public Result mapRow(ResultSet rs, int rowNum) throws SQLException {
        Result result = new Result();
        result.setId(rs.getInt(1));
        result.setCause(rs.getString(2));
        result.setRecommendation(rs.getString(3));
        result.setEnabled(rs.getInt(4));
        result.setLinkToSymptom(rs.getInt(5));

        return result;
    }
}
