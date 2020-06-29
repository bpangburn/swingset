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


/* This SQL script is used to generate the sample suppliers_and_parts database
   for the SwingSet sample programs */

/* housekeeping */
DROP TABLE IF EXISTS supplier_part_data;
DROP SEQUENCE IF EXISTS supplier_part_data_seq;
DROP TABLE IF EXISTS supplier_data;
DROP SEQUENCE IF EXISTS supplier_data_seq;
DROP TABLE IF EXISTS part_data;
DROP SEQUENCE IF EXISTS part_data_seq;

/* supplier_data */
CREATE SEQUENCE IF NOT EXISTS supplier_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS supplier_data 
( 
    supplier_id INTEGER DEFAULT nextval('supplier_data_seq') NOT NULL PRIMARY KEY,
    supplier_name VARCHAR(50), 
    status SMALLINT, 
    city VARCHAR(50)
);

MERGE INTO supplier_data VALUES (2,'Jones',  10, 'Paris') ;
MERGE INTO supplier_data VALUES (5,'Adams', 30, 'Athens');
MERGE INTO supplier_data VALUES (4,'Clark', 20, 'London');
MERGE INTO supplier_data VALUES (3,'Blake', 30, 'Paris');
MERGE INTO supplier_data VALUES (1,'Smith',20,'London');

SELECT * from supplier_data;


/* part_data */
CREATE SEQUENCE IF NOT EXISTS part_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS part_data 
( 
    part_id INTEGER DEFAULT nextval('part_data_seq') NOT NULL PRIMARY KEY,
    part_name VARCHAR(50), 
    color_code SMALLINT, 
    weight SMALLINT, 
    city VARCHAR(50)
);

MERGE INTO part_data VALUES (6, 'Cog', 0, 19, 'London');
MERGE INTO part_data VALUES (5, 'Cam', 2, 12, 'Paris');
MERGE INTO part_data VALUES (4, 'Screw', 0, 14, 'London');
MERGE INTO part_data VALUES (3, 'Screw', 2, 17, 'Rome');
MERGE INTO part_data VALUES (2, 'Bolt', 1, 17, 'Paris');
MERGE INTO part_data VALUES (1, 'Nut', 0, 12, 'London');

SELECT * FROM part_data;

/* supplier_part_data */
CREATE SEQUENCE IF NOT EXISTS supplier_part_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS supplier_part_data 
( 
    supplier_part_id INTEGER DEFAULT nextval('supplier_part_data_seq') NOT NULL PRIMARY KEY,
    supplier_id INT, 
    part_id INT, 
    quantity SMALLINT, 
    ship_date DATE,
    FOREIGN KEY (supplier_id) REFERENCES supplier_data(supplier_id), 
    FOREIGN KEY (part_id) REFERENCES part_data(part_id)
);

MERGE INTO supplier_part_data VALUES (8, 3, 2, 700, '2003-10-15');
MERGE INTO supplier_part_data VALUES (7, 2, 1, 400, '2004-09-15');
MERGE INTO supplier_part_data VALUES (4, 4, 6, 700, '2004-03-15');
MERGE INTO supplier_part_data VALUES (5, 5, 5, 100, '2004-03-25');
MERGE INTO supplier_part_data VALUES (1, 1, 4, 100, '2003-10-20');
MERGE INTO supplier_part_data VALUES (3, 3, 3, 400, '2003-01-05');
MERGE INTO supplier_part_data VALUES (2, 2, 2, 200, '2003-01-01');
MERGE INTO supplier_part_data VALUES (6, 1, 1, 700, '2003-12-12');
MERGE INTO supplier_part_data VALUES (9, 4, 3, 700, '2003-12-12');
MERGE INTO supplier_part_data VALUES (12, 2, 5, 400, '2003-12-12');
MERGE INTO supplier_part_data VALUES (11, 1, 4, 300, '2003-12-12');
MERGE INTO supplier_part_data VALUES (10, 5, 2, 200, '2003-12-12');

SELECT * FROM supplier_part_data;
