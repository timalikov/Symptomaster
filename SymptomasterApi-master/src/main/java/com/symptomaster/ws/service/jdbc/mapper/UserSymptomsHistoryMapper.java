package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.infra.dto.HistoryDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nikolay on 29/06/16.
 */
public class UserSymptomsHistoryMapper implements RowMapper<HistoryDto> {
    @Override
    public HistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        HistoryDto h = new HistoryDto();

        h.setId(rs.getInt(1));
        h.setTransactionId(rs.getString(2));
        h.setUserId(rs.getInt(3));
        h.setAnswersStack(rs.getString(4));
        h.setResultId(rs.getInt(5));
        h.setTimestamp(rs.getDate(6));
        h.setEnabled(rs.getInt(7));
        h.setEncryptedResultId(rs.getBytes(8));
        h.setEncryptedSerializedSequence(rs.getBytes(9));
        h.setAesIV(rs.getString(10));

        return h;
    }
}
