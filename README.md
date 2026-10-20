# cis-prosecution-api
CIS Prosecution API - This service is part of the CIS integration layer and allows calls to CHIPS relating to prosecution cases.

### Running using docker

The service can be ran in development mode on docker. Doing so will start it on port `8080`.

To start the service on docker you will need to run `chs-dev modules enable cis-sjp` and `chs-dev development enable cis-prosecution-api`.

If you have not set up the service to run on docker before there are a series of additional steps which will be required. They can be found [here](https://companieshouse.atlassian.net/wiki/spaces/TEAM8/pages/5921210431/Guide+to+Running+CIS+Services+in+Docker)

### Healthcheck

A healthcheck endpoint is provided to verify that the service is running:

`GET /cis-prosecution-api/healthcheck`

A successful response will return HTTP 200.
