/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.utils.convertors;

import cloudservices.brokerage.policy.policymanager.service.Policy;
import cloudservices.brokerage.policy.policymanager.service.Proposition;
import cloudservices.brokerage.policy.policymanager.service.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class PolicyManagerServiceObjectsConvertor {
    
    public static Policy convert(cloudservices.brokerage.policy.policycommons.model.entities.Policy policy) {
        Policy ret = new Policy();
        ret.setId(policy.getId());
        ret.setName(policy.getName());
        ret.setPriority(policy.getPriority());
        return ret;
    }
    
    public static Proposition convert(cloudservices.brokerage.policy.policycommons.model.entities.Proposition proposition) {
        Proposition ret = new Proposition();
        ret.setId(proposition.getId());
        ret.setName(proposition.getName());
        ret.setValid(proposition.isValid());
        return ret;
    }
    
    public static List<Policy> policyListConvert(Set<cloudservices.brokerage.policy.policycommons.model.entities.Policy> applicablePolicies) {
        if (applicablePolicies == null) {
            return null;
        }
        List<Policy> policies = new ArrayList<>();
        for (cloudservices.brokerage.policy.policycommons.model.entities.Policy policy : applicablePolicies) {
            policies.add(convert(policy));
        }
        return policies;
    }
    
    public static List<Proposition> propositionListConvert(Set<cloudservices.brokerage.policy.policycommons.model.entities.Proposition> propositions) {
        if (propositions == null) {
            return null;
        }
        List<Proposition> ret = new ArrayList<>();
        for (cloudservices.brokerage.policy.policycommons.model.entities.Proposition proposition : propositions) {
            ret.add(convert(proposition));
        }
        return ret;
    }
    
    public static State convert(cloudservices.brokerage.policy.policycommons.model.entities.State currentState) {
        State state = new State();
        state.setNumber(currentState.getNumber());
        state.getPropositions().addAll(propositionListConvert(currentState.getPropositions()));
        return state;
    }
}
