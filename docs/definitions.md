
<a name="definitions"></a>
## Definitions

<a name="actiondto"></a>
### ActionDTO

|Name|Schema|
|---|---|
|**actionPlanId**  <br>*optional*|string (uuid)|
|**actionRuleId**  <br>*optional*|string (uuid)|
|**actionTypeName**  <br>*required*|string|
|**caseId**  <br>*required*|string (uuid)|
|**createdBy**  <br>*required*|string|
|**createdDateTime**  <br>*optional*|string (date-time)|
|**id**  <br>*optional*|string (uuid)|
|**manuallyCreated**  <br>*optional*|boolean|
|**priority**  <br>*optional*|integer (int32)|
|**situation**  <br>*optional*|string|
|**state**  <br>*optional*|enum (SUBMITTED, PENDING, ACTIVE, COMPLETED, DECLINED, CANCEL_SUBMITTED, CANCEL_PENDING, CANCELLING, CANCELLED, ABORTED)|
|**updatedDateTime**  <br>*optional*|string (date-time)|


<a name="actionfeedbackrequestdto"></a>
### ActionFeedbackRequestDTO

|Name|Schema|
|---|---|
|**outcome**  <br>*required*|enum (REQUEST_FAILED, REQUEST_ACCEPTED, REQUEST_COMPLETED, REQUEST_DECLINED, REQUEST_COMPLETED_DEACTIVATE, REQUEST_COMPLETED_DISABLE, CANCELLATION_FAILED, CANCELLATION_ACCEPTED, CANCELLATION_COMPLETED)|
|**situation**  <br>*required*|string|


<a name="actionplandto"></a>
### ActionPlanDTO

|Name|Schema|
|---|---|
|**createdBy**  <br>*optional*|string|
|**description**  <br>*optional*|string|
|**id**  <br>*required*|string (uuid)|
|**lastRunDateTime**  <br>*optional*|string (date-time)|
|**name**  <br>*optional*|string|
|**selectors**  <br>*optional*|< string, string > map|


<a name="actionplanjobdto"></a>
### ActionPlanJobDTO

|Name|Description|Schema|
|---|---|---|
|**actionPlanId**  <br>*optional*||string (uuid)|
|**createdBy**  <br>*required*|**Length** : `2 - 50`|string|
|**createdDateTime**  <br>*optional*||string (date-time)|
|**id**  <br>*optional*||string (uuid)|
|**state**  <br>*optional*||string|
|**updatedDateTime**  <br>*optional*||string (date-time)|


<a name="actionplanjobrequestdto"></a>
### ActionPlanJobRequestDTO

|Name|Description|Schema|
|---|---|---|
|**createdBy**  <br>*required*|**Length** : `2 - 50`|string|


<a name="actionplanpostrequestdto"></a>
### ActionPlanPostRequestDTO

|Name|Description|Schema|
|---|---|---|
|**createdBy**  <br>*required*|**Length** : `0 - 20`|string|
|**description**  <br>*required*|**Length** : `0 - 250`|string|
|**name**  <br>*required*|**Length** : `0 - 100`|string|
|**selectors**  <br>*optional*||< string, string > map|


<a name="actionplanputrequestdto"></a>
### ActionPlanPutRequestDTO

|Name|Schema|
|---|---|
|**description**  <br>*optional*|string|
|**lastRunDateTime**  <br>*optional*|string (date-time)|
|**selectors**  <br>*optional*|< string, string > map|


<a name="actionpostrequestdto"></a>
### ActionPostRequestDTO

|Name|Schema|
|---|---|
|**actionTypeName**  <br>*required*|string|
|**caseId**  <br>*required*|string (uuid)|
|**createdBy**  <br>*required*|string|
|**priority**  <br>*optional*|integer (int32)|


<a name="actionputrequestdto"></a>
### ActionPutRequestDTO

|Name|Schema|
|---|---|
|**priority**  <br>*optional*|integer (int32)|
|**situation**  <br>*optional*|string|


<a name="actionruledto"></a>
### ActionRuleDTO

|Name|Schema|
|---|---|
|**actionTypeName**  <br>*optional*|string|
|**daysOffset**  <br>*optional*|integer (int32)|
|**description**  <br>*optional*|string|
|**id**  <br>*optional*|string (uuid)|
|**name**  <br>*optional*|string|
|**priority**  <br>*optional*|integer (int32)|


<a name="actionrulepostrequestdto"></a>
### ActionRulePostRequestDTO

|Name|Description|Schema|
|---|---|---|
|**actionPlanId**  <br>*required*||string (uuid)|
|**actionTypeName**  <br>*required*|**Length** : `0 - 100`|string|
|**daysOffset**  <br>*required*||integer (int32)|
|**description**  <br>*required*|**Length** : `0 - 250`|string|
|**name**  <br>*required*|**Length** : `0 - 100`|string|
|**priority**  <br>*optional*|**Minimum value** : `1`  <br>**Maximum value** : `5`|integer (int32)|


<a name="actionruleputrequestdto"></a>
### ActionRulePutRequestDTO

|Name|Description|Schema|
|---|---|---|
|**daysOffset**  <br>*optional*||integer (int32)|
|**description**  <br>*optional*|**Length** : `0 - 250`|string|
|**name**  <br>*optional*|**Length** : `0 - 100`|string|
|**priority**  <br>*optional*|**Minimum value** : `1`  <br>**Maximum value** : `5`|integer (int32)|



