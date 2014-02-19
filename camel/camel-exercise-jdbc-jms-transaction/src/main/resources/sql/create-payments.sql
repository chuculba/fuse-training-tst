-- Table: "Payments"

-- DROP TABLE "Payments";

CREATE TABLE "Payments"
(
  "from" character varying(32),
  "to" character varying(32),
  amount double precision,
  id serial NOT NULL,
  CONSTRAINT primke PRIMARY KEY (id)
)
WITH (OIDS=FALSE);
ALTER TABLE "Payments" OWNER TO postgres;
