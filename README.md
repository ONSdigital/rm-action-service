# Action Service
The Action service is a RESTful web service implemented using Spring Boot. 

An action represents an operation that is required for a case. For example, posting out an enrolment code or sending an email.

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


## Scheduled Tasks
### Action Distribution (default: 1s delay between invocations)
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

### Plan Execution (default: 1s delay between invocations)
* For each action plan:
    * If there are no cases in the local database for the action plan, do nothing.
    * Get the cases, action rules and action types from the local database.
    * For each case and each rule for the action plan, if the rule has triggered, create actions and save them to the database.

### Report Scheduler (default: 1s delay between invocations)
Creates a report using the stored procedure ActionReport.mi

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
