import format.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DB {
    private TreeMap<String, Map<String,String>> full_buffer = new TreeMap<>();
    private String[] fields;
    private Mode mode = Mode.UNKNOWN;
    private Out out = Out.DB_SQLITE;
    private String hash;
    private String db;

    public DB(String db,Mode mode, String hash) {
        setMode(mode);
        setHash(hash);
        setDb(db);
    }

    public void setDBtype(Out db) {
        if(db == Out.DB_SQLITE || db == Out.DB_POSTGRESQL) {
            out= db;
        }
    }

    public void setFields(String[] fields) { this.fields = fields; }
    private void setDb(String db) { this.db = db; }
    private void setMode(Mode m) { this.mode = m; }
    private void setHash(String hash) { this.hash = hash; }
    public void setFull_buffer(TreeMap<String, Map<String,String>> fb) { this.full_buffer = fb; }

    public void setShort_buffer(TreeMap<String,String> sb) {
        full_buffer.clear();

        switch (mode) {
            case MONITOR_HR:
                for(Map.Entry<String,String> monitor: sb.entrySet()) {
                    full_buffer.put(monitor.getKey(), new HashMap<>() { { put("heart_rate", monitor.getValue()); } });
                }
                break;
            case MONITOR_SPO2:
                for(Map.Entry<String,String> monitor: sb.entrySet()) {
                    full_buffer.put(monitor.getKey(), new HashMap<>() { { put("SPO2", monitor.getValue()); } });
                }
                break;
        }
    }
    public void setArray_buffer(TreeMap<String,String[]> ab) {
        full_buffer.clear();

        switch (mode) {
            case HRV:
                for (Map.Entry<String, String[]> array_row : ab.entrySet()) {
                    full_buffer.put(array_row.getKey(), new HashMap<>() {
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
                    full_buffer.put(array_row.getKey(), new HashMap<>() {
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
