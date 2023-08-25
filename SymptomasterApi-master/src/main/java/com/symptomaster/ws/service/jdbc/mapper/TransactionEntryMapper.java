package com.symptomaster.ws.service.jdbc.mapper;

import com.symptomaster.ws.models.TransactionEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionEntryMapper implements RowMapper<TransactionEntry> {
    @Override
    public TransactionEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionEntry transactionEntry = new TransactionEntry();

        transactionEntry.setId(rs.getInt(1));
        transactionEntry.setTransactionId(rs.getString(2));
        transactionEntry.setResultIdEnc(rs.getBytes(3));
        transactionEntry.setSymptomSequenceEnc(rs.getBytes(4));
        transactionEntry.setIv(rs.getString(5));
        transactionEntry.setDate(rs.getTimestamp(6));

        return transactionEntry;
    }
}
