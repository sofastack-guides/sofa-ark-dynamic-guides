package io.sofastack.guides.master.controller;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import io.sofastack.guides.facade.TestFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: guolei.sgl (glmapper_2018@163.com) 2020/6/13 2:32 PM
 * @since:
 **/
@RestController
@RequestMapping("api")
public class MasterController {

    @SofaReference
    private TestFacade testFacade;

    @RequestMapping("master")
    public String master(){
        try {
            return testFacade.testMethod("MODULE PROVIDER");
        } catch (Throwable ex){
            return "THIS IS DEFAULT PROVIDER";
        }
    }
}
