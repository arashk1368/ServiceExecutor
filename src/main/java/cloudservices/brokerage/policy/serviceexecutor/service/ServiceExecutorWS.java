/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.service;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.policy.policycommons.model.DAO.BaseDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import cloudservices.brokerage.policy.serviceexecutor.logic.ServiceExecutionException;
import cloudservices.brokerage.policy.serviceexecutor.logic.ServiceOperator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
@WebService(serviceName = "ServiceExecutorWS")
public class ServiceExecutorWS {

    private final static Logger LOGGER = Logger.getLogger(ServiceExecutorWS.class
            .getName());

    private void setupLoggers() throws IOException {
        LoggerSetup.setup(ResourceFileUtil.getResourcePath("log.txt"), ResourceFileUtil.getResourcePath("log.html"));
        LoggerSetup.log4jSetup(ResourceFileUtil.getResourcePath("log4j.properties"),
                ResourceFileUtil.getResourcePath("hibernate.log"));
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "executeService")
    public List executeService(@WebParam(name = "service") Service service,
            @WebParam(name = "initialState") State initialState,
            @WebParam(name = "goalState") State goalState)
            throws IOException, DAOException, ServiceExecutionException {
        setupLoggers();

        if (service == null) {
            throw new IllegalArgumentException("Service can not be null");
        }
        if (initialState == null) {
            throw new IllegalArgumentException("Initial state can not be null");
        }
        if (initialState.getParam("seeds") instanceof String) {
            List<String> seeds = new ArrayList<>();
            seeds.add((String) initialState.getParam("seeds"));
            HashMap<String, Object> params = new HashMap<>();
            params.put("seeds", seeds);
            initialState.setParams(params);
        }
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            BaseDAO.openSession(configuration);

            ServiceOperator operator = new ServiceOperator(initialState, goalState);
            Object result = operator.execute(service, initialState);

            //only for crawler services
            List<String> finalResult = new ArrayList<>();
            Map<String, Object> resultMap = (Map<String, Object>) result;
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                List<String> urls = (List<String>) entry.getValue();
                finalResult.addAll(urls);
            }
            return finalResult;
        } finally {
            BaseDAO.closeSession();
        }
    }
}
