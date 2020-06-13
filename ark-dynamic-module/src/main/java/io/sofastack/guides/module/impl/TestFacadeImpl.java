package io.sofastack.guides.module.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import io.sofastack.guides.facade.TestFacade;
import org.springframework.stereotype.Service;

/**
 * @author: guolei.sgl (glmapper_2018@163.com) 2020/6/13 2:30 PM
 * @since:
 **/
@SofaService
@Service
public class TestFacadeImpl implements TestFacade {

    public String testMethod(String message) {
        return "THIS IS " + message;
    }
}
