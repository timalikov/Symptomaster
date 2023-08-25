package com.symptomaster.ws.service.impl;

import com.symptomaster.infra.database.TableNames;
import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.ws.models.MainCategory;
import com.symptomaster.ws.models.Symptom;
import com.symptomaster.ws.service.CategoryService;
import com.symptomaster.ws.service.jdbc.mapper.MainCategoryMapper;
import com.symptomaster.ws.service.jdbc.mapper.SymptomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nikolay on 30/06/16.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void setBabySymptomFlags(List<MainCategory> categories) {
        if(categories != null) {
            for (MainCategory mainCategory : categories) {
                mainCategory.setBabySymptom(mainCategory.getCategoryId() == 9);
            }
        }
    }

    @Override
    public List<MainCategory> getFrontCategoriesForUserType(int userType, EnumLocale locale) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(TableNames.MAIN_CATEGORIES_COL_ID).append(", ").
                append(" main_category_loc.").append(TableNames.MAIN_CATEGORIES_LOC_COL_NAME);

        query.append(" FROM ").append(TableNames.FRONT_SYMPTOMS_TREE).append(", ")
                .append(TableNames.MAIN_CATEGORIES).append(", ")
                .append(TableNames.getTableNameForLoc(TableNames.MAIN_CATEGORIES_LOC, locale)).append(" as main_category_loc ");

        query.append("WHERE ")
                .append(TableNames.FRONT_SYMPTOMS_TREE_COL_MAIN_CATEGORY_ID).append(" = ").append(TableNames.MAIN_CATEGORIES_COL_ID)
                .append(" AND ").append(TableNames.MAIN_CATEGORIES_COL_STRINGS_ID).append(" = main_category_loc.")
                .append(TableNames.MAIN_CATEGORIES_LOC_COL_ID)
                .append(" AND ").append(TableNames.FRONT_SYMPTOMS_TREE_COL_ENABLED).append(" = 1")
                .append(" AND ").append(TableNames.FRONT_SYMPTOMS_TREE_COL_USER_TYPE_ID).append(" = ?")
                .append(" GROUP BY ").append(TableNames.MAIN_CATEGORIES_COL_ID)
                .append(", main_category_loc.").append(TableNames.MAIN_CATEGORIES_LOC_COL_NAME)
                .append(" ORDER BY ").append(TableNames.MAIN_CATEGORIES_COL_ID);

        List<MainCategory> categories = jdbcTemplate.query(query.toString(), new Object[]{userType}, new MainCategoryMapper());
        setBabySymptomFlags(categories);
        return categories;
    }

    @Override
    public List<Symptom> getFrontSymptoms(int userType, int categoryId, EnumLocale locale) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT s._id, ls.name, ls.description, ls.question, ls.precaution, ls.suggestion ")
                .append(" FROM front_symptoms_tree as fst, symptoms as s, ")
                .append(TableNames.getTableNameForLoc(TableNames.SYMPTOMS_LOC, locale)).append(" as ls")
                .append(" WHERE fst.symptom_id = s._id ")
                .append(" AND s.strings = ls._id")
                .append(" AND fst.user_type_id = ?")
                .append(" AND fst.main_category_id = ?")
                .append(" AND fst.enabled = 1")
                .append(" ORDER BY fst.priority");

        List<Symptom> symptoms = jdbcTemplate.query(query.toString(), new Object[] {userType, categoryId}, new SymptomMapper());
        return symptoms;
    }

    @Override
    public List<MainCategory> getFrontCategoriesForUserTypeWithSymptoms(int userType, EnumLocale locale) {
        List<MainCategory> allCategories = getFrontCategoriesForUserType(userType, locale);

        // TODO: see if we can make only one query to DB instead of looping
        for (MainCategory category : allCategories) {
            List<Symptom> symptomList = getFrontSymptoms(userType, category.getCategoryId(), locale);

            category.setSymptoms(symptomList);
        }

        return allCategories;
    }
}
