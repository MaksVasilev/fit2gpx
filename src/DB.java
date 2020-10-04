/*
Copyright © 2015-2020 by Maks Vasilev

created 7.02.2015
http://velo100.ru/garmin-fit-to-gpx
https://github.com/MaksVasilev/fit2gpx

*/

import format.DB_Append_Policy;
import format.DB_Create_Policy;
import format.Database;
import format.Mode;
import tools.FileCheck;

import java.sql.*;
import java.util.*;

public class DB {

    static ResourceBundle tr = ResourceBundle.getBundle("locale/tr", Locale.getDefault());

    private TreeMap<String, Map<String,String>> Buffer = new TreeMap<>();
    private String[] fields;
    private Mode MODE = Mode.UNKNOWN;
    private Database DBASE = Database.SQLITE;
    private DB_Create_Policy create_policy = DB_Create_Policy.CREATE_IF_NOT_FOUND;
    private DB_Append_Policy append_policy = DB_Append_Policy.APPEND_NEW_NO_REPLACE;
    private String activityhash;
    private String db_connect;
    private String db_prefix;
    private Connection CONN;
    private boolean xDebug = false;
    private boolean use_only_exist_schema = false;

    private void setDebug(boolean b) {xDebug = b; }
    private void setDBtype(Database dbase) { this.DBASE = dbase; }
    private void setDBconnect(String db) { this.db_connect = db; }
    private void setHash(String hash) { this.activityhash = hash; }
    private void setDBprefix(String prefix) {
        if(prefix.equals("")) {
            this.db_prefix = "_no_person";
        } else this.db_prefix = prefix;
    }

    public void useOnlyExistSchema(boolean b) { use_only_exist_schema = b; }
    public void setMode(Mode m) { this.MODE = m; }
    public void setFields(String[] fields) { this.fields = fields; }
    public void setCreatePolicy(DB_Create_Policy policy) { create_policy = policy; }
    public void setAppendPolicy(DB_Append_Policy policy) { append_policy = policy; }
    public void setBuffer(TreeMap<String, Map<String,String>> fb) { this.Buffer = fb; }

    public DB(boolean debug) {
        setDebug(debug);
    }

    public boolean connectDB(Database dbase, String db_conn, String db_pref) {
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
                    if(!checkSchema()) return false;

                    return CONN != null;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if(create_policy == DB_Create_Policy.CREATE_IF_NOT_FOUND && !use_only_exist_schema) {
                try {
                    CONN = DriverManager.getConnection("jdbc:sqlite:" + db_connect);
                    if (xDebug) System.out.println("[DB:] Connect new (created): " + db_connect + ", prefix: " + db_prefix);
                    if(CONN != null) {  return createTables();
                    } else return false;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if(use_only_exist_schema) {
                System.out.println(tr.getString("DB_error_no_db") + db_connect + ", " + db_prefix);
                System.exit(13);
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
        int person_id = getPersonID( db_prefix );
        if(person_id < 1) {
            if(use_only_exist_schema) {
                System.out.println(tr.getString("DB_error_no_schema") + db_prefix);
                System.exit(13);
            }
            if(xDebug) System.out.println("[DB:] Prefix not found, try to create schema");
            if(!insertPerson(db_prefix)) return false;
            return (createMonitorSchema() && createHrvSchema());
        } else {
            if(xDebug) System.out.println("[DB:] Prefix exist, ID: " + person_id);
            return true;
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


    public int push(String hash, String activityDateTime, ArrayList<String> taglist) {
        setHash(hash);

        if (xDebug) System.out.println("[DB:] Push mode: " + MODE + ", Buffer size: " + Buffer.size() + ", prefix: " + db_prefix);

        if(Buffer.size() == 0) { return 89; }
        if(fields == null) { return 82; }

        String table;
        StringBuilder tags = new StringBuilder();

        for(String s:taglist) {
            tags.append(s).append(",");
        }

        Integer serial = 0;

        switch (MODE) {
            case MONITOR_HR:
            case MONITOR_GSI:
            case MONITOR_SPO2:  table = db_prefix + "_monitor"; break;
            case CSV_HR:        table = db_prefix + "_activities_HR_only"; break;
            case HRV:           table = db_prefix + "_HRV"; serial = getSerial(SerialType.HRV, activityhash, activityDateTime, tags.toString()); break;
            case GPX: case CSV: table = db_prefix + "_activities"; serial = getSerial(SerialType.ACTIVITY, activityhash, activityDateTime, tags.toString()); break;
            default:            return 81;
        }

        Transaction(CONN, Status.BEGIN);
        for(Map.Entry<String, Map<String,String>> mapEntry:Buffer.entrySet()) {
            StringBuilder sql = new StringBuilder("INSERT");

            ArrayList<String> field_names = new ArrayList<>();
            ArrayList<String> field_values = new ArrayList<>();
            for (String field : fields) {
                if(field.equals("serial")) {
                    field_names.add("serial");
                    field_values.add(String.valueOf(serial));
                }
                if (mapEntry.getValue().get(field) != null && !mapEntry.getValue().get(field).equals("")) {
                    field_names.add(field);
                    field_values.add(mapEntry.getValue().get(field));
                }
            }

            sql.append(" INTO ").append(table).append("(date,").append(ListToString(field_names)).append(") VALUES ('");
            sql.append(mapEntry.getKey()).append("','");
            sql.append(ListToValuesString(field_values)).append("')");
            sql.append(" ON CONFLICT ");

            if (MODE == Mode.HRV) {
                sql.append("(date,serial)");
            } else {
                sql.append("(date)");
            }

            String SQL = "";
            switch (append_policy) {
                case REPLACE_ALL:
                    sql.append(" DO UPDATE SET ");
                    for(int i = 0; i< field_names.size();i++) {
                        if(!(field_names.get(i).equals("serial") && MODE == Mode.HRV)) {
                            sql.append(" ").append(field_names.get(i)).append(" = EXCLUDED.").append(field_names.get(i)).append(",");
                        }
                    }
                    SQL = sql.toString();
                    SQL = removeLastSeparator(SQL);
                    break;
                case APPEND_NEW_NO_REPLACE:
                default:
                    sql.append(" DO NOTHING");
                    SQL = sql.toString();
                    break;
            }
            SQL += ";";

            // INSERT INTO Maks_monitor (date, GSI) VALUES ("2020-10-04T16:39:00", 15) ON CONFLICT (date) DO UPDATE SET GSI = EXCLUDED.GSI;

//            System.out.println(SQL);
            if(!executeSQL(CONN, SQL)) {
                Transaction(CONN, Status.ABORT);
                return 83;
            };
        }
        Transaction(CONN, Status.COMMIT);
        return 0;
    }

    private String removeLastSeparator(String s) {
        if(s.equals("")) return "";
        if(s.charAt(s.length() - 1) == ',') s = s.substring(0, s.length() - 1);
        return s;
    }

    private String ListToString(ArrayList<String> list) {
        if(list.size() > 0) return Arrays.toString(list.toArray()).replace("[", "").replace("]", "").replace(" ","").trim();
        else return "";
    }

    private String ListToValuesString(ArrayList<String> list) {
        if(list.size() > 0) return Arrays.toString(list.toArray()).replace("[", "").replace("]", "").replace(",","','").replace(" ","").trim();
        else return "";
    }

    private enum SerialType { HRV, ACTIVITY }

    private int getSerial(SerialType type, String hash, String activityDateTime, String tags) {
        String serial_table = "";
        switch (type) {
            case HRV: serial_table = "_hrv"; break;
            case ACTIVITY: serial_table = "_activity"; break;
        }

        String sql = "SELECT ROWID FROM " + serial_table + " WHERE hash = '" + hash + "';";
        tags = removeLastSeparator(tags);   // remove last separator
        try {
            Statement stmt = CONN.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst() ) {
                sql = "INSERT OR REPLACE INTO " + serial_table + "(date, person_id, hash, tags) VALUES('" + activityDateTime + "','" + getPersonID(db_prefix) + "','" + hash + "','" + tags + "');";
                if(executeSQL(CONN, sql)) {
                    return getSerial(type, hash, activityDateTime, tags);
                } else return 0;
            } else {
                return rs.getInt("ROWID");
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private int getPersonID(String person) {
        String sql = "SELECT ROWID FROM _persons WHERE name = '" + person + "';";
        try {
            Statement stmt = CONN.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst() ) {
                return 0;
            } else {
                return rs.getInt("ROWID");
            }
            } catch(SQLException e) {
                System.out.println(e.getMessage());
                return 0;
            }
    }

    private boolean insertPerson(String person) {
        String sql = "INSERT INTO _persons ( name ) VALUES ( '" + person + "' );\n";
        if(xDebug) System.out.println("[DB:] Add new person: " + person);
        return executeSQL(CONN, sql);
    }

    private boolean createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS _persons ( name VARCHAR PRIMARY KEY UNIQUE );\n";
        sql += "CREATE TABLE IF NOT EXISTS _hrv (date DATETIME, person_id INTEGER, hash VARCHAR, tags TEXT, UNIQUE(hash) );\n";
        sql += "CREATE TABLE IF NOT EXISTS _activity (date DATETIME, person_id INTEGER, hash VARCHAR, tags TEXT, UNIQUE(hash) );\n";

        if(!executeSQL(CONN, sql)) return false;
        return checkSchema();
    }

    private boolean createMonitorSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS " + db_prefix + "_monitor ( date DATETIME NOT NULL, heart_rate INTEGER, SPO2 INTEGER, GSI INTEGER, BODY_BATTERY INTEGER, DELTA INTEGER, gsi_227_4 INTEGER, UNIQUE(date) );\n";
        sql += "CREATE TABLE IF NOT EXISTS " + db_prefix + "_activities_HR_only ( date DATETIME NOT NULL, heart_rate INTEGER NOT NULL ON CONFLICT IGNORE, duration TIME, UNIQUE(date) );\n";

        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_monitor_date_idx ON " + db_prefix + "_monitor (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_date_idx ON " + db_prefix + "_activities_HR_only (datetime(date) ASC);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_activities_HR_only_duration_idx ON " + db_prefix + "_activities_HR_only (time(duration) ASC);\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity AS SELECT strftime('%Y-%m-%dT%H:%M:00', date) AS date, avg(heart_rate) AS heart_rate  FROM " + db_prefix + "_activities_HR_only WHERE heart_rate NOT NULL GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_activity_profile AS SELECT strftime('%H:%M:%S', duration) AS time, avg(heart_rate) as heart_rate, count(heart_rate) as count FROM " + db_prefix + "_activities_HR_only GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_full AS SELECT date, heart_rate FROM ( SELECT date, heart_rate FROM " + db_prefix + "_monitor WHERE heart_rate NOT NULL AND date NOT IN ( SELECT date FROM " + db_prefix + "_HR_activity WHERE heart_rate NOT NULL) UNION SELECT date, heart_rate FROM " + db_prefix + "_HR_activity ) WHERE heart_rate NOT NULL ORDER BY date ASC;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(heart_rate) as heart_rate, count(heart_rate) as count FROM " + db_prefix + "_HR_full WHERE heart_rate NOT NULL GROUP BY 1  ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_HR_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(heart_rate) AS heart_rate, count(heart_rate) as count FROM " + db_prefix + "_HR_full WHERE heart_rate NOT NULL GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(SPO2) as SPO2, count(SPO2) as count FROM " + db_prefix + "_monitor WHERE SPO2 NOT NULL GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_SPO2_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(SPO2) AS SPO2, count(SPO2) as count FROM " + db_prefix + "_monitor WHERE SPO2 NOT NULL GROUP BY 1 ORDER BY 1;\n";

        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_day_profile AS SELECT strftime('%H:%M', date) AS time, avg(GSI) as GSI, count(GSI) as count FROM " + db_prefix + "_monitor WHERE GSI NOT NULL GROUP BY 1 ORDER BY 1;\n";
        sql += "CREATE VIEW IF NOT EXISTS " + db_prefix + "_GSI_by_day AS SELECT strftime('%Y-%m-%d', date) AS date, avg(GSI) AS GSI, count(GSI) as count FROM " + db_prefix + "_monitor WHERE GSI NOT NULL GROUP BY 1 ORDER BY 1;\n";

        return executeSQL(CONN, sql);
    }

    private boolean createHrvSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS " + db_prefix + "_HRV ( serial INTEGER NOT NULL, date DATETIME NOT NULL, time INTEGER NOT NULL, RR DOUBLE, HR DOUBLE, filter INTEGER, UNIQUE(serial, date) );\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_HRV_serial_idx ON " + db_prefix + "_HRV (serial);\n";
        sql += "CREATE INDEX IF NOT EXISTS " + db_prefix + "_HRV_date_idx ON " + db_prefix + "_HRV (datetime(date));\n";
        return executeSQL(CONN, sql);
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

    private enum Status {BEGIN, COMMIT, ABORT}

}
