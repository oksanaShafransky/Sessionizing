package com.sessionazing;

import com.sessionizing.SessionizingApplication;
import com.sessionizing.dataLoader.DataLoader;
import com.sessionizing.utils.SessionizingUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SessionizingApplication.class, DataLoader.class})
class SessionizingApplicationTests {
	private static Logger logger = Logger.getLogger(SessionizingApplicationTests.class);
	@Autowired
	SessionizingUtils sessionizingUtils;
	String visitorId = "visitor_1752";
	String siteUrl = "www.s_6.com";

	@BeforeAll
	public static void beforeTest(){
		SessionizingApplication.loadData();
	}
	@Test
	public void testNumOfSessionsPerSiteUrl(){
		long num = sessionizingUtils.getNumOfSessions(siteUrl);
		long expectedNum = 3113;
		assertEquals("testNumOfSessionsPerSiteUrl expected num = " + expectedNum, expectedNum, num);
	}
	@Test
	public void testMedianOfSessionLengthPerSiteUrl(){
		double num = sessionizingUtils.getMedianSessionLength(siteUrl);
		double expectedNum = 2038;
		assertEquals("testNumOfSessionsPerSiteUrl expected num = " + expectedNum, expectedNum, num);
	}

	@Test
	public void testNumOfUniqueSitesPerVisitorId(){
		long num = sessionizingUtils.getNumOfUniqueVisitedSites(visitorId);
		long expectedNum = 4;
		assertEquals("testNumOfUniqueSitesPerVisitorId expected num = " + expectedNum, expectedNum, num);
	}
}
