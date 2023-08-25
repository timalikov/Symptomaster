package com.symptomaster.ws.service;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.enumeration.EnumUserType;
import com.symptomaster.ws.models.Result;
import com.symptomaster.ws.models.Symptom;

import java.util.List;

/**
 * Created by Maxim Kasyanov.
 */
public interface SymptomService {

    List<Symptom> getSymptomList(EnumUserType userType, EnumLocale locale);

    List<Symptom> getSymptomChilds(int parentId, EnumLocale locale);

    Result getResultForSymptom(int symptomId, EnumLocale locale);

    Result getResult(int resultId, EnumLocale locale);

    boolean isSymptomBelongsToCategory(int symptomId, int categoryId);

    Symptom getSymptomById(int id, EnumLocale locale);
}
