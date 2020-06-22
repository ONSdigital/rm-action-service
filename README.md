[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3a24e234068a4a1396ff5f3ff9ab64d9)](https://www.codacy.com/app/sdcplatform/rm-action-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-action-service&amp;utm_campaign=Badge_Grade) [![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/actionsvc.svg)]()
[![Build Status](https://travis-ci.org/ONSdigital/rm-action-service.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-action-service)
[![codecov](https://codecov.io/gh/ONSdigital/rm-action-service/branch/master/graph/badge.svg)](https://codecov.io/gh/ONSdigital/rm-action-service)

# Action Service
The Action service is a RESTful web service implemented using Spring Boot. 

An action represents an operation that is required for a case. For example, posting out a paper form or arranging a field visit. They are loose-form and can represent a number of different tasks that need to happen, both manual and automatic. These are distributed to consuming services via RabbitMQ and will be picked up if they match the action's requested handler. Common handlers include Action Exporter and Notify Gateway.

The Action service doesn't "understand" what a certain Action represents - the consuming services will do and will update the Action service when the action's state has been changed. Sometimes the consuming service will need more fine-grained information on the state than this service provides; for example, a Hotel Visit action for the Fieldwork Management Tool will be `ACTIVE` as soon as FWMT accepts it, but the FWMT will have sub-states such as "Visit assigned" or "Someone on-site". These are stored in the Action service as the Situation.

Actions are grouped into action plans via a set of action rules that define, in terms of a day, when a certain action should be taken. Action plans are applied to cases to create actions.

## Running

There are two ways of running this service

* The easiest way is via docker (https://github.com/ONSdigital/ras-rm-docker-dev)
* Alternatively running the service up in isolation
    ```bash
    cp .maven.settings.xml ~/.m2/settings.xml  # This only needs to be done once to set up mavens settings file
    mvn clean install
    mvn spring-boot:run
    ```

## API
See [the OpenAPI docs](https://onsdigital.github.io/rm-action-service/) for API documentation.

## Services consumed
### Case Service
* POST /cases/{caseid}/iac
* GET /cases/{caseid}

### Collection Exercise Service
* GET /collectionexercises/{id}

### Party Service
* GET /party-api/v1/parties/type/{sampleUnitType}/id/{partyId}

### Survey Service
* GET /surveys/{surveyId}

## Rabbit queues used
* ActionInstruction

## Scheduled Tasks
### Action Distribution (default: 1s delay between invocations)
* For each action of Action Type `SOCIALREM`, make a call to the Case service and generate a new IAC for the case.
* For each other action, if they're in the state `SUBMITTED`:
    * If the action has no Action Type or a null Response Required field, throw an exception.
    * Get information from the Collection Exercise, Party, Survey and Case services, and combine it with the Action Plan from the database to build an Action Request Context.
    * Create an Action Request (if the Action Type is `NOTIFY` and the Case Type is `B`, create one for each child party (aka Respondent)).
    * Use the 5 decorators in `BusinessActionProcessingService` to decorate the Action Request.
    * Transition the action to `REQUEST_DISTRIBUTED` if a response is required or `REQUEST_COMPLETED` if not.
    * Place the Action Request(s) on the ActionInstruction queue.
* For each other action, if they're in the state `CANCEL_SUBMITTED`:
    * Transition the action to `CANCELLATION_DISTRIBUTED`.
    * Create an Action Request for cancellation.
    * Place the Action Request on the ActionInstruction queue.

### CSV Ingester (default: whenever a CSV file is found to ingest in /tmp/csv)
* Parse each CSV line into an ActionRequest and place it into a bucket based on its Handler and whether its a `REQUEST_INSTRUCTION` or `CANCEL_INSTRUCTION`.
* If any line fails to parse, rename the CSV file with an indication of which line needs fixing.
* Place the Action Request(s) on the ActionInstruction queue.

### Plan Execution (default: 1s delay between invocations)


## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
