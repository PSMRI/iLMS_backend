ALTER TABLE eg_lg_case
DROP COLUMN IF EXISTS recommend_oic;
ALTER TABLE eg_lg_case
ADD COLUMN recommend_oic character varying(64);