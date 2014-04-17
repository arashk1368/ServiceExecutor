/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cloudservices.brokerage.policy.serviceexecutor.logic;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class ServiceExecutionException extends Exception{
    public ServiceExecutionException(String message) {
        super(message);
    }
    
    public ServiceExecutionException(String message, Throwable cause){
        super(message,cause);
    }
}
