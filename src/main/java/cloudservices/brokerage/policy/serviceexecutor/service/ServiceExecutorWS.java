/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.service;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.policy.policycommons.model.DAO.BaseDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.DAO.PropositionDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.ServiceDAO;
import cloudservices.brokerage.policy.policycommons.model.entities.Proposition;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import cloudservices.brokerage.policy.serviceexecutor.logic.ServiceExecutionException;
import cloudservices.brokerage.policy.serviceexecutor.logic.ServiceOperator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public Object executeService(@WebParam(name = "service") Service service,
            @WebParam(name = "initialState") State initialState)
            throws IOException, DAOException, ServiceExecutionException {
        setupLoggers();

//        if (service == null) {
//            throw new IllegalArgumentException("Service can not be null");
//        }
//        if (initialState == null) {
//            throw new IllegalArgumentException("Initial state can not be null");

        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            BaseDAO.openSession(configuration);

            //****************SAMPLE DATA******************
            ServiceDAO serviceDAO = new ServiceDAO();
            service = serviceDAO.getById(4L);
            PropositionDAO pDAO = new PropositionDAO();
            Set<Proposition> initials = new HashSet<>();
            initials.add(pDAO.getById(7L)); //Filter Available
            initials.add(pDAO.getById(8L)); //Seeds Available
            HashMap<String, Object> params = new HashMap<>();
            List<String> seeds = new ArrayList<>();
            seeds.add("http://www.arashkhodadadi.com/");
            params.put("seeds", seeds);
            String filter = ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$";
            params.put("filter", filter);
            initialState = new State();
            initialState.setNumber(0);
            initialState.setPropositions(initials);
            initialState.setParams(params);

            ServiceOperator operator = new ServiceOperator();
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
