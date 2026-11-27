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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import org.h2.tools.RunScript;
import com.nqadmin.swingset.utils.SSUtils;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import dev.visdb.seesaw.models.DbCollection;
import dev.visdb.seesaw.models.DbStringCollection;
import dev.visdb.seesaw.utils.JStuff;

/** Database setup for the demo, separate from Swing window construction. */
final class DemoDatabase {
  private static final Logger logger = JStuff.getLogger();
  private DemoDatabase() {}

  record Options(boolean verbose, boolean noLoadImages, boolean noInitializeDb,
                 String propertyFile) {}

  private static final String DATABASE_PATH = "//localhost/~/h2/databases/";

  private static final boolean USE_IN_MEMORY_DATABASE = true;

  private static final String DATABASE_SCRIPT_DEMO = "suppliers_and_parts.sql";
  private static final String DATABASE_SCRIPT_TEST = "swingset_tests.sql";
  private static final String DATABASE_SCRIPT_TEST_IMAGES = "swingset_tests_load_blobs.sql";
  private static final String DATABASE_SCRIPT_DEBUG = "swingset_debug.sql";

  private static final boolean RUN_DEMO_SQL_SCRIPTS = true;
  private static final boolean RUN_TEST_SQL_SCRIPTS = true;
  private static final boolean RUN_DEBUG_SQL_SCRIPTS = false;

  static abstract class DatabaseSetup implements Runnable {
    private Connection conn;
    final Options options;

    DatabaseSetup(Options options) {
      this.options = options;
    }

    abstract Properties getDatabaseProperties();

    abstract List<String> getScripts();

    List<String> getDumpScripts() { return getScripts(); }

    boolean executeScript(Connection connection, BufferedReader reader) throws SQLException {
      return DemoUtil.runSqlStatements(connection, reader, options.verbose());
    }

    boolean shouldLoadImages() { return !options.noLoadImages(); }

    Connection getConnection() {
      return conn;
    }

    /**
     * Find resource; an alternate implementation could take the resource as a file.
     *
     * @param resourceName name of resource
     * @return BufferReader for specified resource
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    BufferedReader getBufferedReader(String resourceName) {
      InputStream stream = DemoDatabase.class.getResourceAsStream(resourceName);
      if (stream == null) {
        System.err.println("Script '" + resourceName + "' not found. Exiting.");
        return null;
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      return br;
    }

    @Override
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void run() {
      Properties info = getDatabaseProperties();
      if (info == null) {
        return;
      }
      String clazz = info.getProperty("DB_DRIVER_CLASS", "");

      if (clazz.isEmpty()) {
        System.err.println("'DB_DRIVER_CLASS' not found in property file");
        conn = null;
        return;
      }

      try {
        Class.forName(clazz);
      } catch (ClassNotFoundException ex) {
        System.err.println("Class '" + clazz + "' not found");
        conn = null;
        return;
      }

      conn = DemoUtil.getConnection(info.getProperty("DB_URL"), info);
      if (conn == null) {
        return;
      }

      if (options.noInitializeDb()) {
        return;
      }

      boolean ok = true;
      for (String script : getScripts()) {
        ok = false;
        try (BufferedReader br = getBufferedReader(script)) {
          if (br != null) {
            ok = executeScript(conn, br);
          }
        } catch (IOException | SQLException ex) {
          ok = false;
          logger.log(Level.ERROR, "Error executing demo script.", ex);
        }
        if (!ok) {
          break;
        }
      }

      if (ok && shouldLoadImages()) {
        // Load up the images
        String sql = "UPDATE swingset_base_test_data"
                     + " SET ss_image = ? WHERE swingset_base_test_pk = ?";
        ok = DemoUtil.loadBinaries(conn, "/swingset-demo-images.txt", sql, options.verbose());
      }

      if (!ok) {
        try {
          conn.close();
        } catch (SQLException ex) {
          logger.log(Level.WARNING, "Error closing failed demo connection.", ex);
        }
        conn = null;
      }
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    Properties getFileProperties(String fname) {
      try {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(fname)) {
          props.load(reader);
        }
        return props;
      } catch (FileNotFoundException ex) {
        System.err.println("Property file '" + fname + "' not found");
      } catch (IOException ex) {
        logger.log(Level.ERROR, "IO exception.", ex);
      }
      return null;
    }

    void dump() {
      for (String script : getDumpScripts()) {
        boolean ok = false;
        try (BufferedReader br = getBufferedReader(script)) {
          if (br != null) {
            String name = "dump." + new File(script).getName();
            try (FileWriter fout = new FileWriter(name)) {
              char buf[] = new char[4 * 1024];
              int n;
              while ((n = br.read(buf)) > 0) {
                fout.write(buf, 0, n);
              }
            }
            ok = true;
          }
        } catch (IOException ex) {
          logger.log(Level.ERROR, "IO exception.", ex);
        }
        if (!ok) {
          break;
        }
      }
    }
  }

  static class ExternalSetup extends DatabaseSetup {
    List<String> sqlFiles;

    public ExternalSetup(Options options, List<String> _sqlFiles) {
      super(options);
      sqlFiles = List.copyOf(_sqlFiles);
    }

    @Override
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    BufferedReader getBufferedReader(String fileName) {
      try {
        return new BufferedReader(new FileReader(fileName));
      } catch (FileNotFoundException ex) {
        System.err.println("Script '" + fileName + "' not found. Exiting.");
        return null;
      }
    }

    @Override
    Properties getDatabaseProperties() {
      Properties info = getFileProperties(options.propertyFile());
      return info;
    }

    @Override
    List<String> getScripts() {
      return sqlFiles;
    }
  }

  static class H2Setup extends DatabaseSetup {
    H2Setup(Options options) { super(options); }

    @Override
    Properties getDatabaseProperties() {
      if (options.propertyFile() != null) {
        return getFileProperties(options.propertyFile());
      }
      Properties info = new Properties();
      info.setProperty("DB_DRIVER_CLASS", "org.h2.Driver");
      if (USE_IN_MEMORY_DATABASE) {
        MainClass.H2Trace trace = SSUtils.getOption(MainClass.H2Trace.class);
        info.setProperty("DB_URL", "jdbc:h2:mem:" + MainClass.DATABASE_NAME
            + (trace == null ? "" : trace.getTraceUrlFlags()));
      } else {
        info.setProperty("DB_URL", "jdbc:h2:tcp:" + DATABASE_PATH + MainClass.DATABASE_NAME);
        info.setProperty("user", "sa");
        info.setProperty("password", "");
      }
      return info;
    }

    @Override
    List<String> getScripts() {
      List<String> scripts = new ArrayList<>();
      if (RUN_DEMO_SQL_SCRIPTS) scripts.add("/" + DATABASE_SCRIPT_DEMO);
      if (RUN_TEST_SQL_SCRIPTS) scripts.add("/" + DATABASE_SCRIPT_TEST);
      if (RUN_DEBUG_SQL_SCRIPTS) scripts.add("/" + DATABASE_SCRIPT_DEBUG);
      return scripts;
    }

    @Override
    List<String> getDumpScripts() {
      return List.of("/" + DATABASE_SCRIPT_DEMO, "/" + DATABASE_SCRIPT_TEST,
          "/" + DATABASE_SCRIPT_TEST_IMAGES);
    }

    @Override
    boolean executeScript(Connection connection, BufferedReader reader) throws SQLException {
      RunScript.execute(connection, reader);
      return true;
    }

    @Override
    boolean shouldLoadImages() {
      return RUN_TEST_SQL_SCRIPTS && super.shouldLoadImages();
    }
  }

  static class MysqlSetup extends DatabaseSetup {
    private final Map<String, Object> globalHints;

    MysqlSetup(Options options, Map<String, Object> globalHints) {
      super(options);
      this.globalHints = globalHints;
    }

    @Override
    public void run() {
      super.run();
      globalHints.put(
          "collectionModel",
          (Supplier<DbCollection>) ()
              -> new DbStringCollection(JDBCType.INTEGER, DbStringCollection.COMMA_SEP));
      // () -> new SSStringArrayModel(JDBCType.INTEGER));
    }

    @Override
    Properties getDatabaseProperties() {
      // <dependency>
      // <groupId>mysql</groupId>
      // <artifactId>mysql-connector-java</artifactId>
      // <version>8.0.21</version>
      // </dependency>
      Properties info;
      if (options.propertyFile() != null) {
        info = getFileProperties(options.propertyFile());
      } else {
        info = new Properties();
        info.put("DB_DRIVER_CLASS", "com.mysql.cj.jdbc.Driver");
        info.put("DB_URL", "jdbc:mysql://localhost/swingset_demo_suppliers_and_parts");
        // DB_NAME NOT USED
        info.put("DB_NAME", "swingset_demo_suppliers_and_parts");
        info.put("user", "root");
        info.put("serverTimezone", "UTC");
      }
      return info;
    }

    @Override
    List<String> getScripts() {
      return Arrays.asList(
          new String[] {"/mysql.swingset-demo-app.sql", "/mysql.swingset-demo-components.sql"});
    }
  }

}
