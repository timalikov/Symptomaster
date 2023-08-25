package com.symptomaster.ws.service.jdbc.mapper;


import com.symptomaster.ws.models.Symptom;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Maxim Kasyanov on 11/02/16.
 */
public class SymptomMapper implements RowMapper<Symptom> {

    @Override
    public Symptom mapRow(ResultSet resultSet, int rowNum) throws SQLException {


        Symptom symptom = new Symptom();
        symptom.setId(resultSet.getInt(1));
        symptom.setName(resultSet.getString(2));
        symptom.setDescription(resultSet.getString(3));
        symptom.setQuestion(resultSet.getString(4));
        symptom.setPrecaution(resultSet.getString(5));
        symptom.setSuggestion(resultSet.getString(6));

        return symptom;
    }


}
