
<a name="paths"></a>
## Paths

<a name="createactionplan"></a>
### Create an action plan
```
POST /actionplans
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Body**|**body**  <br>*optional*|[ActionPlanPostRequestDTO](#actionplanpostrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|successful operation|[ActionPlanDTO](#actionplandto)|
|**201**|Action plan has been created|No Content|
|**400**|Required fields are missing or invalid|No Content|
|**409**|Action plan already exists|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for action plans


<a name="findactionplans"></a>
### List action plans for the optional selectors, most recent first
```
GET /actionplans
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Query**|**selectors**  <br>*required*|object|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action plans for the optional selectors|< [ActionPlanDTO](#actionplandto) > array|
|**204**|Action plans not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for action plans


<a name="findactionplanjobbyid"></a>
### Get the Action plan job for an actionPlanJobId
```
GET /actionplans/jobs/{actionplanjobid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanjobid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action plan job for the actionPlanJobId|[ActionPlanJobDTO](#actionplanjobdto)|
|**404**|Action plan job not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for action plan jobs


<a name="findactionplanbyactionplanid"></a>
### Get the action plan for an actionPlanId
```
GET /actionplans/{actionplanid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action plan has been updated|[ActionPlanDTO](#actionplandto)|
|**404**|Action plan not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for action plans


<a name="updateactionplanbyactionplanid"></a>
### Update action plan
```
PUT /actionplans/{actionplanid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanid**  <br>*required*|string (uuid)|
|**Body**|**body**  <br>*optional*|[ActionPlanPutRequestDTO](#actionplanputrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action plan has been updated|[ActionPlanDTO](#actionplandto)|
|**400**|Required fields are missing or invalid|No Content|
|**404**|Action plan not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for action plans


<a name="executeactionplan"></a>
### Create an action plan job (i.e. execute the action plan)
```
POST /actionplans/{actionplanid}/jobs
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanid**  <br>*required*|string (uuid)|
|**Body**|**body**  <br>*optional*|[ActionPlanJobRequestDTO](#actionplanjobrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|successful operation|[ActionPlanJobDTO](#actionplanjobdto)|
|**201**|Action plan job has been created|No Content|
|**400**|Required fields are missing or invalid|No Content|
|**404**|Action plan not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for action plan jobs


<a name="findallactionplanjobsbyactionplanid"></a>
### List Action plan job for an actionPlanId, most recent first
```
GET /actionplans/{actionplanid}/jobs
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action plan jobs for the actionPlanId|< [ActionPlanJobDTO](#actionplanjobdto) > array|
|**204**|Action plan jobs not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for action plan jobs


<a name="createactionrule"></a>
### Create an action rule
```
POST /actionrules
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Body**|**body**  <br>*optional*|[ActionRulePostRequestDTO](#actionrulepostrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|successful operation|[ActionRuleDTO](#actionruledto)|
|**201**|Action rule has been created|No Content|
|**400**|Required fields are missing or invalid|No Content|
|**404**|Action plan or action type not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for action rules


<a name="findactionrulesbyactionplanid"></a>
### List action rules for an actionPlanId
```
GET /actionrules/actionplan/{actionplanid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionplanid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action rules for an actionPlanId|< [ActionRuleDTO](#actionruledto) > array|
|**404**|Action plan not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for action rules


<a name="updateactionrule"></a>
### Update an action rule
```
PUT /actionrules/{actionRuleId}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionRuleId**  <br>*required*|string (uuid)|
|**Body**|**body**  <br>*optional*|[ActionRulePutRequestDTO](#actionruleputrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|successful operation|[ActionRuleDTO](#actionruledto)|
|**201**|Action rule has been updated|No Content|
|**400**|Required fields are missing or invalid|No Content|
|**404**|Action rule not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for action rules


<a name="createadhocaction"></a>
### Create an Action
```
POST /actions
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Body**|**body**  <br>*optional*|[ActionPostRequestDTO](#actionpostrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|successful operation|[ActionDTO](#actiondto)|
|**201**|Action has been created|No Content|
|**400**|Required fields are missing or invalid|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="findactions"></a>
### List actions with the actionType and state, most recent first
```
GET /actions
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Query**|**actiontype**  <br>*optional*|string|
|**Query**|**state**  <br>*optional*|enum (SUBMITTED, PENDING, ACTIVE, COMPLETED, DECLINED, CANCEL_SUBMITTED, CANCEL_PENDING, CANCELLING, CANCELLED, ABORTED)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Actions for the actionType and state|< [ActionDTO](#actiondto) > array|
|**204**|Action not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="findactionsbycaseid"></a>
### List actions for a case
```
GET /actions/case/{caseid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**caseid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Actions for the case|< [ActionDTO](#actiondto) > array|
|**204**|Action not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="cancelactions"></a>
### Cancel all the actions for a case
```
PUT /actions/case/{caseid}/cancel
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**caseid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Cancelled action|< [ActionDTO](#actiondto) > array|
|**204**|No actions to be cancelled|No Content|
|**404**|Case not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="findactionbyactionid"></a>
### Get the action for an actionId
```
GET /actions/{actionid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionid**  <br>*required*|string (uuid)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action for the actionId|[ActionDTO](#actiondto)|
|**404**|Action not found|No Content|


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="updateaction"></a>
### Update an Action
```
PUT /actions/{actionid}
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionid**  <br>*required*|string (uuid)|
|**Body**|**body**  <br>*optional*|[ActionPutRequestDTO](#actionputrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action has been updated|[ActionDTO](#actiondto)|
|**404**|Action not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for actions


<a name="feedbackaction"></a>
### Update state of the action
```
PUT /actions/{actionid}/feedback
```


#### Parameters

|Type|Name|Schema|
|---|---|---|
|**Path**|**actionid**  <br>*required*|string (uuid)|
|**Body**|**body**  <br>*optional*|[ActionFeedbackRequestDTO](#actionfeedbackrequestdto)|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Action has been updated|[ActionDTO](#actiondto)|
|**400**|Required fields are missing or invalid|No Content|
|**404**|Action not found|No Content|


#### Consumes

* `application/json`


#### Produces

* `application/json`


#### Tags

* API for actions



