package com.QueryBuilder.controller;

import com.QueryBuilder.dto.MethodConfigDTO;
import com.QueryBuilder.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/")
public class Query {


    @Autowired
    private ScriptService scriptService;
    @PostMapping("generate")
    public String generateScript(@RequestBody String  jsonString)
    {
        try {
            //converting json to DTO
            List<MethodConfigDTO> methodConfigDTOList = scriptService.mapJsonToDTO(jsonString);
            // Perform further processing with the list of MethodConfigDTOs
            String  script =scriptService.getAllMethods(methodConfigDTOList);
            return script;
        } catch (Exception e) {
            // Handle the exception appropriately
            System.out.println(e);
            e.printStackTrace();
            return "failure";
        }
    }
}
