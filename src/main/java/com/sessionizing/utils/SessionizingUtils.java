package com.sessionizing.utils;


import com.sessionizing.SessionizingApplication;
import com.sessionizing.data.PageView;
import com.sessionizing.data.Session;
import com.sessionizing.dataLoader.DataLoader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class SessionizingUtils {
    private static Logger logger = Logger.getLogger(SessionizingUtils.class);
    DataLoader dataLoader = SessionizingApplication.dataLoader;

    //find all sessions with siteUrl and count them.
    public long getNumOfSessions(String siteUrl){
        Set<Session> sessions = prepareSessionDataBySiteUrl(siteUrl);
        return sessions.stream().count();
    }

    //find all session with siteUrl, sort them by session length,
    //find the median value of the session length in the middle of the data range.
    //in case number of sessions is even, take mean of two middle values.
    //in case no siteUrl found in the sessions, return -1 (not found).
    public double getMedianSessionLength(String siteUrl){
        List<Session> sessions = prepareSessionDataBySiteUrl(siteUrl).stream().collect(Collectors.toList());
        if(sessions != null && sessions.size() > 0) {
            sessions.stream().sorted(Comparator.comparing(Session::getSessionLength));
            int medianIndex = sessions.size() / 2;
            return sessions.size() % 2 == 0 ?
                    (sessions.get(medianIndex).getSessionLength() + sessions.get(medianIndex + 1).getSessionLength()) / 2 :
                    sessions.get(medianIndex).getSessionLength();
        }
        //not found
        return -1;
    }

    //calc number of visited sites for visitor id without duplication
    public long getNumOfUniqueVisitedSites(String visitorId) {
        Set<String> pages = new HashSet<>();
        List<PageView> pageViewsList = dataLoader.getVisitorIdMap().get(visitorId);
        Set<String> uniqueSiteOfVisitor = pageViewsList.stream().map(PageView::getSiteUrl).collect(Collectors.toSet());
        return uniqueSiteOfVisitor.size();
    }

    /**
     * Prepare session data from the page view list.
     * If no session found in set of sessions with visitor id, site url and timestamp of prev not longer than 30 min,
     * Create new session with session length = 0.
     * Otherwise, update existing session with new page view and session length.
     */
    public Set<Session> prepareSessionDataBySiteUrl(String siteUrl) {
        List<PageView> pageViewList = dataLoader.getSiteUrlMap().get(siteUrl);
        pageViewList.sort(Comparator.comparing(PageView::getTimestamp));
        Set<Session> sessions = new HashSet<>();
        try {
            if(pageViewList != null && !pageViewList.isEmpty()) {
                 pageViewList.stream().forEach(pageView -> {
                        Session session = findSession(pageView, sessions);
                        if (session != null) {
                            updateSession(session, pageView);
                        } else {
                            addSession(pageView, sessions);
                        }
                });
                logger.info("Session data preparation was completed successfully.");
            } else {
                logger.info("No siteUrl "  + siteUrl + " found");
            }
        } catch (Exception e){
            logger.error("Error on prepareSessionData.", e);
        }
        return sessions;
    }

    private void addSession(PageView page, Set<Session> sessions) {
        Session session = new Session();
        session.setVisitorId(page.getVisitorId());
        session.setSiteUrl(page.getSiteUrl());
        session.setSessionStart(page.getTimestamp());
        List<PageView> pageViews = new ArrayList<>();
        pageViews.add(page);
        session.setPageViewList(pageViews);
        session.setSessionLength(0);
        session.setPageViewCount(1);
        sessions.add(session);
    }

    private void updateSession(Session session, PageView page) {
        session.setSessionLastPageTimestamp(page.getTimestamp());
        session.getPageViewList().add(page);
        session.setSessionLength(session.getSessionLastPageTimestamp() - session.getSessionStart());
        session.setPageViewCount(session.getPageViewCount()+1);
    }

    private Session findSession(PageView pageView, Set<Session> sessions){
        AtomicReference<Session> session = new AtomicReference<>();
        sessions.stream().forEach(entry -> {
            if (entry.getVisitorId().equals(pageView.getVisitorId()) &&
                    entry.getSiteUrl().equals(pageView.getSiteUrl()) &&
                    pageView.getTimestamp() <= (entry.getSessionStart() + SessionizingConstants.HALF_HOUR)) {
                session.set(entry);
                return;
            }
        });
        return session.get();
    }

}
