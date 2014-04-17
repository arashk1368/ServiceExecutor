/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.logic;

import cloudservices.brokerage.policy.policycommons.model.entities.State;
import java.util.logging.Logger;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class Observer {

    private final static Logger LOGGER = Logger.getLogger(Observer.class
            .getName());

    String policyObserve(String serviceLevel, State currentState, State nextState) {
        return serviceLevel;
    }
}
