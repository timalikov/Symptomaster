package com.symptomaster.ws.service;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.ws.models.MainCategory;
import com.symptomaster.ws.models.Symptom;

import java.util.List;

/**
 * Created by nikolay on 30/06/16.
 */
public interface CategoryService {
    List<MainCategory> getFrontCategoriesForUserType(int userType, EnumLocale locale);

    List<Symptom> getFrontSymptoms(int userType, int categoryId, EnumLocale locale);

    List<MainCategory> getFrontCategoriesForUserTypeWithSymptoms(int userType, EnumLocale locale);
}
