CREATE TABLE IF NOT EXISTS "module-db".flyway_schema_history (
                                                 installed_rank integer NOT NULL,
                                                 version character varying(50),
                                                 description character varying(200) NOT NULL,
                                                 type character varying(20) NOT NULL,
                                                 script character varying(1000) NOT NULL,
                                                 checksum integer,
                                                 installed_by character varying(100) NOT NULL,
                                                 installed_on timestamp without time zone DEFAULT now() NOT NULL,
                                                 execution_time integer NOT NULL,
                                                 success boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS public.hops_module_mapping (
                                          id uuid NOT NULL,
                                          kuerzel character varying(255),
                                          version character varying(255),
                                          module_id uuid
);

CREATE TABLE IF NOT EXISTS public.hops_module_mapping (
                                                        id uuid NOT NULL,
                                                        kuerzel character varying(255),
                                                        version character varying(255),
                                                        module_id uuid
);


CREATE TABLE IF NOT EXISTS public.hops_study_course_mapping (
                                                id uuid NOT NULL,
                                                "study_course_kürzel" character varying(255),
                                                study_course_id uuid
);


CREATE TABLE IF NOT EXISTS public.module (
                             id uuid NOT NULL,
                             description character varying(9000),
                             name character varying(255),
                             module_id uuid
);

CREATE TABLE IF NOT EXISTS public.study_course (
                                   id uuid NOT NULL,
                                   academic_degree integer,
                                   name character varying(255),
                                   parent_study_course_id uuid
);


CREATE TABLE IF NOT EXISTS public.study_course_modules (
                                           study_course_id uuid NOT NULL,
                                           modules_id uuid NOT NULL
);
