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

-- drop database if exists swingset_demo_suppliers_and_parts;
-- CREATE DATABASE IF NOT EXISTS swingset_demo_suppliers_and_parts;
-- USE swingset_demo_suppliers_and_parts;

/* This SQL script is used to generate the sample swingset_demo_suppliers_and_parts
   database for the SwingSet sample programs */

DROP TABLE IF EXISTS supplier_part_data, part_data, supplier_data;

/* supplier_data */
--CREATE SEQUENCE IF NOT EXISTS supplier_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS supplier_data 
( 
    supplier_id INTEGER NOT NULL PRIMARY KEY,
    supplier_name VARCHAR(50), 
    status SMALLINT, 
    city VARCHAR(50)
);

INSERT INTO supplier_data VALUES (2,'Jonesy',  10, 'Paris') ,
                                 (5,'Adams', 30, 'Athens'),
                                 (4,'Clark', 20, 'London'),
                                 (3,'Blake', 30, 'Paris'),
                                 (1,'Smith',20,'London');

--SELECT * from supplier_data;


/* part_data */
--CREATE SEQUENCE IF NOT EXISTS part_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS part_data 
( 
    part_id INTEGER NOT NULL PRIMARY KEY,
    part_name VARCHAR(50), 
    color_code SMALLINT, 
    weight SMALLINT, 
    city VARCHAR(50)
);

INSERT INTO part_data VALUES (6, 'Cog', 0, 19, 'London'),
                             (5, 'Cam', 2, 12, 'Paris'),
                             (4, 'Screw', 0, 14, 'London'),
                             (3, 'Screw', 2, 17, 'Rome'),
                             (2, 'Bolt', 1, 17, 'Paris'),
                             (1, 'Nut', 0, 12, 'London');

--SELECT * FROM part_data;

/* supplier_part_data */
--CREATE SEQUENCE IF NOT EXISTS supplier_part_data_seq START WITH 1000;
CREATE TABLE IF NOT EXISTS supplier_part_data 
( 
    supplier_part_id INTEGER NOT NULL PRIMARY KEY,
    supplier_id INT, 
    part_id INT, 
    quantity SMALLINT, 
    ship_date DATE,
    FOREIGN KEY (supplier_id) REFERENCES supplier_data(supplier_id), 
    FOREIGN KEY (part_id) REFERENCES part_data(part_id)
);

INSERT INTO supplier_part_data VALUES (8, 3, 2, 700, '2003-10-15'),
                                      (7, 2, 1, 400, '2004-09-15'),
                                      (4, 4, 6, 700, '2004-03-15'),
                                      (5, 5, 5, 100, '2004-03-25'),
                                      (1, 1, 4, 100, '2003-10-20'),
                                      (3, 3, 3, 400, '2003-01-05'),
                                      (2, 2, 2, 200, '2003-01-01'),
                                      (6, 1, 1, 700, '2003-12-12'),
                                      (9, 4, 3, 700, '2003-12-12'),
                                      (12, 2, 5, 400, '2003-12-12'),
                                      (11, 1, 4, 300, '2003-12-12'),
                                      (10, 5, 2, 200, '2003-12-12');

--SELECT * FROM supplier_part_data;
