ALTER TABLE eg_lg_case
DROP COLUMN case_stage,
DROP COLUMN first_hearing_date;
ALTER TABLE eg_lg_advocate
ADD CONSTRAINT unique_contact_number UNIQUE (contact_number);
ALTER TABLE eg_lg_case
ADD COLUMN IF NOT EXISTS application_status character varying(64) DEFAULT NULL;
ALTER TABLE eg_lg_hearing
ADD COLUMN IF NOT EXISTS application_status character varying(64) DEFAULT NULL;
ALTER TABLE eg_lg_judgement
ADD COLUMN IF NOT EXISTS application_status character varying(64) DEFAULT NULL;
