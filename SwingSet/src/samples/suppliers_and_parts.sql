/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2005, The Pangburn Company and Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */


/* This SQL script is used to generate the sample suppliers_and_parts database
   for the SwingSet sample programs */
   
--
-- PostgreSQL database dump
--

\connect - postgres

SET search_path = public, pg_catalog;

--
-- TOC entry 2 (OID 64754)
-- Name: supplier_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE supplier_data_seq
    START 1
    INCREMENT 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    CACHE 1;


--
-- TOC entry 8 (OID 64756)
-- Name: supplier_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE supplier_data (
    supplier_id integer DEFAULT nextval('supplier_data_seq'::text) NOT NULL,
    supplier_name character varying(50) NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    city character varying(50) NOT NULL
);


--
-- TOC entry 9 (OID 64756)
-- Name: supplier_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE supplier_data FROM PUBLIC;
GRANT SELECT ON TABLE supplier_data TO swingset;


--
-- TOC entry 4 (OID 64763)
-- Name: part_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE part_data_seq
    START 1
    INCREMENT 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    CACHE 1;


--
-- TOC entry 10 (OID 64765)
-- Name: part_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE part_data (
    part_id integer DEFAULT nextval('part_data_seq'::text) NOT NULL,
    part_name character varying(50) NOT NULL,
    color_code smallint DEFAULT 0 NOT NULL,
    weight smallint DEFAULT 0 NOT NULL,
    city character varying(50) NOT NULL
);


--
-- TOC entry 11 (OID 64765)
-- Name: part_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE part_data FROM PUBLIC;
GRANT SELECT ON TABLE part_data TO swingset;


--
-- TOC entry 6 (OID 64773)
-- Name: supplier_part_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE supplier_part_data_seq
    START 1
    INCREMENT 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    CACHE 1;


--
-- TOC entry 12 (OID 64775)
-- Name: supplier_part_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE supplier_part_data (
    supplier_part_id integer DEFAULT nextval('supplier_part_data_seq'::text) NOT NULL,
    supplier_id integer,
    part_id integer,
    quantity smallint DEFAULT 0 NOT NULL,
    ship_date date
);


--
-- TOC entry 13 (OID 64775)
-- Name: supplier_part_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE supplier_part_data FROM PUBLIC;
GRANT SELECT ON TABLE supplier_part_data TO swingset;


--
-- Data for TOC entry 21 (OID 64756)
-- Name: supplier_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY supplier_data (supplier_id, supplier_name, status, city) FROM stdin;
1	Smith	20	London
2	Jones	10	Paris
3	Blake	30	Paris
4	Clark	20	London
5	Adams	30	Athens
\.


--
-- Data for TOC entry 22 (OID 64765)
-- Name: part_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY part_data (part_id, part_name, color_code, weight, city) FROM stdin;
1	Nut	0	12	London
2	Bolt	1	17	Paris
3	Screw	2	17	Rome
4	Screw	0	14	London
5	Cam	2	12	Paris
6	Cog	0	19	London
\.


--
-- Data for TOC entry 23 (OID 64775)
-- Name: supplier_part_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY supplier_part_data (supplier_part_id, supplier_id, part_id, quantity, ship_date) FROM stdin;
10	4	2	200	\N
11	4	4	300	\N
12	4	5	400	\N
9	3	3	700	\N
6	1	1	700	2003-12-12
2	1	2	200	2003-01-01
3	1	3	400	2003-01-05
1	1	4	100	2003-10-20
5	1	5	100	2004-03-25
4	1	6	700	2004-03-15
7	2	1	400	2004-09-15
8	2	2	700	2003-10-15
\.


--
-- TOC entry 14 (OID 64762)
-- Name: sd_supplier_name_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sd_supplier_name_idx ON supplier_data USING btree (supplier_name);


--
-- TOC entry 17 (OID 64772)
-- Name: pd_part_name_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX pd_part_name_idx ON part_data USING btree (part_name);


--
-- TOC entry 19 (OID 64789)
-- Name: spd_supplier_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX spd_supplier_id_idx ON supplier_part_data USING btree (supplier_id);


--
-- TOC entry 18 (OID 64790)
-- Name: spd_part_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX spd_part_id_idx ON supplier_part_data USING btree (part_id);


--
-- TOC entry 15 (OID 64760)
-- Name: supplier_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supplier_data
    ADD CONSTRAINT supplier_data_pkey PRIMARY KEY (supplier_id);


--
-- TOC entry 16 (OID 64770)
-- Name: part_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY part_data
    ADD CONSTRAINT part_data_pkey PRIMARY KEY (part_id);


--
-- TOC entry 20 (OID 64779)
-- Name: supplier_part_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supplier_part_data
    ADD CONSTRAINT supplier_part_data_pkey PRIMARY KEY (supplier_part_id);


--
-- TOC entry 24 (OID 64781)
-- Name: $1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supplier_part_data
    ADD CONSTRAINT "$1" FOREIGN KEY (supplier_id) REFERENCES supplier_data(supplier_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 25 (OID 64785)
-- Name: $2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supplier_part_data
    ADD CONSTRAINT "$2" FOREIGN KEY (part_id) REFERENCES part_data(part_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3 (OID 64754)
-- Name: supplier_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval ('supplier_data_seq', 5, true);


--
-- TOC entry 5 (OID 64763)
-- Name: part_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval ('part_data_seq', 6, true);


--
-- TOC entry 7 (OID 64773)
-- Name: supplier_part_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval ('supplier_part_data_seq', 12, true);


