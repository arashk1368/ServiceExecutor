/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.logic;

import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.DAO.PropositionDAO;
import cloudservices.brokerage.policy.policycommons.model.DAO.ServiceDAO;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.ServiceProposition;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class StateManager {

    private final static Logger LOGGER = Logger.getLogger(StateManager.class
            .getName());
    private PropositionDAO propositionDAO;
    private ServiceDAO serviceDAO;

    public StateManager() {
        propositionDAO = new PropositionDAO();
        serviceDAO = new ServiceDAO();
    }

    public State peekNextState(String serviceLevelStr, State currentState)
            throws ServiceExecutionException, DAOException {
        State next = new State();
        String[] serviceIds = serviceLevelStr.split(",");
        if (serviceIds.length == 0) {
            LOGGER.log(Level.SEVERE, "Services string is not well defined");
            throw new ServiceExecutionException("Services string is not well defined");
        }
        next.setPropositions(new HashSet(currentState.getPropositions()));
        for (String idStr : serviceIds) {
            Service service = serviceDAO.getById(Long.parseLong(idStr));
            Set<ServiceProposition> sps = service.getServicePropositions();
            for (ServiceProposition serviceProposition : sps) {
                if (serviceProposition.isOutput()) {
                    next.addProposition(serviceProposition.getProposition());
                }
            }
        }
        next.setParams(new HashMap(currentState.getParams()));

        return next;
    }

    /**
     * This needs to be changed to work dynamically. For test purposes it is
     * only working with crawler services!
     */
    public void addResultToState(State next, Object result) throws ServiceExecutionException {
        try {
            List<String> finalResult = new ArrayList<>();
            Map<String, Object> resultMap = (Map<String, Object>) result;
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                List<String> urls = (List<String>) entry.getValue();
                finalResult.addAll(urls);
            }
            next.addParam("seeds", finalResult);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "The service is only working with crawler services", ex);
            throw new ServiceExecutionException("The service is only working with crawler services", ex);
        }
    }
}
