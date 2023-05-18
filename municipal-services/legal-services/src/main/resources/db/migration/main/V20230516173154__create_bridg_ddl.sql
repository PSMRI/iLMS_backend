DROP TABLE IF EXISTS eg_lg_party_advocate_bridge;

CREATE TABLE eg_lg_party_advocate_bridge (
id character varying(64) NOT NULL,
case_id character varying(64) NOT NULL,
party_id character varying(64) NOT NULL,
party_type character varying(64) NOT NULL,
advocate_id character varying(64) NOT NULL,
createdby character varying(64),
createdtime bigint,
lastmodifiedby character varying(64),
lastmodifiedtime bigint,
CONSTRAINT pk_eg_lg_party_advocate_bridge_id PRIMARY KEY (id)
);

ALTER TABLE eg_lg_case_party DROP COLUMN advocate_id;