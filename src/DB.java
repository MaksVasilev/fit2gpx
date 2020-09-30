import format.DB_Append_Policy;
import format.DB_Create_Policy;
import format.Database;
import format.Mode;
import tools.FileCheck;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class DB {
    private TreeMap<String, Map<String,String>> Buffer = new TreeMap<>();
    private String[] fields;
    private Mode MODE = Mode.UNKNOWN;
    private Database DBASE = Database.SQLITE;
    private DB_Create_Policy create_policy = DB_Create_Policy.CREATE_IF_NOT_FOUND;
    private DB_Append_Policy append_policy = DB_Append_Policy.REPLACE_ALL;
    private String hash;
    private String db_connect;
    private String db_prefix;
    private Connection CONN;

    public DB(Mode mode) {
        setMode(mode);

    }

    public boolean connctDB(Database database, String db_connect, String db_prefix) {
        setDBtype(database);
        setDBconnect(db_connect);
        setDBprefix(db_prefix);

        switch (DBASE) {
            case SQLITE:
            FileCheck fileCheck = new FileCheck();

            if (fileCheck.FileCanWrite(db_connect)) {
                try {
                    CONN = DriverManager.getConnection("jdbc:sqlite:" + fileCheck.FullPath(db_connect));
                    return true;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if(create_policy == DB_Create_Policy.CREATE_IF_NOT_FOUND) {
                try {
                    CONN = DriverManager.getConnection("jdbc:sqlite:" + db_connect);
                    return createTables(CONN);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
            break;
            case POSTGRESQL:
                break;
            case NONE:
                return false;
        }
        return true;
    }

    private boolean executeSQL(Connection conn,String sql) {
        if(conn == null) { return false; }
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean createTables(Connection conn) {
        if(conn == null) { return false; }

        String sql = "CREATE TABLE IF NOT EXISTS persons ( name VARCHAR PRIMARY KEY UNIQUE ); INSERT INTO persons ( name ) VALUES ( '" + db_prefix + "' );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_HR_monitor ( date DATETIME NOT NULL, heart_rate INTEGER NOT NULL);\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_SPO2_monitor ( date DATETIME NOT NULL, SPO2 INTEGER NOT NULL);\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_GSI_monitor ( date DATETIME NOT NULL, GSI INTEGER NOT NULL, BODY_BATTERY INTEGER, DELTA INTEGER);\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_activities_HR_only ( date DATETIME NOT NULL, heart_rate INTEGER  NOT NULL, Duration TIME);\n";

        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_HR_monitor_date_idx ON " + db_prefix + "_HR_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_SPO2_monitor_date_idx ON " + db_prefix + "_SPO2_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_GSI_monitor_date_idx ON " + db_prefix + "_GSI_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_date_idx ON " + db_prefix + "_activities_HR_only (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_duration_idx ON " + db_prefix + "_activities_HR_only (time(Duration) ASC);\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity AS SELECT strftime('%Y-%m-%dT%H:%M:00', date) AS date, avg(heart_rate) AS heart_rate  FROM " + db_prefix + "_activities_HR_only GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity_profile AS SELECT strftime('%H:%M:%S', Duration) AS time, avg(heart_rate) as heart_rate, count(heart_rate) as count FROM " + db_prefix + "_activities_HR_only GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_full AS SELECT date, heart_rate FROM ( SELECT date, heart_rate FROM " + db_prefix + "_HR_monitor WHERE date NOT IN ( SELECT date FROM " + db_prefix + "_HR_activity ) UNION SELECT date, heart_rate FROM " + db_prefix + "_HR_activity ) ORDER BY date ASC;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(heart_rate), count(heart_rate) as count FROM " + db_prefix + "_HR_full GROUP BY 1  ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(heart_rate) AS heart_rate, count(heart_rate) as count FROM " + db_prefix + "_HR_full GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(SPO2) as SPO2, count(SPO2) as count FROM " + db_prefix + "_SPO2_monitor GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(SPO2) AS SPO2, count(SPO2) as count FROM " + db_prefix + "_SpO2_monitor GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(GSI) as GSI, count(GSI) as count FROM " + db_prefix + "_GSI_monitor GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(GSI) AS GSI, count(GSI) as count FROM " + db_prefix + "_GSI_monitor GROUP BY 1 ORDER BY 1;\n";

        return executeSQL(conn,sql);

    }

    public void setFields(String[] fields) { this.fields = fields; }
    public void setCreatePolicy(DB_Create_Policy policy) { create_policy = policy; }
    public void setAppendPolicy(DB_Append_Policy policy) { append_policy = policy; }
    public void setBuffer(TreeMap<String, Map<String,String>> fb) { this.Buffer = fb; }

    private void setDBtype(Database dbase) { this.DBASE = dbase; }
    private void setDBconnect(String db) { this.db_connect = db; }
    private void setMode(Mode m) { this.MODE = m; }
    private void setHash(String hash) { this.hash = hash; }
    private void setDBprefix(String db) {
        if(db.equals("")) {
            this.db_prefix = "_no_person";
        } else this.db_connect = db;
    }

}
