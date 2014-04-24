/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.logic;

import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.DAO.ServiceDAO;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class ServiceOperator {

    private final static Logger LOGGER = Logger.getLogger(ServiceOperator.class
            .getName());
    private ServiceDAO serviceDAO;
    private Observer observer;
    private ServiceCaller caller;
    private StateManager stateManager;
    private final State initialState;
    private final State goalState;

    public ServiceOperator(State initialState, State goalState) {
        this.initialState = initialState;
        this.goalState = goalState;
        serviceDAO = new ServiceDAO();
        observer = new Observer(initialState, goalState);
        caller = new ServiceCaller();
        stateManager = new StateManager();
    }

    public Object execute(Service service, State state)
            throws DAOException, ServiceExecutionException, IOException {
        if (service.getNumberOfServiceLevels() < 1) {
            LOGGER.log(Level.SEVERE, "There is no service to execute");
            throw new ServiceExecutionException("There is no service to execute");
        }
        String servicesStr = service.getServicesStr();
        String[] servicesLevels = servicesStr.split("-");
        if (service.getNumberOfServiceLevels() == 1) {
            if (servicesLevels.length != 1) {
                LOGGER.log(Level.SEVERE, "Services string does not match number of levels");
                throw new ServiceExecutionException("Services string does not match number of levels");
            }
            String[] levelStr = servicesLevels[0].split(",");
            if (levelStr.length > 1) {
                return executeServiceLevel(levelStr, state);
            } else if (levelStr.length == 1) {
                Service toCall = serviceDAO.getById(Long.parseLong(levelStr[0]));
                return caller.callService(toCall, state);
            }
            LOGGER.log(Level.SEVERE, "Services string is not well defined");
            throw new ServiceExecutionException("Services string is not well defined");
        }
        boolean finished = false;
        int level = 0;
        State currentState = state;
        Object result = null;
        while (!finished) {
            String serviceLevel = servicesLevels[level];
            State next = stateManager.peekNextState(serviceLevel, currentState);
            String policyObserved = observer.observePolicy(serviceLevel, currentState, next);
            if (policyObserved.compareTo(serviceLevel) != 0) {
                Service temp = new Service();
                temp.setServicesStr(policyObserved);
                Object tempResult = execute(temp, currentState);
                if (!(tempResult instanceof Map)) {
                    HashMap<String, Object> tmp = new HashMap<>();
                    tmp.put(service.getName() + "-" + service.getId(),
                            tempResult);
                    return tmp;
                }
                return tempResult;
            } else {
                result = executeServiceLevel(servicesLevels[level].split(","), currentState);
                level++;
                if (level >= servicesLevels.length) {
                    finished = true;
                } else {
                    stateManager.addResultToState(next, result);
                    currentState = next;
                }
            }
        }
        return result;
    }

    private Object executeServiceLevel(String[] level, State currentState)
            throws DAOException, ServiceExecutionException, IOException {
        HashMap<String, Object> result = new HashMap<>();
        for (String idStr : level) {
            Long id = Long.parseLong(idStr);
            Service service = serviceDAO.getById(id);
            result.put(service.getName() + "-" + service.getId(),
                    execute(service, currentState));
        }
        return result;
    }
}
