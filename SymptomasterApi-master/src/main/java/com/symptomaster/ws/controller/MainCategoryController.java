package com.symptomaster.ws.controller;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.enumeration.EnumUserType;
import com.symptomaster.ws.models.MainCategory;
import com.symptomaster.ws.models.Symptom;
import com.symptomaster.ws.service.CategoryService;
import com.symptomaster.ws.utils.http.DefaultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by nikolay on 30/06/16.
 */
@RestController
@Api(value = "MainCategory", description = "Operations with front categories")
@RequestMapping("/front-category")
public class MainCategoryController extends BaseRestController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "Get frontCategories for userType")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public void getFrontCategoriesFourUserType(HttpServletResponse response,
                                               @RequestParam(value = "type", defaultValue = "MALE") EnumUserType type,
                                               @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONresponse;
        try {
            List<MainCategory> categories = categoryService.getFrontCategoriesForUserType(type.getId(), locale);

            JSONresponse = new DefaultResponse(categories);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Get symptoms by categoryId")
    @RequestMapping(value = "{categoryId}/symptoms", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomsForCategory(HttpServletResponse response,
                                               @PathVariable int categoryId,
                                               @RequestParam(value = "type", defaultValue = "MALE") EnumUserType type,
                                               @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONresponse;
        try {

            List<Symptom> symptoms = categoryService.getFrontSymptoms(type.getId(), categoryId, locale);

            JSONresponse = new DefaultResponse(symptoms);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Get all categories and symptoms")
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public void getCategoriesAndSymptoms(HttpServletResponse response,
                                         @RequestParam(value = "type", defaultValue = "MALE") EnumUserType type,
                                         @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONreponse;

        try {
            List<MainCategory> mainCategories = categoryService.getFrontCategoriesForUserTypeWithSymptoms(type.getId(), locale);

            JSONreponse = new DefaultResponse(mainCategories);
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }
}
