ALTER TABLE eg_lg_document
DROP COLUMN document_id;
ALTER TABLE eg_lg_document
ADD COLUMN IF NOT EXISTS document_type character varying(64) NOT NULL;