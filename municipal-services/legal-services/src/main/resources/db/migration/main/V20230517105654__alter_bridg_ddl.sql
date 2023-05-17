ALTER TABLE eg_lg_party_advocate_bridge
ADD COLUMN IF NOT EXISTS createdby character varying(64),
ADD COLUMN IF NOT EXISTS createdtime bigint,
ADD COLUMN IF NOT EXISTS lastmodifiedby character varying(64),
ADD COLUMN IF NOT EXISTS lastmodifiedtime bigint;