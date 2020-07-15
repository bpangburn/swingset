/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 ******************************************************************************/


/* This SQL script is used to add to the suppliers_and_parts database with
   some tables for testing individual SwingSet components.
 */

/* housekeeping */
DROP TABLE IF EXISTS swingset_base_test_data;
DROP SEQUENCE IF EXISTS swingset_base_test_seq;

/* swingset_base_test_data */
CREATE SEQUENCE IF NOT EXISTS swingset_base_test_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS swingset_base_test_data 
( 
    swingset_base_test_pk INTEGER DEFAULT nextval('swingset_base_test_seq') NOT NULL PRIMARY KEY,
	ss_check_box INTEGER,
	ss_combo_box INTEGER, /* arbitrary values 0-3 */
	ss_db_combo_box INTEGER, /* mapping to part_data, which has valid PK values of 1-6 by default */
	ss_image BLOB,
	ss_label VARCHAR(50),
	ss_list ARRAY, /* arbitrary range 2-8 */
	ss_slider INTEGER, /* arbitrary range 0-20 */
	ss_text_area VARCHAR(255),
	ss_text_field VARCHAR(100)
);

/* NOTE THAT THE FILE_READ COMMAND USED TO LOAD THE IMAGE BLOBS WILL ***NOT*** WORK FOR A CLIENT-SERVER DATABASE. IN-MEMORY DATABASE MUST BE USED */

MERGE INTO swingset_base_test_data VALUES (1,0,2,3,NULL,'This is Label 1',ARRAY[1,2,3],12,'This is Text Area 1','This is TextField 1') ;
MERGE INTO swingset_base_test_data VALUES (2,1,1,2,NULL,'This is Label 2',ARRAY[3,4,5],3,'This is Text Area 2. Image is NULL by default for this record.','This is TextField 2') ;
MERGE INTO swingset_base_test_data VALUES (3,1,0,1,NULL,'This is Label 3',ARRAY[4,5,1],8,'This is Text Area 3','This is TextField 3') ;
MERGE INTO swingset_base_test_data VALUES (4,0,3,4,NULL,'This is Label 4',ARRAY[1,5,4],15,'This is Text Area 4','This is TextField 4') ;
MERGE INTO swingset_base_test_data VALUES (5,1,2,5,NULL,'This is Label 5',ARRAY[5,3,4],20,'This is Text Area 5','This is TextField 5') ;
MERGE INTO swingset_base_test_data VALUES (6,0,1,6,NULL,'This is Label 6',ARRAY[1,3,4],1,'This is Text Area 6.','This is TextField 6') ;
MERGE INTO swingset_base_test_data VALUES (7,1,0,3,NULL,'This is Label 7',ARRAY[5,2,3],17,'This is Text Area 7','This is TextField 7') ;

/* SELECT * from swingset_base_test_data; */

