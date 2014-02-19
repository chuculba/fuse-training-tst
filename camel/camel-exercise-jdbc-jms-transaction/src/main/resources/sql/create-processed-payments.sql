-- Table: "ProcessedPayments"

-- DROP TABLE "ProcessedPayments";

CREATE TABLE "ProcessedPayments"
(
  "paymentIdentifier" character varying(32),
  id serial NOT NULL,
  CONSTRAINT processedpaymentspk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);
ALTER TABLE "ProcessedPayments" OWNER TO postgres;

