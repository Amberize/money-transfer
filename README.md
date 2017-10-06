Money transfer application.



Tech stack:

- Dropwizard
- Guice
- Apache Ignite
- TestNG



API (port 8080):

- /accounts - CRUD for account (more in AccountsResource.class)
- /transfers - money transfer (more in TransfersResource.class)

Health & metrics (port 8081):

- /healthcheck - app & ignite health status
- /metrics - app metrics



How to run:

`java -jar money-transfer-1.0.jar server`



From Amberize with love =)