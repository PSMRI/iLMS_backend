ALTER TABLE eg_lg_hearing ADD COLUMN tenant_id character varying(64) DEFAULT NULL;
ALTER TABLE eg_lg_judgement ADD COLUMN tenant_id character varying(64) DEFAULT NULL;
