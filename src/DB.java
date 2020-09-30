import format.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DB {
    private TreeMap<String, Map<String,String>> Buffer = new TreeMap<>();
    private String[] fields;
    private Mode MODE = Mode.UNKNOWN;
    private Database DBASE = Database.SQLITE;
    private String hash;
    private String db_connect;

    public DB(Database database, String db_connect, Mode mode) {
        setMode(mode);
        setDBtype(database);
        setDBconnect(db_connect);
    }

    public void setDBtype(Database dbase) { this.DBASE = dbase; }
    public void setFields(String[] fields) { this.fields = fields; }
    private void setDBconnect(String db) { this.db_connect = db; }
    private void setMode(Mode m) { this.MODE = m; }
    private void setHash(String hash) { this.hash = hash; }
    public void setBuffer(TreeMap<String, Map<String,String>> fb) { this.Buffer = fb; }

    public void setShort_buffer(TreeMap<String,String> sb) {
        Buffer.clear();

        switch (MODE) {
            case MONITOR_HR:
                for(Map.Entry<String,String> monitor: sb.entrySet()) {
                    Buffer.put(monitor.getKey(), new HashMap<>() { { put("heart_rate", monitor.getValue()); } });
                }
                break;
            case MONITOR_SPO2:
                for(Map.Entry<String,String> monitor: sb.entrySet()) {
                    Buffer.put(monitor.getKey(), new HashMap<>() { { put("SPO2", monitor.getValue()); } });
                }
                break;
        }
    }
    public void setArray_buffer(TreeMap<String,String[]> ab) {
        Buffer.clear();

        switch (MODE) {
            case HRV:
                for (Map.Entry<String, String[]> array_row : ab.entrySet()) {
                    Buffer.put(array_row.getKey(), new HashMap<>() {
                        {
                            put("Time", array_row.getValue()[0]);
                            put("RR", array_row.getValue()[1]);
                            put("HR", array_row.getValue()[2]);
                        }
                    });
                }
            break;
            case MONITOR_GSI:
                for (Map.Entry<String, String[]> array_row : ab.entrySet()) {
                    Buffer.put(array_row.getKey(), new HashMap<>() {
                        {
                            put("GSI", array_row.getValue()[0]);
                            put("BODY_BATTERY", array_row.getValue()[1]);
                            put("DELTA", array_row.getValue()[2]);
                        }
                    });
                }
                break;
        }
    }
}
