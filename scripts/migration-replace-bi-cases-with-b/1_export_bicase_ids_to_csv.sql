\copy (SELECT id FROM action."case" WHERE sampleunittype = 'BI') TO STDOUT WITH CSV;