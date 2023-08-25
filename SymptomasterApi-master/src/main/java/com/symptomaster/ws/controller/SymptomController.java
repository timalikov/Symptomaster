package com.symptomaster.ws.controller;

import com.symptomaster.infra.enumeration.EnumLocale;
import com.symptomaster.infra.enumeration.EnumUserType;
import com.symptomaster.ws.models.Result;
import com.symptomaster.ws.models.Symptom;
import com.symptomaster.ws.service.SymptomService;
import com.symptomaster.ws.utils.http.DefaultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Maxim Kasyanov on 08/01/16.
 */
@RestController
@Api(value = "Symptom", description = "Operations with Symptom")
@RequestMapping("/symptom")
public class SymptomController extends BaseRestController {


    @Autowired
    SymptomService symptomService;

    @ApiOperation(value = "View all symptom")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomList(HttpServletResponse response,
                               @RequestParam(value = "type", defaultValue = "MALE") EnumUserType type,
                               @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale,
                               HttpServletRequest request) {

        DefaultResponse JSONreponse;

        try {
            List<Symptom> symptomList = symptomService.getSymptomList(type, locale);

            JSONreponse = new DefaultResponse(symptomList);
            renderJSON(response, JSONreponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Get symptom by id", notes = "Get exactly symptom by id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomById(HttpServletResponse response, @PathVariable int id,
                               @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONresponse;
        try {
            Symptom symptom = symptomService.getSymptomById(id, locale);

            JSONresponse = new DefaultResponse(symptom);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Get symptomChilds by parent id")
    @RequestMapping(value = "/{parentId}/childs", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomChilds(HttpServletResponse response, @PathVariable int parentId,
                                 @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONresponse;
        try {
            List<Symptom> childs = symptomService.getSymptomChilds(parentId, locale);

            JSONresponse = new DefaultResponse(childs);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }

    @ApiOperation(value = "Get result by symptomId")
    @RequestMapping(value = "/{symptomId}/result", method = RequestMethod.GET, produces = "application/json")
    public void getSymptomResult(HttpServletResponse response, @PathVariable int symptomId,
                                 @RequestParam(value = "locale", defaultValue = "RUS") EnumLocale locale) {

        DefaultResponse JSONresponse;
        try {
            Result result = symptomService.getResultForSymptom(symptomId, locale);

            if(result == null) {
                result = new Result();
            }

            JSONresponse = new DefaultResponse(result);
            renderJSON(response, JSONresponse.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            renderError(response, e);
        }
    }
}
