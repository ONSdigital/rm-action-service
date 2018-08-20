## Update action plan selectors using casetypeoverride table
Export casetypeoverride data into csv from collection exercise database
```
psql "{COLLECTION_EXERCISE_POSTGRES_URI}" -f 1_export-temp-cto-table.sql > temp_cto.csv
```

Create temporary `temp_cto` table in action database
```
psql "{ACTION_POSTGRES_URI}" -f 2_create-temp-cto-table.sql
```

Import data from csv to table
```
psql "{ACTION_POSTGRES_URI}" -c "COPY action.temp_cto(collectionexerciseid, sampleunittype, actionplanid) FROM STDIN WITH CSV;" < temp_cto.csv
```

Update action plan selectors
```
psql "{ACTION_POSTGRES_URI}" -f 4_update-action-plan-selectors.sql
```

Then drop temporary table and csv file