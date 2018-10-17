## Replace BI cases with B cases in action service
This folder contains scripts to replace all BI cases in the action.case table with the corresponding B case from the case service

### Scripts
Export the BI case id's from the action.case table to a csv file
```
psql "{ACTION_POSTGRES_URI}" -f 1_export_bicase_ids_to_csv.sql > temp_cases.csv
```

Create a temporary table in case service to hold the BI case id's
```
psql "{CASE_POSTGRES_URI}" -f 2_create_temp_cases_table.sql
```

Copy the BI case id's from the csv to the temporary table
```
psql "{CASE_POSTGRES_URI}" -c "COPY casesvc.temp_cases(bicaseid, actionplanid) FROM STDIN WITH CSV;" < temp_cases.csv
```
ac
Copy B case id's into temporary table
```
psql "{CASE_POSTGRES_URI}" -f 4_copy_bcase_ids_into_temp_table.sql
```

Export B/BI case id's back into csv file
```
psql "{CASE_POSTGRES_URI}" -f 5_export_updated_temp_cases_csv.sql > temp_cases.csv
```

Create temporary table in action service to hold B/BI case id's
```
psql "{ACTION_POSTGRES_URI}" -f 6_create_temp_action_case_table.sql
```

Copy id's from csv into temporary table
```
psql "{ACTION_POSTGRES_URI}" -c "COPY action.temp_cases(bicaseid, bcaseid) FROM STDIN WITH CSV;" < temp_cases.csv
```

Run main script to replace BI cases with B cases. 
Where two BI cases map to the same B case the extra B cases are removed
```
psql "{ACTION_POSTGRES_URI}" -f 8_replace_bicases_with_bcases.sql
```

Be sure to delete the temporary tables and csv file after finishing