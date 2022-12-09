CREATE TABLE IF NOT EXISTS  ilms_case(
	id character varying(64) NOT NULL,
	tenant_id character varying(64),
	case_number character varying(64) UNIQUE,
	cnr_number character varying(32) UNIQUE,
	parent_case_id character varying(32) DEFAULT NULL,
    case_hierarchy character varying(32) NOT NULL,
    case_type character varying(32) NOT NULL,
    case_category character varying(64) NOT NULL,
	case_year bigint NOT NULL,
	filing_number character varying(64) DEFAULT NULL,
	filing_date bigint NOT NULL,
    registrtion_date bigint NOT NULL,
    case_summary character varying(200) NOT NULL,
    arising_details character varying(32) DEFAULT NULL,
    policy_or_nonpolicy_matter character varying(32) NOT NULL,
    application_no character varying(32) NOT NULL,
    is_case_number_correct boolean NOT NULL,
	case_status character varying(64) NOT NULL,
	first_hearing_date bigint,
	previous_hearing_date bigint,
	next_hearing_date bigint,
    case_stage character varying(64) NOT NULL,
	case_sub_stage character varying(64) NOT NULL,
	case_flag character varying(32) DEFAULT NULL,
	department_name character varying(32) NOT NULL,
	recommend_oic character varying(32) NOT NULL,
	remarks character varying(200) DEFAULT NULL,
	assigned_officer_id character varying(200) DEFAULT NULL,
	additional_details jsonb,
	status character varying(64) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_ilms_case_id PRIMARY KEY (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case  ON ilms_case
(
  id
);

CREATE INDEX  IF NOT EXISTS  index_case_number_ilms_case  ON ilms_case
(
  case_number
);

CREATE INDEX  IF NOT EXISTS  index_cnr_number_ilms_case  ON ilms_case
(
  cnr_number
);

CREATE TABLE IF NOT EXISTS  ilms_case_auditlog(
	id character varying(64) NOT NULL,
    	tenant_id character varying(64),
    	case_number character varying(64),
    	cnr_number character varying(32),
    	parent_case_id character varying(32) DEFAULT NULL,
        case_hierarchy character varying(32) NOT NULL,
        case_type character varying(32) NOT NULL,
        case_category character varying(64) NOT NULL,
    	case_year bigint NOT NULL,
    	filing_number character varying(64) DEFAULT NULL,
    	filing_date bigint NOT NULL,
        registrtion_date bigint NOT NULL,
        case_summary character varying(200) NOT NULL,
        arising_details character varying(32) DEFAULT NULL,
        policy_or_nonpolicy_matter character varying(32) NOT NULL,
        application_no character varying(32) NOT NULL,
        is_case_number_correct boolean NOT NULL,
    	case_status character varying(64) NOT NULL,
    	first_hearing_date bigint,
    	previous_hearing_date bigint,
    	next_hearing_date bigint,
        case_stage character varying(64) NOT NULL,
    	case_sub_stage character varying(64) NOT NULL,
    	case_flag character varying(32) DEFAULT NULL,
    	department_name character varying(32) NOT NULL,
    	recommend_oic character varying(32) NOT NULL,
    	remarks character varying(200) DEFAULT NULL,
    	assigned_officer_id character varying(200) DEFAULT NULL,
    	additional_details jsonb,
    	status character varying(64) NOT NULL,
        createdby character varying(64),
        createdtime bigint,
        lastmodifiedby character varying(64),
        lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case_auditlog  ON ilms_case_auditlog
(
  id
);


CREATE TABLE IF NOT EXISTS  ilms_act(
	id character varying(32) NOT NULL,
	case_id character varying(64) NOT NULL,
	name character varying(64) NOT NULL,
	section_number character varying(64) NOT NULL,
	status character varying(64) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
	CONSTRAINT pk_act_id PRIMARY KEY (id),
	CONSTRAINT fk_act_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_act  ON ilms_act
(
   id
);
CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_act  ON ilms_act
(
  case_id
);

---- To store the petioner and respondent information which can be identified using party_type column

CREATE TABLE IF NOT EXISTS  ilms_case_party(
	id character varying(32) NOT NULL,
	case_id character varying(64) NOT NULL,
    first_name character varying(32) NOT NULL,
    last_name character varying(32) NOT NULL,
    gender character varying(32) NOT NULL,
    petitioner_type character varying(32) default NULL,
    address character varying(32) NOT NULL,
	department_name character varying(64) default NULL,
	contact_number character varying(64) NOT NULL,
    party_type character varying(32) NOT NULL,
	status character varying(64) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_party_id PRIMARY KEY (id),
	CONSTRAINT fk_party_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case_party  ON ilms_case_party
(
  id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_case_party  ON ilms_case_party
(
  case_id
);

CREATE TABLE IF NOT EXISTS  ilms_advocate(
	id character varying(32) NOT NULL,
	party_id character varying(64) NOT NULL,
    hearing_id character varying(64) DEFAULT NULL,
    first_name character varying(32) NOT NULL,
    last_name character varying(32) NOT NULL,
	contact_number character varying(64) NOT NULL,
    party_type character varying(32) NOT NULL,
	status character varying(64) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_advocate_id PRIMARY KEY (id),
	CONSTRAINT fk_advocate_party_id FOREIGN KEY (party_id) REFERENCES ilms_case_party (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_advocate  ON ilms_advocate
(
  id
);

CREATE INDEX  IF NOT EXISTS  index_party_id_ilms_advocate  ON ilms_advocate
(
   party_id
);


CREATE TABLE IF NOT EXISTS  ilms_hearing(
	id character varying(32) NOT NULL,
	hearing_number character varying(32) NOT NULL,
	court_id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
    judge_name character varying(32) DEFAULT NULL,
	hearing_date bigint,
	business_date bigint,
    hearing_purpose character varying(32) NOT NULL,
	required_officer character varying(32) NOT NULL,
	affidavit_filing_date bigint,
	affidavit_filing_due_date bigint,
	case_number character varying(32) NOT NULL,
	oath_number character varying(32) NOT NULL,
	first_hearing_date bigint,
	previous_hearing_date bigint,
	next_hearing_date bigint,
	is_presence_required boolean,
	hearing_type character varying(32) NOT NULL,
	department_officer character varying(32) NOT NULL,
	remarks character varying(100) NOT NULL,
	additional_details jsonb,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_hearing_id PRIMARY KEY (id),
	CONSTRAINT fk_hearing_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_hearing  ON ilms_hearing
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_hearing  ON ilms_hearing
(
   case_id
);

CREATE TABLE IF NOT EXISTS  ilms_hearing_auditlog(
	id character varying(32) NOT NULL,
    	hearing_number character varying(32) NOT NULL,
    	court_id character varying(32) NOT NULL,
    	case_id character varying(32) NOT NULL,
        judge_name character varying(32) DEFAULT NULL,
    	hearing_date bigint,
    	business_date bigint,
        hearing_purpose character varying(32) NOT NULL,
    	required_officer character varying(32) NOT NULL,
    	affidavit_filing_date bigint,
    	affidavit_filing_due_date bigint,
    	cnr_number character varying(32) NOT NULL,
    	oath_number character varying(32) NOT NULL,
    	first_hearing_date bigint,
    	previous_hearing_date bigint,
    	next_hearing_date bigint,
    	is_presence_required boolean,
    	hearing_type character varying(32) NOT NULL,
    	department_officer character varying(32) NOT NULL,
    	remarks character varying(100) NOT NULL,
    	additional_details jsonb,
        status character varying(16) NOT NULL,
        createdby character varying(64),
        createdtime bigint,
        lastmodifiedby character varying(64),
        lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_hearing_auditlog  ON ilms_hearing_auditlog
(
   id
);



CREATE TABLE IF NOT EXISTS  ilms_court(
	id character varying(32) NOT NULL,
	court_number character varying(32) NOT NULL,
	hearing_id character varying(32) NOT NULL,
    court_name character varying(32) DEFAULT NULL,
    district character varying(32) NOT NULL,
	state character varying(32) NOT NULL,
	bench character varying(32) NOT NULL,
	division character varying(32) NOT NULL,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_court_id PRIMARY KEY (id),
	CONSTRAINT fk_hearing_court_id FOREIGN KEY (hearing_id) REFERENCES ilms_hearing (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_court  ON ilms_court
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_hearing_id_ilms_court  ON ilms_court
(
   hearing_id
);

CREATE TABLE IF NOT EXISTS  ilms_payment(
	id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
	hearing_id character varying(32) NOT NULL,
	fine_imposed_date character varying(64),
	fine_due_date character varying(64),
	fine_amount character varying(32) NOT NULL,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_payment_id PRIMARY KEY (id),
	CONSTRAINT fk_hearing_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id),
	CONSTRAINT fk_payment_hearing_id FOREIGN KEY (hearing_id) REFERENCES ilms_hearing (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_payment ON ilms_payment
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_payment ON ilms_payment
(
   case_id
);
CREATE INDEX  IF NOT EXISTS  index_hearing_id_ilms_payment  ON ilms_payment
(
   hearing_id
);

CREATE TABLE IF NOT EXISTS  ilms_payment_auditlog(
	id character varying(32) NOT NULL,
    case_id character varying(32) NOT NULL,
    hearing_id character varying(32) NOT NULL,
    fine_imposed_date character varying(64),
    fine_due_date character varying(64),
    fine_amount character varying(32) NOT NULL,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_payment_auditlog ON ilms_payment_auditlog
(
   id
);


CREATE TABLE IF NOT EXISTS  ilms_judgement(
	id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
    order_type character varying(32) DEFAULT NULL,
    order_date bigint NOT NULL,
	decision_status character varying(32) NOT NULL,
	compliance_date bigint,
	revised_compliance_date bigint,
	order_no_override character varying(32) NOT NULL,
	revised_complaince_reason character varying(32) NOT NULL,
    compliance_status character varying(32) NOT NULL,
	remarks character varying(64) NOT NULL,
	additional_details jsonb,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_judgement_id PRIMARY KEY (id),
	CONSTRAINT fk_judgement_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_judgement  ON ilms_judgement
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_judgement  ON ilms_judgement
(
   case_id
);

CREATE TABLE IF NOT EXISTS  ilms_judgement_auditlog(
   id character varying(32) NOT NULL,
       case_id character varying(32) NOT NULL,
        order_type character varying(32) DEFAULT NULL,
        order_date bigint NOT NULL,
       decision_status character varying(32) NOT NULL,
       compliance_date bigint,
       revised_compliance_date bigint,
       order_no_override character varying(32) NOT NULL,
       revised_complaince_reason character varying(32) NOT NULL,
        compliance_status character varying(32) NOT NULL,
       remarks character varying(64) NOT NULL,
       additional_details jsonb,
        status character varying(16) NOT NULL,
        createdby character varying(64),
        createdtime bigint,
        lastmodifiedby character varying(64),
        lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_judgement_auditlog  ON ilms_judgement_auditlog
(
   id
);

CREATE TABLE IF NOT EXISTS  ilms_document(
	id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
	document_type character varying(64) NOT NULL,
	file_store_id character varying(64) NOT NULL,
    status character varying(32) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_document ON ilms_document
(
   id
);

