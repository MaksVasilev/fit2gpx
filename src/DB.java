import format.DB_Append_Policy;
import format.DB_Create_Policy;
import format.Database;
import format.Mode;
import tools.FileCheck;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class DB {
    private TreeMap<String, Map<String,String>> Buffer = new TreeMap<>();
    private String[] fields;
    private Mode MODE = Mode.UNKNOWN;
    private Database DBASE = Database.SQLITE;
    private DB_Create_Policy create_policy = DB_Create_Policy.CREATE_IF_NOT_FOUND;
    private DB_Append_Policy append_policy = DB_Append_Policy.APPEND_NEW_NO_REPLACE;
    private String hash;
    private String db_connect;
    private String db_prefix;
    private Connection CONN;
    private boolean xDebug = false;

    private void setDebug(boolean b) {xDebug = b; }
    private void setDBtype(Database dbase) { this.DBASE = dbase; }
    private void setDBconnect(String db) { this.db_connect = db; }
    private void setMode(Mode m) { this.MODE = m; }
    private void setHash(String hash) { this.hash = hash; }
    private void setDBprefix(String prefix) {
        if(prefix.equals("")) {
            this.db_prefix = "_no_person";
        } else this.db_prefix = prefix;
    }

    public void setFields(String[] fields) { this.fields = fields; }
    public void setCreatePolicy(DB_Create_Policy policy) { create_policy = policy; }
    public void setAppendPolicy(DB_Append_Policy policy) { append_policy = policy; }
    public void setBuffer(TreeMap<String, Map<String,String>> fb) { this.Buffer = fb; }

    public DB(Mode mode, boolean debug) {
        setMode(mode);
        setDebug(debug);
    }

    public boolean connctDB(Database dbase, String db_conn, String db_pref) {
        setDBtype(dbase);
        setDBconnect(db_conn);
        setDBprefix(db_pref);

        switch (DBASE) {
            case SQLITE:
            FileCheck fileCheck = new FileCheck();

            if (fileCheck.FileCanWrite(db_connect)) {
                try {
                    CONN = DriverManager.getConnection("jdbc:sqlite:" + fileCheck.FullPath(db_connect));
                    if (xDebug) System.out.println("[DB:] Connect exist: " + db_connect + ", prefix: " + db_prefix);
                    checkSchema();

                    return CONN != null;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if(create_policy == DB_Create_Policy.CREATE_IF_NOT_FOUND) {
                try {
                    CONN = DriverManager.getConnection("jdbc:sqlite:" + db_connect);
                    if (xDebug) System.out.println("[DB:] Connect new (created): " + db_connect + ", prefix: " + db_prefix);
                    if(CONN != null) {  return createTables();
                    } else return false;
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

    private boolean checkSchema() {
        String sql = "SELECT ROWID FROM persons WHERE name=\"" + db_prefix + "\";";
        try (
                Statement stmt = CONN.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst() ) {
                if(xDebug) System.out.println("[DB:] Prefix not found, create schema");
                createMonitorSchema();
            } else {
                if(xDebug) System.out.println("[DB:] Prefix exist, ID: " + rs.getInt("ROWID"));
            }

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean executeSQL(Connection conn,String sql) {
        if(conn == null || sql.equals("")) { return false; }
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean createTables() {
        return createMonitorSchema();
    }

    public int push() {
        if (xDebug) System.out.println("[DB:] Push mode: " + MODE + ", Buffer size: " + Buffer.size() + ", prefix: " + db_prefix);

        if(Buffer.size() == 0) { return 89; }

        String table = "";
        String[] fields;

        switch (MODE) {
            case MONITOR_HR:
                table = db_prefix + "_HR_monitor";
                fields = new String[]{"date","heart_rate"};
                break;
            case MONITOR_SPO2:
                table = db_prefix + "_SPO2_monitor";
                fields = new String[]{"date","SPO2"};
                break;
            case MONITOR_GSI:
                table = db_prefix + "_GSI_monitor";
                fields = new String[]{"date","GSI","BODY_BATTERY","DELTA"};
                break;
            case CSV_HR:
                table = db_prefix + "_activities_HR_only";
                fields = new String[]{"date","heart_rate","duration"};
                break;
            default:
                return 81;
        }

        Transaction(CONN, Status.BEGIN);
        for(Map.Entry<String, Map<String,String>> mapEntry:Buffer.entrySet()) {
            StringBuilder sql = new StringBuilder("INSERT");
            switch (append_policy) {
                case REPLACE_ALL:
                    sql.append(" OR REPLACE");
                    break;
                case APPEND_NEW_NO_REPLACE:
                default:
                    sql.append(" OR IGNORE");
                    break;
            }

            sql.append(" INTO ").append(table).append("(").append(Arrays.toString(fields).replace("[", "").replace("]", "").trim()).append(") VALUES ('").append(mapEntry.getKey()).append("'");
            for (int i = 1; i < fields.length; i++) {
                String field = fields[i];
                sql.append(",'").append(mapEntry.getValue().get(field)).append("'");
            }
            sql.append(");");
            executeSQL(CONN, sql.toString());
        }
        Transaction(CONN, Status.COMMIT);
        return 0;
    }

    private boolean createMonitorSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS persons ( name VARCHAR PRIMARY KEY UNIQUE ); INSERT INTO persons ( name ) VALUES ( '" + db_prefix + "' );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_HR_monitor ( date DATETIME NOT NULL, heart_rate INTEGER NOT NULL, UNIQUE(date) );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_SPO2_monitor ( date DATETIME NOT NULL, SPO2 INTEGER NOT NULL, UNIQUE(date) );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_GSI_monitor ( date DATETIME NOT NULL, GSI INTEGER NOT NULL, BODY_BATTERY INTEGER, DELTA INTEGER, UNIQUE(date) );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_activities_HR_only ( date DATETIME NOT NULL, heart_rate INTEGER NOT NULL, duration TIME, UNIQUE(date) );\n";

        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_HR_monitor_date_idx ON " + db_prefix + "_HR_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_SPO2_monitor_date_idx ON " + db_prefix + "_SPO2_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_GSI_monitor_date_idx ON " + db_prefix + "_GSI_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_date_idx ON " + db_prefix + "_activities_HR_only (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_duration_idx ON " + db_prefix + "_activities_HR_only (time(duration) ASC);\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity AS SELECT strftime('%Y-%m-%dT%H:%M:00', date) AS date, avg(heart_rate) AS heart_rate  FROM " + db_prefix + "_activities_HR_only GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity_profile AS SELECT strftime('%H:%M:%S', duration) AS time, avg(heart_rate) as heart_rate, count(heart_rate) as count FROM " + db_prefix + "_activities_HR_only GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_full AS SELECT date, heart_rate FROM ( SELECT date, heart_rate FROM " + db_prefix + "_HR_monitor WHERE date NOT IN ( SELECT date FROM " + db_prefix + "_HR_activity ) UNION SELECT date, heart_rate FROM " + db_prefix + "_HR_activity ) ORDER BY date ASC;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(heart_rate) as heart_rate, count(heart_rate) as count FROM " + db_prefix + "_HR_full GROUP BY 1  ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(heart_rate) AS heart_rate, count(heart_rate) as count FROM " + db_prefix + "_HR_full GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(SPO2) as SPO2, count(SPO2) as count FROM " + db_prefix + "_SPO2_monitor GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(SPO2) AS SPO2, count(SPO2) as count FROM " + db_prefix + "_SpO2_monitor GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(GSI) as GSI, count(GSI) as count FROM " + db_prefix + "_GSI_monitor GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(GSI) AS GSI, count(GSI) as count FROM " + db_prefix + "_GSI_monitor GROUP BY 1 ORDER BY 1;\n";

        return executeSQL(CONN,sql);
    }

    private static void Transaction(Connection conn, Status status) {
        try {
            Statement stmt = conn.createStatement();
            switch (status) {
                case BEGIN:
                    stmt.executeUpdate("BEGIN TRANSACTION;");
                    break;
                case COMMIT:
                    stmt.executeUpdate("COMMIT;");
                    break;
                case ABORT:
                    stmt.executeUpdate("ROLLBACK;");
                    break;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private enum Status {BEGIN, COMMIT, ABORT};

}