DROP TABLE IF EXISTS swingset_base_test_data;
DROP SEQUENCE IF EXISTS swingset_base_test_seq;

/* swingset_base_test_data */
CREATE SEQUENCE IF NOT EXISTS swingset_base_test_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS swingset_base_test_data 
( 
    swingset_base_test_pk INTEGER DEFAULT nextval('swingset_base_test_seq') NOT NULL PRIMARY KEY,
    ss_label VARCHAR(50),
    ss_list INTEGER ARRAY, /* ARRAY is typed for 2.x+, arbitrary range 2-8 */
    ss_text_field VARCHAR(100)
);

MERGE INTO swingset_base_test_data VALUES (1,'This is Label 1',ARRAY[1,2,3],'This is TextField 1') ;
MERGE INTO swingset_base_test_data VALUES (2,'This is Label 2',ARRAY[3,4,5],'This is TextField 2') ;
