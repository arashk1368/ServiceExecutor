/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.service;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import java.io.IOException;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
@WebService(serviceName = "ServiceExecutorWS")
public class ServiceExecutorWS {

    private final static Logger LOGGER = Logger.getLogger(ServiceExecutorWS.class
            .getName());

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    private void setupLoggers() throws IOException {
        LoggerSetup.setup(ResourceFileUtil.getResourcePath("log.txt"), ResourceFileUtil.getResourcePath("log.html"));
        LoggerSetup.log4jSetup(ResourceFileUtil.getResourcePath("log4j.properties"),
                ResourceFileUtil.getResourcePath("hibernate.log"));
    }
}
