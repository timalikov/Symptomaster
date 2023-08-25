package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.infra.dto.HistoryDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nikolay on 11/08/16.
 */
public class SymptomsHistoryMapper  implements RowMapper<HistoryDto> {


    @Override
    public HistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        HistoryDto history = new HistoryDto();

        history.setId(rs.getInt(1));
        history.setTransactionId(rs.getString(2));
        history.setAnswersStack(rs.getString(4));
        history.setResultId(rs.getInt(5));
        history.setTimestamp(rs.getDate(6));
        history.setEnabled(rs.getInt(7) == 1);
        return history;
    }
}
