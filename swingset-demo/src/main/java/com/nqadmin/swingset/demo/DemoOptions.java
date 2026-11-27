/*******************************************************************************
 * Copyright (C) 2003-2021, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 *   Ernie R. Rael
 ******************************************************************************/
package com.nqadmin.swingset.demo;

import java.util.ArrayList;
import java.util.List;
import gnu.getopt.Getopt;

/** Immutable command-line configuration for one demo launch. */
record DemoOptions(boolean verbose, boolean dump, boolean noLoadImages,
                   boolean noInitializeDb, String propertyFile,
                   List<String> userSqlFiles, String database) {
  private static final String COMMAND = "SwingSetDemo";

  DemoOptions {
    userSqlFiles = List.copyOf(userSqlFiles);
  }

  static DemoOptions defaults() {
    return new DemoOptions(false, false, false, false, null, List.of(), "h2");
  }

  /** Returns null for an explicit help request; invalid arguments throw. */
  static DemoOptions parse(String[] args) {
    boolean verbose = false, dump = false, noLoadImages = false, noInitializeDb = false;
    String propertyFile = null;
    List<String> sqlFiles = new ArrayList<>();
    String[] parsedArgs = args.clone();
    Getopt g = new Getopt(COMMAND, parsedArgs, "hvdinp:s:");
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'h': return null;
        case 'v': verbose = true; break;
        case 'd': dump = true; break;
        case 'n': noInitializeDb = true; break;
        case 'i': noLoadImages = true; break;
        case 'p': propertyFile = g.getOptarg(); break;
        case 's': sqlFiles.add(g.getOptarg()); break;
        default: throw new IllegalArgumentException("Invalid command-line option.");
      }
    }
    int i = g.getOptind();
    boolean explicitDatabase = i < args.length;
    if (args.length - i > 1)
      throw new IllegalArgumentException("Only one database server may be specified.");
    // Getopt may permute its argument array. Use its returned non-option arguments.
    String database = explicitDatabase ? parsedArgs[i] : "h2";
    if (!sqlFiles.isEmpty()) {
      if (propertyFile == null)
        throw new IllegalArgumentException("-p required if -s is used.");
      if (explicitDatabase)
        throw new IllegalArgumentException("-s not allowed if database specified.");
      if (dump)
        throw new IllegalArgumentException("-d not allowed when -s is used.");
      database = "external";
    } else if (!database.equals("h2") && !database.equals("mysql")) {
      throw new IllegalArgumentException("Unknown database server '" + database + "'.");
    }
    if (verbose)
      System.err.println("\n" + COMMAND + " " + String.join(" ", args));
    return new DemoOptions(verbose, dump, noLoadImages, noInitializeDb, propertyFile,
                           sqlFiles, database);
  }

  static void usage() {
    String usage = """
        
        Run the SwingSet demo. With no options/args use the self contained
        in memory database.
        
        CMD_NAME [-h] [-v] [-d] [-n] [-i] [-p fname] [-s sql]* [dbms-server]
        
            -h             help
            -v             verbose; output initialization sql as executed
            -d             dump/create sql scripts in local directory, exit
            -n             do NOT initialize database, just run demo
            -i             do NOT load images
            -p fname       properties file for jdbc database connection
                           'DB_URL', 'DB_DRIVER_CLASS' keys required
            -s sqlScript   sql file to initialize database, multiple OK
        
        If specified, dbms-server in {h2, mysql}
        Internal mysql properties use database swingset_demo_suppliers_and_parts.
        After the sql files are run, the images are loaded, unless '-i'.
        Use '-n -p props' to run demo with a previously initialized database.
        Use '-d' or '-d mysql' to create local files with sql initialization.
        See swingset-demo/README.md for more information.
        
        Examples: (ss.jar like swingset-demo-vers-jar-with-dependencies.jar)
            java -jar ss.jar -d   # dump sql that creates in memory database
            java -jar ss.jar -d mysql   # dump sql to create mysql database
            java -cp jdbc_driver:ss.jar com.nqadmin.swingset.demo.MainClass \\
                -p db_props -s initializer.sql
        
        """;
    usage = usage.replace("CMD_NAME", COMMAND);
    System.err.println(usage);
  }

}
