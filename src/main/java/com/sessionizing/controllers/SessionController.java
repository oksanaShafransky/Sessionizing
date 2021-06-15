package com.sessionizing.controllers;

import com.sessionizing.utils.SessionizingUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@Scope("prototype")
@RequestMapping(value = "session")
public class SessionController {
    private static Logger logger = Logger.getLogger(SessionController.class);
    @Autowired
    SessionizingUtils sessionazingUtils;

    //API to get number of session for given siteUrl - run http://localhost:8080/session/numOfSession/{siteUrl}
    @RequestMapping(value = "numOfSessions/{siteUrl}", method = {RequestMethod.GET})
    public @ResponseBody
    long getNumOfSessions(@PathVariable String siteUrl) {
        long numOfSessions = sessionazingUtils.getNumOfSessions(siteUrl);
        String msg = numOfSessions == 0 ? "No session data found for siteUrl " + siteUrl : "Num sessions for site " + siteUrl + " = " + numOfSessions;
        logger.info(msg);
        return numOfSessions;
    }

    //API to get median of sessions length (in seconds) for given siteUrl - run http://localhost:8080/session/medianSessionLength/{siteUrl}
    @RequestMapping(value = "medianSessionLength/{siteUrl}", method = {RequestMethod.GET})
    public @ResponseBody
    double getMedianSessionLength(@PathVariable String siteUrl) {
        double num = sessionazingUtils.getMedianSessionLength(siteUrl);
        String msg = num == -1 ? "No data found for siteUrl " + siteUrl : "Median session length for siteUrl " + siteUrl + " = " + num;
        logger.info(msg);
        return num;
    }

    //API to get number of unique visited sites for given visitor id - run http://localhost:8080/session/numOfUniqueVisitedSites/{visitorId}
    @RequestMapping(value = "numOfUniqueVisitedSites/{visitorId}", method = {RequestMethod.GET})
    public @ResponseBody
    long getNumOfUniqueVisitedSites(@PathVariable String visitorId) {
        long num = sessionazingUtils.getNumOfUniqueVisitedSites(visitorId);
        String msg = num == 0 ? "No data found for visitor id " + visitorId : "Num of unique sites for visitor id " + visitorId + " = " + num;
        logger.info(msg);
        return num;
    }
}
