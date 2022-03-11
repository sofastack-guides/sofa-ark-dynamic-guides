package io.sofastack.dynamic.provider.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: guolei.sgl (glmapper_2018@163.com) 2020/6/13 2:36 PM
 * @since:
 **/
@RestController
@RequestMapping("api")
public class ModuleController {
    @RequestMapping("module")
    public String master(){
        return "SUCCESS FROM MODULE";
    }
}
