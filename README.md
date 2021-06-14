#Sessionizing
1. Set up and run instructions:
a. Clone the project to the development environment (IntelliJ).
b. Run locally SessionizingApplication.java under /sessionazing/src/main/java/com/sessionizing (make sure you are running with Java 8).
Right-click on the class and select Run "SessionizingApplication".

After the application is up, there are 3 available APIs to run from POSTMAN or Browser:
a. http://localhost:9090/session/numOfSessions/{siteUrl} - like http://localhost:9090/session/numOfSessions/www.s_6.com
Expected to get number of sessions for selected site url. 
b. http://localhost:9090/session/numOfUniqueVisitedSites/{visitorId} - like http://localhost:9090/session/numOfUniqueVisitedSites/visitor_1752
Expected to get number of unique visited sites by provided visitor id.
c. http://localhost:9090/session/medianSessionLength/{siteUrl} - http://localhost:9090/session/medianSessionLength/www.s_4.com
Expected to get median of session length for provided sute url.

2. Description of the solution:
In this solution I used Spring Boot framework to run web application using Appache tomcat server.
The code is written using Java 8 capabilities.
On application initialization, DataLoader class loads the data files located under /resources/static folder into Map that clusters the data
a. By siteUrl
b. By visitorId
This kind of data storage enables to get data for specific siteUrl by time complexity O(1), space complexity O(n).
The application uses 2 kind of objects:
a. PageView  - to describe single page of a site that was visited by a visitor at some timestamp.
b. Session - object that unites group of page views of a single visitor to a single site such that the time between every two successive page views is not longer than 30 minutes.
This object contains data for specific site, visitor id, session start, session length, list of PageView related to the specific session.

While running of the APIs it invokes one of the methods under SessioningUtils class that provides responce for the API requests.

3. How the code was tested:
The code was tested using unit test, testing each method used by APIs vs expected results under test/java/com/sessioning/SessionizingApplicationTests.java
To run the test, right-click SessionizingApplicationTests.java and select Run SessionizingApplicationTests.

4. Space and time complexity:
The main object the project works with is Map compound of key - siteUrl, value - Map with key - visitor id and value - list of related Page Views.
Using this map we can obtain all data related to specific siteUrl with O(1), and then to cut data for specific visitor id with O(1).
The map requires memory allocation with O(n).

5. Scale support proposition:
To provide scale support, the project need to use streaming (like Kafka), to process the data on going and not to hold it in memory.
The processed data I would store in some persistent layer (like db) and would hold in memory only cache of recently used objects (using cache solutions like Redis).
As well, the application should run on several clusters to be able to contest big number of concurrent requests getting processed data from shared cache (as one point of truth).