package com.symptomaster.ws.service.impl;

import com.symptomaster.infra.database.TableNames;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.enumeration.EnumUserType;
import com.symptomaster.ws.models.Result;
import com.symptomaster.ws.models.Symptom;
import com.symptomaster.ws.service.SymptomService;
import com.symptomaster.ws.service.jdbc.mapper.ResultMapper;
import com.symptomaster.ws.service.jdbc.mapper.SymptomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Maxim Kasyanov on 26/01/16.
 */
@Service
public class SymptomServiceImpl implements SymptomService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Symptom> getSymptomList(EnumUserType userType, EnumLocale locale) {
        StringBuilder query = new StringBuilder().append("SELECT  lsr._id, lsr.name,  lsr.description, lsr.question, lsr.precaution, lsr.suggestion ").
                append(" FROM symptoms as s, ").
                append(TableNames.getTableNameForLoc(TableNames.SYMPTOMS_LOC, locale) + " as lsr, front_symptoms_tree as tree ").
                append(" WHERE s.strings = lsr._id").
                append(" AND tree.symptom_id = lsr._id").
                append(" AND tree.user_type_id = ?");

        List<Symptom> symptomList = jdbcTemplate.query(query.toString(), new Object[]{userType.getId()}, new SymptomMapper());

        return symptomList;
    }

    @Override
    public Symptom getSymptomById(int id, EnumLocale locale) {
        StringBuilder query = new StringBuilder().append("SELECT s._id, lsr. name, lsr.description,lsr.question, lsr.precaution, ").
                append("lsr.suggestion, s. enabled ").append("FROM symptoms as s, ").
                append(TableNames.getTableNameForLoc(TableNames.SYMPTOMS_LOC, locale) + " as lsr ").
                append("WHERE s.strings = lsr._id ").
                append("AND s.enabled = 1 ").append(" AND s._id = ?");

        Symptom symptom = jdbcTemplate.queryForObject(query.toString(), new Object[]{id}, new SymptomMapper());
        return symptom;
    }

    @Override
    public List<Symptom> getSymptomChilds(int parentId, EnumLocale locale) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT tree.").append(TableNames.SYMPTOMS_TREE_COL_CHILD)
                .append(",loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_NAME)
                .append(",loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_DESCRIPTION)
                .append(",loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_QUESTION)
                .append(",loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_PRECAUTION)
                .append(",loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_SUGGESTION);

        query.append(" FROM ").append(TableNames.SYMPTOMS_TREE).append(" as tree ");

        query.append("INNER JOIN ").append(TableNames.SYMPTOMS).append(" as symptoms ");
        query.append("ON tree.").append(TableNames.SYMPTOMS_TREE_COL_CHILD)
                .append(" = symptoms.").append(TableNames.SYMPTOMS_COL_ID);
        query.append(" AND symptoms.").append(TableNames.SYMPTOMS_COL_ENABLED).append(" = 1 ");

        query.append("INNER JOIN ")
                .append(TableNames.getTableNameForLoc(TableNames.SYMPTOMS_LOC, locale)).append(" as loc_symptoms ")
                .append("ON symptoms.").append(TableNames.SYMPTOMS_COL_STRINGS).append("=loc_symptoms.").append(TableNames.SYMPTOMS_LOC_COL_ID);

        query.append(" WHERE tree.").append(TableNames.SYMPTOMS_TREE_COL_PARENT).append(" = ? ");
        query.append("AND tree.").append(TableNames.SYMPTOMS_TREE_COL_ENABLED).append(" = 1");

        List<Symptom> childs = jdbcTemplate.query(query.toString(), new Object[]{parentId}, new SymptomMapper());
        return childs;
    }

    @Override
    public Result getResultForSymptom(int symptomId, EnumLocale locale) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT results._id,")
                .append("loc_results.cause,")
                .append("loc_results.recommendation,")
                .append("results.enabled,")
                .append("results.link_to_symptom ")
                .append("FROM ").append(TableNames.RESULTS).append(" as results ");

        query.append("INNER JOIN ")
                .append(TableNames.getTableNameForLoc(TableNames.RESULTS_LOC, locale)).append(" as loc_results ")
                .append("ON results.").append(TableNames.RESULTS_COL_STRINGS).append("=loc_results.")
                .append(TableNames.RESULTS_LOC_COL_ID);

        query.append(" INNER JOIN ")
                .append(TableNames.RESULTS_TREE).append(" as tree ")
                .append("ON results.").append(TableNames.RESULTS_COL_ID).append("=tree.")
                .append(TableNames.RESULTS_TREE_COL_RESULT_ID).append(" AND results.").append(TableNames.RESULTS_COL_ENABLED)
                .append("=1 AND tree.").append(TableNames.RESULTS_TREE_COL_ENABLED).append("=1 ");

        query.append("WHERE tree.").append(TableNames.RESULTS_TREE_COL_SYMPTOM_ID).append("=? ORDER BY tree.")
                .append(TableNames.RESULTS_TREE_COL_PRIORITY);

        Result result;

        try {
            result = jdbcTemplate.queryForObject(query.toString(), new Object[]{symptomId}, new ResultMapper());
            result.setSymptomId(symptomId);
            // Spring throws an EmptyResultDataAccessException instead of returning a null when record not found
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    @Override
    public Result getResult(int resultId, EnumLocale locale) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT results._id,")
                .append("loc_results.cause,")
                .append("loc_results.recommendation,")
                .append("results.enabled,")
                .append("results.link_to_symptom ")
                .append("FROM ").append(TableNames.RESULTS).append(" as results ");

        query.append("INNER JOIN ")
                .append(TableNames.getTableNameForLoc(TableNames.RESULTS_LOC, locale)).append(" as loc_results ")
                .append("ON results.").append(TableNames.RESULTS_COL_STRINGS).append("=loc_results.")
                .append(TableNames.RESULTS_LOC_COL_ID);

        query.append(" INNER JOIN ")
                .append(TableNames.RESULTS_TREE).append(" as tree ")
                .append("ON results.").append(TableNames.RESULTS_COL_ID).append("=tree.")
                .append(TableNames.RESULTS_TREE_COL_RESULT_ID).append(" AND results.").append(TableNames.RESULTS_COL_ENABLED)
                .append("=1 AND tree.").append(TableNames.RESULTS_TREE_COL_ENABLED).append("=1 ");

        query.append(" WHERE results._id=?");

//        query.append("WHERE tree.").append(TableNames.RESULTS_TREE_COL_SYMPTOM_ID).append("=? ORDER BY tree.")
//                .append(TableNames.RESULTS_TREE_COL_PRIORITY);

        Result result;

        try {
            result = jdbcTemplate.queryForObject(query.toString(), new Object[]{resultId}, new ResultMapper());
            // Spring throws an EmptyResultDataAccessException instead of returning a null when record not found
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    @Override
    public boolean isSymptomBelongsToCategory(int symptomId, int categoryId) {
        String query = "SELECT main_category_id FROM front_symptoms_tree WHERE symptom_id = ?";

        try {
            List<Integer> ids = jdbcTemplate.queryForList(query, new Object[] {symptomId}, Integer.class);

            if(ids != null) {
                for(int id : ids) {
                    if(id == categoryId) return true;
                }
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
