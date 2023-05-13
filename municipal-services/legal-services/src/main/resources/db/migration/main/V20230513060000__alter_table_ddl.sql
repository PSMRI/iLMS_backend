ALTER TABLE eg_lg_case
DROP COLUMN case_sub_stage,
DROP COLUMN registrtion_date;
ALTER TABLE eg_lg_document DROP COLUMN document_type;
ALTER TABLE eg_lg_case
ADD COLUMN IF NOT EXISTS first_hearing_date bigint;