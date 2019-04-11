--
-- PostgreSQL database cluster dump
--

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE "module-service";
ALTER ROLE "module-service" WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'md50360b90d13873e39ac27538b744718a3';






\connect template1

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.2
-- Dumped by pg_dump version 11.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.2
-- Dumped by pg_dump version 11.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: module-db; Type: DATABASE; Schema: -; Owner: module-service
--

CREATE DATABASE "module-db" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';


ALTER DATABASE "module-db" OWNER TO "module-service";

\connect -reuse-previous=on "dbname='module-db'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: hops_module_mapping; Type: TABLE; Schema: public; Owner: module-service
--

CREATE TABLE public.hops_module_mapping (
                                          id uuid NOT NULL,
                                          kuerzel character varying(255),
                                          version character varying(255),
                                          module_id uuid
);


ALTER TABLE public.hops_module_mapping OWNER TO "module-service";

--
-- Name: hops_study_course_mapping; Type: TABLE; Schema: public; Owner: module-service
--

CREATE TABLE public.hops_study_course_mapping (
                                                id uuid NOT NULL,
                                                "study_course_kürzel" character varying(255),
                                                study_course_id uuid
);


ALTER TABLE public.hops_study_course_mapping OWNER TO "module-service";

--
-- Name: module; Type: TABLE; Schema: public; Owner: module-service
--

CREATE TABLE public.module (
                             id uuid NOT NULL,
                             description character varying(9000),
                             name character varying(255),
                             module_id uuid
);


ALTER TABLE public.module OWNER TO "module-service";

--
-- Name: study_course; Type: TABLE; Schema: public; Owner: module-service
--

CREATE TABLE public.study_course (
                                   id uuid NOT NULL,
                                   academic_degree integer,
                                   name character varying(255),
                                   parent_study_course_id uuid
);


ALTER TABLE public.study_course OWNER TO "module-service";

--
-- Name: study_course_modules; Type: TABLE; Schema: public; Owner: module-service
--

CREATE TABLE public.study_course_modules (
                                           study_course_id uuid NOT NULL,
                                           modules_id uuid NOT NULL
);


ALTER TABLE public.study_course_modules OWNER TO "module-service";

--
-- Name: hops_module_mapping hops_module_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.hops_module_mapping
  ADD CONSTRAINT hops_module_mapping_pkey PRIMARY KEY (id);


--
-- Name: hops_study_course_mapping hops_study_course_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.hops_study_course_mapping
  ADD CONSTRAINT hops_study_course_mapping_pkey PRIMARY KEY (id);


--
-- Name: module module_pkey; Type: CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.module
  ADD CONSTRAINT module_pkey PRIMARY KEY (id);


--
-- Name: study_course_modules study_course_modules_pkey; Type: CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.study_course_modules
  ADD CONSTRAINT study_course_modules_pkey PRIMARY KEY (study_course_id, modules_id);


--
-- Name: study_course study_course_pkey; Type: CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.study_course
  ADD CONSTRAINT study_course_pkey PRIMARY KEY (id);


--
-- Name: module fkrfvoljmatu1ueyxh2h2gi5shq; Type: FK CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.module
  ADD CONSTRAINT fkrfvoljmatu1ueyxh2h2gi5shq FOREIGN KEY (module_id) REFERENCES public.study_course(id);


--
-- Name: study_course fktajqw3u2hx2bjf491iovkl9pi; Type: FK CONSTRAINT; Schema: public; Owner: module-service
--

ALTER TABLE ONLY public.study_course
  ADD CONSTRAINT fktajqw3u2hx2bjf491iovkl9pi FOREIGN KEY (parent_study_course_id) REFERENCES public.study_course(id);


--
-- PostgreSQL database dump complete
--

\connect postgres

--
-- PostgreSQL database dump
--

-- Dumped from database version 11.2
-- Dumped by pg_dump version 11.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database cluster dump complete
--

