/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2018, The Pangburn Group and Prasanth R. Pasala
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
   
DROP TABLE IF EXISTS supplier_data;
CREATE TABLE supplier_data 
( 
supplier_id INT AUTO_INCREMENT, 
supplier_name VARCHAR(50), 
status SMALLINT, 
city VARCHAR(50), 
PRIMARY KEY (supplier_id) 
);
insert into supplier_data values (2,'Jones',  10, 'Paris');
insert into supplier_data values (5,'Adams', 30, 'Athens');
insert into supplier_data values (4,'Clark', 20, 'London');
insert into supplier_data values (3,'Blake', 30, 'Paris');
insert into supplier_data values (1,'Smith',20,'London');
select * from supplier_data;
DROP TABLE IF EXISTS part_data;
CREATE TABLE part_data 
( 
part_id INT AUTO_INCREMENT, 
part_name VARCHAR(50), 
color_code SMALLINT, 
weight SMALLINT, 
city VARCHAR(50), 
PRIMARY KEY (part_id) 
);
insert into part_data values (6, 'Cog', 0, 19, 'London');
insert into part_data values (5, 'Cam', 2, 12, 'Paris',);
insert into part_data values (4, 'Screw', 0, 14, 'London');
insert into part_data values (3, 'Screw', 2, 17, 'Rome',);
insert into part_data values (2, 'Bolt', 1, 17, 'Paris');
insert into part_data values (1, 'Nut', 0, 12, 'London');
SELECT * FROM part_data;
DROP TABLE IF EXISTS supplier_part_data;
CREATE TABLE supplier_part_data 
( 
supplier_part_id INT AUTO_INCREMENT, 
supplier_id INT, 
part_id INT, 
quantity SMALLINT, 
ship_date DATE,
PRIMARY KEY (supplier_part_id), 
FOREIGN KEY (supplier_id) REFERENCES supplier_data(supplier_id), 
FOREIGN KEY (part_id) REFERENCES part_data(part_id), 
);
Insert into supplier_part_data values (8, 3, 2, 700, '2003-10-15');
Insert into supplier_part_data values (7, 2, 1, 400, '2004-09-15');
Insert into supplier_part_data values (4, 4, 6, 700, '2004-03-15');
Insert into supplier_part_data values (5, 5, 5, 100, '2004-03-25');
Insert into supplier_part_data values (1, 1, 4, 100, '2003-10-20');
Insert into supplier_part_data values (3, 3, 3, 400, '2003-01-05');
Insert into supplier_part_data values (2, 2, 2, 200, '2003-01-01');
Insert into supplier_part_data values (6, 1, 1, 700, '2003-12-12');
Insert into supplier_part_data values (9, 4, 3, 700, '2003-12-12');
Insert into supplier_part_data values (12, 2, 5, 400, '2003-12-12');
Insert into supplier_part_data values (11, 1, 4, 300, '2003-12-12');
Insert into supplier_part_data values (10, 5, 2, 200, '2003-12-12');
SELECT * FROM supplier_part_data;
