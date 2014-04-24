/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.logic;

import cloudservices.brokerage.policy.policycommons.model.DAO.DAOException;
import cloudservices.brokerage.policy.policycommons.model.DAO.PolicyDAO;
import cloudservices.brokerage.policy.policycommons.model.entities.Policy;
import cloudservices.brokerage.policy.policycommons.model.entities.PolicyProposition;
import cloudservices.brokerage.policy.policycommons.model.entities.Proposition;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import cloudservices.brokerage.policy.policymanager.service.DAOException_Exception;
import cloudservices.brokerage.policy.policymanager.service.IOException_Exception;
import cloudservices.brokerage.policy.policymanager.service.PolicyManagerWS;
import cloudservices.brokerage.policy.policymanager.service.PolicyManagerWS_Service;
import cloudservices.brokerage.policy.serviceexecutor.utils.convertors.PolicyManagerServiceObjectsConvertor;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class Observer {

    private final static Logger LOGGER = Logger.getLogger(Observer.class
            .getName());
    private final PolicyDAO policyDAO;
    private final State initialState;
    private final State goalState;

    public Observer(State initialState, State goalState) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.policyDAO = new PolicyDAO();
    }

    public String observePolicy(String serviceLevel, State currentState, State nextState) throws DAOException, IOException {
        List<Policy> policies = policyDAO.getAll("Policy");
        Set<Proposition> availableConditions = currentState.getPropositions();
        Set<Proposition> availableEvents = nextState.getPropositions();

        boolean isApplicable;
        Set<Policy> applicablePolicies = new HashSet<>();
        for (Policy policy : policies) {
            if (policy.isValid()) {
                isApplicable = true;
                for (PolicyProposition pp : policy.getPolicyPropositions()) {
                    switch (pp.getType()) {
                        case CONDITION:
                            if (!availableConditions.contains(pp.getProposition())) {
                                isApplicable = false;
                            }
                            break;
                        case EVENT:
                            if (!availableEvents.contains(pp.getProposition())) {
                                isApplicable = false;
                            }
                            break;
                    }
                    if (!isApplicable) {
                        break;
                    }
                }
                if (isApplicable) {
                    applicablePolicies.add(policy);
                }
            }
        }
        String result = serviceLevel;
        if (!applicablePolicies.isEmpty()) {
            cloudservices.brokerage.policy.policymanager.service.Service applied;
            applied = applyPolicy(applicablePolicies, currentState, nextState, serviceLevel);
            result = applied.getServicesStr();
        }
        return result;
    }

    private cloudservices.brokerage.policy.policymanager.service.Service applyPolicy(Set<Policy> applicablePolicies, State currentState, State nextState, String serviceLevel)
            throws DAOException, IOException {
        try {
            PolicyManagerWS_Service service = new PolicyManagerWS_Service();
            PolicyManagerWS port = service.getPolicyManagerWSPort();
            List<cloudservices.brokerage.policy.policymanager.service.Policy> capplicablePolicies = PolicyManagerServiceObjectsConvertor.policyListConvert(applicablePolicies);
            cloudservices.brokerage.policy.policymanager.service.State ccurrentState = PolicyManagerServiceObjectsConvertor.convert(currentState);
            cloudservices.brokerage.policy.policymanager.service.State cnextState = PolicyManagerServiceObjectsConvertor.convert(nextState);
            cloudservices.brokerage.policy.policymanager.service.State cinitialState = PolicyManagerServiceObjectsConvertor.convert(initialState);
            cloudservices.brokerage.policy.policymanager.service.State cgoalState = PolicyManagerServiceObjectsConvertor.convert(goalState);
            cloudservices.brokerage.policy.policymanager.service.Service result = port.applyPolicy(capplicablePolicies, serviceLevel, ccurrentState, cnextState, cinitialState, cgoalState);
            return result;
        } catch (DAOException_Exception ex) {
            String msg = "Exception occured in calling Policy Manager Service from Service Executor Service";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new DAOException(msg, ex);
        } catch (IOException_Exception ex) {
            String msg = "Exception occured in calling Policy Manager Service from Service Executor Service";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new IOException(msg, ex);
        }
    }
}
