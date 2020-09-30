import format.DB_Append_Policy;
import format.DB_Create_Policy;
import format.Database;
import format.Mode;

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

    public DB(Database database, String db_connect, Mode mode) {
        setMode(mode);
        setDBtype(database);
        setDBconnect(db_connect);
    }

    public void setFields(String[] fields) { this.fields = fields; }
    public void setCreatePolicy(DB_Create_Policy policy) { create_policy = policy; }
    public void setAppendPolicy(DB_Append_Policy policy) { append_policy = policy; }
    public void setBuffer(TreeMap<String, Map<String,String>> fb) { this.Buffer = fb; }

    private void setDBtype(Database dbase) { this.DBASE = dbase; }
    private void setDBconnect(String db) { this.db_connect = db; }
    private void setMode(Mode m) { this.MODE = m; }
    private void setHash(String hash) { this.hash = hash; }

}
