# was-config-manager

Install admin-client:
```commandline
mvn install:install-file -Dfile=admin-client-7.0.0.jar -DgroupId=com.ibm.ws -DartifactId=admin-client -Dversion=7.0.0 -Dpackaging=jar
```

Configuration sample:

[PUT] /configure
```json
[
  {
    "@type":"Auth",
    "@id":"4283fe80-ba13-4b55-a082-515e714ed932",
    "name":"dbUser",
    "username":"user",
    "password":"password",
    "description":"Password for database"
  },
  {
    "@type":"Auth",
    "@id":"80db9d0b-5a85-4319-983d-a1171a5048cd",
    "name":"EMS",
    "username":"user",
    "password":"password",
    "description":"Password for ems"
  },
  {
    "@type":"JDBCProvider",
    "@id":"520742fd-53f7-4348-89a7-bec33829c8b3",
    "name":"Oracle JDBC Driver (XA)",
    "implementationClassName":"oracle.jdbc.xa.client.OracleXADataSource",
    "classpath":[
      "C:/workspace/libs/ojdbc6.jar"
    ]
  },
  {
    "@type":"Datasource",
    "@id":"24ea9992-d6ac-4c8b-b065-f259885531bf",
    "name":"Datasource",
    "jndi":"jdbc/DS",
    "description":"Datasource",
    "jdbcProviderInfo":"520742fd-53f7-4348-89a7-bec33829c8b3",
    "authInfo":"4283fe80-ba13-4b55-a082-515e714ed932",
    "datasourceHelperClassname":"com.ibm.websphere.rsadapter.Oracle11gDataStoreHelper",
    "statementCacheSize":150,
    "url":"jdbc:oracle:thin:@localhost:1536/XE"
  },
  {
    "@type":"JMSProvider",
    "@id":"a511d126-7e65-4235-a1d6-d898fd469071",
    "name":"TIBCO EMS",
    "classpath":[
      "C:/tibco/ems/6.1/lib/tibjms.jar"
    ],
    "externalInitialContextFactory":"com.tibco.tibjms.naming.TibjmsInitialContextFactory",
    "externalProviderURL":"tibjmsnaming://localhost:7222",
    "username":"jndiuser",
    "password":"jndiuser"
  },
  {
    "@type":"JMSConnectionFactory",
    "@id":"e345e2a0-b24f-49f7-b4f9-900916fe4f17",
    "name":"Queue Connection Factory",
    "xa":false,
    "authInfo":"80db9d0b-5a85-4319-983d-a1171a5048cd",
    "externalJndi":"XAQueueConnectionFactory",
    "jndi":"jms/Tibco/QueueConnectionFactory",
    "type":"QUEUE",
    "providerInfo":"a511d126-7e65-4235-a1d6-d898fd469071",
    "poolInfo":{
      "@type":"JMSPoolInfo",
      "@id":"e2961d6f-19f8-4bf6-b94b-fce4d4fde411",
      "connectionTimeout":180,
      "minConnections":1,
      "maxConnections":10,
      "reapTime":180,
      "unusedTimeout":1800,
      "agedTimeout":0,
      "purgePolicy":"FailingConnectionOnly"
    }
  },
  {
    "@type":"JMSQueue",
    "@id":"9a041e43-a384-4105-91fa-b189afbd2a62",
    "name":"APP.TEST.QUEUE",
    "jndi":"jms/Tibco/APP.TEST.QUEUE",
    "externalJndi":"APP.TEST.QUEUE",
    "providerInfo":"a511d126-7e65-4235-a1d6-d898fd469071"
  },
  {
    "@type":"ListenerPort",
    "@id":"6afd3ea7-98a5-46f9-8406-4ca781a4acba",
    "name":"TestListenerPort",
    "initialState":"START",
    "description":"TestListenerPort",
    "connectionFactory":"e345e2a0-b24f-49f7-b4f9-900916fe4f17",
    "destination":"9a041e43-a384-4105-91fa-b189afbd2a62",
    "maxSessions":1,
    "maxRetries":0,
    "maxMessages":1
  },
  {
    "@type":"WorkManager",
    "@id":"b57e4f95-728b-47d6-86c2-baccd5381bb0",
    "name":"Test WorkManager",
    "jndiName":"wm/testWorkManager",
    "description":"Test WorkManager",
    "workTimeout":0,
    "workReqQSize":0,
    "workReqQFullAction":0,
    "numAlarmThreads":5,
    "minThreads":1,
    "maxThreads":10,
    "threadPriority":5,
    "growable":true
  },
  {
    "@type":"SharedLib",
    "@id":"9e5d64c6-0492-4ff2-b1eb-d223ac5f3fb1",
    "name":"TEST_LIB",
    "description":"TEST_LIB",
    "nativePath":null,
    "classpath":[
      "c:/workspace/app/config"
    ],
    "isolatedClassLoader":false
  }
]
```