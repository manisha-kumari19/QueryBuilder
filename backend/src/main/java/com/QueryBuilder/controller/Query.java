package com.QueryBuilder.controller;

import com.QueryBuilder.dto.MethodConfigDTO;
import com.QueryBuilder.dto.RequestDTO;
import com.QueryBuilder.service.JsonService;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/")
public class Query {

    @Autowired
    private JsonService jsonService;
    @PostMapping("generate")
    public String generateScript(@RequestBody String  jsonString)
    {
        try {
            //converting json to DTO
            List<MethodConfigDTO> methodConfigDTOList = jsonService.mapJsonToDTO(jsonString);
            // Perform further processing with the list of MethodConfigDTOs

           jsonService.getAllMethods(methodConfigDTOList);
            return "success";
        } catch (Exception e) {
            // Handle the exception appropriately
            System.out.println(e);
            e.printStackTrace();
            return "failure";
        }
    }
}
