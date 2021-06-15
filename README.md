#Sessionizing
1. Set up and run instructions:
a. Clone the project to the development environment (IntelliJ) - https://github.com/oksanaShafransky/Sessionizing.
b. Run locally SessionizingApplication.java under /sessionazing/src/main/java/com/sessionizing (make sure you are running with Java 8).
Right-click on the class and select Run "SessionizingApplication".

After the application is up, there are 3 available APIs to run from POSTMAN or Browser:
a. http://localhost:9090/session/numOfSessions/{siteUrl} - like http://localhost:9090/session/numOfSessions/www.s_6.com
Expected to get number of sessions for selected site url. 
b. http://localhost:9090/session/numOfUniqueVisitedSites/{visitorId} - like http://localhost:9090/session/numOfUniqueVisitedSites/visitor_1752
Expected to get number of unique visited sites by provided visitor id.
c. http://localhost:9090/session/medianSessionLength/{siteUrl} - http://localhost:9090/session/medianSessionLength/www.s_4.com
Expected to get median of session length for provided site url.

2. Description of the solution:
In this solution I used Spring Boot framework to run web application using Apache tomcat server.
The code is written using Java 8 capabilities.
On application initialization, DataLoader class loads the data files located under /resources/static folder into Map that clusters the data
a. By siteUrl
b. By visitorId
This kind of data storage enables to get data for specific siteUrl by time complexity O(1), space complexity O(n).
The application uses 2 kind of objects:
a. PageView  - to describe single page of a site that was visited by a visitor at some timestamp.
b. Session - object that unites group of page views of a single visitor to a single site such that the time between every two successive page views is not longer than 30 minutes.
This object contains data for specific site, visitor id, session start, session length, list of PageView related to the specific session.

While running of the APIs defined on SessionizingController class, it invokes one of the methods under SessioningUtils class that provides responce for the API requests.

3. How the code was tested:
The code was tested using unit tests, testing each method used by APIs vs expected results under test/java/com/sessioning/SessionizingApplicationTests.java
To run the test, right-click SessionizingApplicationTests.java and select Run SessionizingApplicationTests.

4. Space and time complexity:
Construction - insert to HashMap O(1) + insert to list O(1), total HashMap construction/loading to memory containing list of PageView is O(n).

Session object construction for specific siteUrl - to get List of all page view is O(1), sort the list by timestamp - O(lLogl). 
to prepare sessions we need O(l) - l is length of the list.
To insert session to the set is O(lLogl).
Total session preparation O(lLogL).
For median calculation, additionally to session construction, we need to sort the set of sessions by session length with O(sLogs) - s is number of sessions.
For unique visited sites for visitor id requires O(nLogn) to insert to the set.


5. Scale support proposition:
To provide scale support, the project needs to use streaming (like Kafka), 
to process the data on going and not to hold it all in memory - preferably on separate service that is in charge of data loading only.
Then the processed data will be stored in some persistent layer (like db) and would hold in memory only cache of recently used objects (using cache solutions like Redis).
As well, the application needs to have some load balancer while running on several clusters to be able to contest with big number of concurrent requests getting processed data from shared cache.
The application can be splitted as well to 2 separate services, one dealing with site url related requests, and the second one related to visitor id requests.
