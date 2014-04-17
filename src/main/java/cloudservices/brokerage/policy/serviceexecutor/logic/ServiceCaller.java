/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.policy.serviceexecutor.logic;

import cloudservices.brokerage.policy.crawling_services.crawler4jservice.service.Crawler4JWS;
import cloudservices.brokerage.policy.crawling_services.crawler4jservice.service.Crawler4JWS_Service;
import cloudservices.brokerage.policy.crawling_services.websphinxservice.service.WebsphinxWS;
import cloudservices.brokerage.policy.crawling_services.websphinxservice.service.WebsphinxWS_Service;
import cloudservices.brokerage.policy.policycommons.model.entities.Service;
import cloudservices.brokerage.policy.policycommons.model.entities.State;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This Class needs to be changed to work dynamically. For test purposes it is
 * only working with crawler services!
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class ServiceCaller {

    private final static Logger LOGGER = Logger.getLogger(ServiceCaller.class
            .getName());

    public Object callService(Service toCall, State state) throws ServiceExecutionException {
        String wsdl = toCall.getWSDLURL();
        URL wsdlURL;
        try {
            wsdlURL = new URL(wsdl);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "Malformed wsdl url in service", ex);
            throw new ServiceExecutionException("Malformed wsdl url in service", ex);
        }

        Object param = state.getParam("seeds");
        if (!(param instanceof List)) {
            LOGGER.log(Level.SEVERE, "State does not contain seeds");
            throw new ServiceExecutionException("State does not contain seeds");
        }
        List<String> seeds = (List<String>) state.getParam("seeds");

        if (toCall.getName().contains("crawler4jFiltered")) {
            Object filterParam = state.getParam("filter");
            if (!(filterParam instanceof String)) {
                LOGGER.log(Level.SEVERE, "State does not contain filter and filtered crawler service performed");
                throw new ServiceExecutionException("State does not contain filter and filtered crawler service performed");
            }
            String filter = (String) filterParam;
            return performCrawler4jFiltered(seeds, filter, wsdlURL);

        } else if (toCall.getName().contains("crawler4j")) {
            return performCrawler4j(seeds, wsdlURL);

        } else if (toCall.getName().contains("websphinx")) {
            return performWebsphinx(seeds, wsdlURL);

        } else {
            LOGGER.log(Level.SEVERE, "Service to be called is not a crawler service");
            throw new ServiceExecutionException("Service to be called is not a crawler service");
        }
    }

    private List<String> performCrawler4jFiltered(List<String> seeds, String filter, URL wsdlURL) throws ServiceExecutionException {
        try {
            Crawler4JWS_Service service = new Crawler4JWS_Service(wsdlURL);
            Crawler4JWS port = service.getCrawler4JWSPort();
            return port.filteredCrawl(seeds, filter);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception in crawler4jFiltered service", ex);
            throw new ServiceExecutionException("Exception in crawler4jFiltered service", ex);
        }
    }

    private List<String> performCrawler4j(List<String> seeds, URL wsdlURL) throws ServiceExecutionException {
        try {
            Crawler4JWS_Service service = new Crawler4JWS_Service(wsdlURL);
            Crawler4JWS port = service.getCrawler4JWSPort();
            return port.crawl(seeds);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception in crawler4j service", ex);
            throw new ServiceExecutionException("Exception in crawler4j service", ex);
        }
    }

    private List<String> performWebsphinx(List<String> seeds, URL wsdlURL) throws ServiceExecutionException {
        try {
            WebsphinxWS_Service service = new WebsphinxWS_Service(wsdlURL);
            WebsphinxWS port = service.getWebsphinxWSPort();
            return port.crawl(seeds);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception in websphinx service", ex);
            throw new ServiceExecutionException("Exception in websphinx service", ex);
        }
    }
}
