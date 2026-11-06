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

### Archive notes

This repo is to be archived as the project scope has reduced and this service is no longer required.

Please look at the ticket:
https://companieshouse.atlassian.net/browse/DPS-101

The subtasks also link all the PRs that were created to build the service and the pipeline.

The following PRs can be re-looked at to create the pipeline in case this service is required again in the future:
ci-pipelines: https://github.com/companieshouse/ci-pipelines/pull/7803
docker-chs-dev: https://github.com/companieshouse/docker-chs-development/pull/2699/changes
aws-shared-services-infrastructure-terraform: https://github.com/companieshouse/aws-shared-services-infrastructure-terraform/pull/424
sonarqube-ansible: https://github.com/companieshouse/sonarqube-ansible/pull/323/changes