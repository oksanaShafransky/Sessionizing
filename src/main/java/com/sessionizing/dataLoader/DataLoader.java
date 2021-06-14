package com.sessionizing.dataLoader;

import com.sessionizing.data.PageView;
import com.sessionizing.utils.SessionizingConstants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataLoader {
    private static Logger logger = Logger.getLogger(DataLoader.class);
    //hash map of key - siteUrl, value - map of key - visitorId, value - list of PageViews
    private ConcurrentHashMap<String, HashMap<String, List<PageView>>> siteUrlHashMap = new ConcurrentHashMap();

    /**
     * This method load all files from the provided folder into the memory
     * and parse the data and then adds it to the list of PageView.
     */
    public void loadCSVData() {
        try {
            File folder = new File(SessionizingConstants.loadFilePath);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String line;
                    final BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i]));
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(String.valueOf(","));
                        PageView pageView = preparePageView(values);
                        if (pageView != null) {
                            //update hashMap for siteUrl
                            updateSiteUrlHashMap(pageView);
                        }
                    }
                }
            }
            logger.info("All data was loaded successfully.");
        } catch (IOException e){
            logger.error("Error on loadCSVData: " + e.getMessage());
        }
    }

    /**
     * This method prepare single PageView object from the line we read on the csv file.
     * @param values
     * @return PageView or null if error
     */
    private PageView preparePageView(String[] values) {
        //handle erroneous data (expected 4 strings)
        if(values.length != 4){
            logger.error("preparePageView got invalid page view");
            return null;
        }
        //valid data
        try {
            PageView pageView = new PageView();
            pageView.setVisitorId(values[0]);
            pageView.setSiteUrl(values[1]);
            pageView.setPageViewUrl(values[2]);
            pageView.setTimestamp(Long.valueOf(values[3]));
            return pageView;
        } catch (Exception e){
            logger.error("preparePageView catch exception.", e);
            return null;
        }
    }

    private void updateSiteUrlHashMap(PageView pageView){
        HashMap<String, List<PageView>> siteUrlEntry = siteUrlHashMap.get(pageView.getSiteUrl());
        if(siteUrlEntry == null) {
            Map<String, List<PageView>> visitorIdMap = new HashMap<>();
            List<PageView> pageViews = new ArrayList<>();
            pageViews.add(pageView);
            visitorIdMap.put(pageView.getVisitorId(), pageViews);
            siteUrlHashMap.put(pageView.getSiteUrl(), (HashMap<String, List<PageView>>) visitorIdMap);
        } else {
            List<PageView> visitorIdEntry = siteUrlEntry.get(pageView.getVisitorId());
            if(visitorIdEntry == null){
                visitorIdEntry = new ArrayList<>();
                visitorIdEntry.add(pageView);
                siteUrlEntry.put(pageView.getVisitorId(), visitorIdEntry);
            } else {
                visitorIdEntry.add(pageView);
            }
        }
    }

    public ConcurrentHashMap<String, HashMap<String, List<PageView>>> getSiteUrlMap(){
        return siteUrlHashMap;
    }
}
