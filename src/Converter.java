/*
Copyright © 2015-2020 by Maks Vasilev

created 7.02.2015
http://velo100.ru/garmin-fit-to-gpx
https://github.com/MaksVasilev/fit2gpx

*/

import com.garmin.fit.*;
import com.garmin.fit.plugins.HrToRecordMesgBroadcastPlugin;
import format.*;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.TimeZone;

public class Converter {

    static ResourceBundle tr = ResourceBundle.getBundle("locale/tr", Locale.getDefault());

    private Number semicircleToDegree(Field field) {
        if (field != null && "semicircles".equals(field.getUnits())) {
            final long semicircle = field.getLongValue();
            return semicircle * (180.0 / Math.pow(2.0, 31.0)); // degrees = semicircles * ( 180 / 2^31 )
        } else {
            return null;
        }
    }

    private double round(double d, int p) {
        double dd = Math.pow(10, p);
        return Math.round(d * dd) / dd;
    }

    private String rounds(double d, int p) {
        double dd = Math.pow(10, p);
        return String.valueOf(Math.round(d * dd) / dd);
    }

    private final String out_gpx_head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx creator=\"Converted by fit2gpx, http://velo100.ru/garmin-fit-to-gpx from {creator}\" version=\"1.1\" " +
            "xmlns=\"http://www.topografix.com/GPX/1/1\" " +
            "xmlns:gpxtrx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" " +
            "xmlns:gpxx=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" " +
            "xmlns:nmea=\"http://trekbuddy.net/2009/01/gpx/nmea\">";
    private final String out_gpx_head1 = " <metadata>\n  <time>{time}</time>\n </metadata>";
    private final String out_gpx_head2 = " <trk>\n  <name>{FTIFile}</name>\n  <number>{serialnumber}</number>\n  <trkseg>";
    private final String out_gpx_tail1 = "  </trkseg>\n </trk>";
    private final String out_gpx_tail2 = "</gpx>";

    final ArrayList<String> activity = new ArrayList<>();
    private final TreeMap<String, Map<String,String>> Buffer = new TreeMap<>(); // buffer for read full info as "key = set of (field = value)" - all data to CSV, GPX

    private static final String[] fields_for_search = {"position_lat","position_long","gps_accuracy","altitude","enhanced_altitude","speed","enhanced_speed","vertical_speed",
            "vertical_oscillation","stance_time_percent","stance_time","vertical_ratio","stance_time_balance","step_length",    // running dinamics
            "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
            "left_right_balance","left_power_phase","right_power_phase","left_power_phase_peak","right_power_phase_peak",       // bike dinamics
            "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness",
            "combined_pedal_smoothness","left_pco","right_pco","grit","flow",
            "absolute_pressure"};

    private static final Integer[] fieldindex_for_search = {
            108,    // Respiratory
            90,     // Performance Contition
            61, 66    // ?
    };

    private static final String[] activities_fiels = {"duration","position_lat","position_long","gps_accuracy","altitude","enhanced_altitude","speed","enhanced_speed","vertical_speed",
            "vertical_oscillation","stance_time_percent","stance_time","vertical_ratio","stance_time_balance","step_length",    // running dinamics
            "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
            "left_right_balance","left_right_balance_persent","left_power_phase_start","left_power_phase_end","right_power_phase_start",
            "right_power_phase_end","left_power_phase_peak_start","left_power_phase_peak_end","right_power_phase_peak_start","right_power_phase_peak_end",
            "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness",
            "combined_pedal_smoothness","left_pco","right_pco","grit","flow","absolute_pressure",
            "respiratory","performance_contition","field_num_61","field_num_66",
            "fixed"};

    private static final String[] hrv_fields = {"serial","time","RR","HR","filter"};
    private static final String[] monitor_hr_fields = {"heart_rate"};
    private static final String[] monitor_spo2_fields = {"SPO2"};
    private static final String[] monitor_gsi_fields = {"GSI","BODY_BATTERY","DELTA","gsi_227_4"};
    private static final String[] hr_only_fields = {"heart_rate","duration"};

    public String[] getFields() {
        switch (MODE) {
            case CSV: case GPX:  return activities_fiels;
            case CSV_HR: return hr_only_fields;
            case MONITOR_GSI: return monitor_gsi_fields;
            case MONITOR_SPO2: return monitor_spo2_fields;
            case MONITOR_HR: return monitor_hr_fields;
            case HRV: return hrv_fields;
            case DUMP: default: return null;
        }
    }

    private Date TimeStamp = new Date();
    private final SimpleDateFormat nonISODateFormatCSV = new SimpleDateFormat("yyyy.MM.dd' 'HH:mm:ss");  // формат вывода в csv
    private final SimpleDateFormat nonISODateFormatCSVms = new SimpleDateFormat("yyyy.MM.dd' 'HH:mm:ss.SSS");  // формат вывода в csv с милисекундами
    private final SimpleDateFormat ISODateFormatCSV = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  // формат вывода в csv ISO/ГОСТ
    private final SimpleDateFormat ISODateFormatCSVms = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");  // формат вывода в csv с милисекундами ISO/ГОСТ
    private final SimpleDateFormat DateFormatGPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  // формат вывода в gpx
    // private final SimpleDateFormat NewFileTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  // формат даты начала, если надо сместить
    private SimpleDateFormat DateFormatCSV = ISODateFormatCSV;
    private SimpleDateFormat DateFormatCSVms = ISODateFormatCSVms;

    private String InputFITfileName;
    private String OutputFileName;

    private FileInputStream InputStream;
    private File InputFITfile;

    private boolean MergeOut = false;
    private String OutputFileNameMerged = "";
    private boolean firstElement = false;
    private boolean EmptyTrack = true;      // признак того, что трек не содержит координат
    private boolean SaveIfEmpty = false;     // разрешить сохранение пустого трека без точек
    private Long SerialNumber = 0L;      //
    private Date FileTimeStamp = new Date();
    private String hashActivity = "";

    private Long mesgTimestamp;
    private double HrvTime = 0.0;

    private Date StartTime = new Date();
    private boolean StartTimeFlag = false;
    private String DeviceCreator = "";

    private long timeOffset = 0L;   // смещение времени, для коррекции треков, в секундах // TODO

    private Mode MODE = Mode.GPX;

    private Out OUT = Out.SINGLE_FILE;

    private double lastGoodRR = 999.0;
    private double currentRR;
    private double thresholdFilterHRV = 30.0;
    private double deltaFilterHRV;
    private boolean useFilterHRV = false;
    private boolean useFlagHRV = false;

    void setMode(Mode m) { this.MODE = m; }
    Mode getMODE() { return MODE; }
    void setOUT(Out out) { this.OUT = out; }
//    Out getOUT() { return OUT; }

    public String getHashActivity() { return hashActivity; }
    public String getFileTimeStamp() { return ISODateFormatCSV.format(FileTimeStamp); }
    void setSaveIfEmpty() {SaveIfEmpty = true;}
    void setMergeOut(boolean merge) { MergeOut = merge; }
    boolean getMergeOut() {return MergeOut; }
    void setFirstElement() { firstElement = true; OutputFileNameMerged = ""; }
    void setUseFilterHRV() {useFilterHRV = true;}
    void setUseFlagHRV() {useFlagHRV = true;}
    void setFilterHRV(Integer FilterFactor) {
        if(FilterFactor != null &&  FilterFactor > 0 && FilterFactor < 100) {
            thresholdFilterHRV = (double)FilterFactor;
        }
    }
    void setInputFITfileName(String inputFITfileName) {InputFITfileName = String.valueOf(inputFITfileName);}
    String getInputFITfileName() {return InputFITfileName;}

    void setUseISOdate(boolean b) {
        if(b) {
            DateFormatCSV = ISODateFormatCSV;
            DateFormatCSVms = ISODateFormatCSVms;
        } else {
            DateFormatCSV = nonISODateFormatCSV;
            DateFormatCSVms = nonISODateFormatCSVms;
        }
    }

    public TreeMap<String, Map<String,String>> getBuffer() { return Buffer; }

    int run() {  // Основной поэтапный цикл работы конвертера
        converter_clear();           // clean for reuse in loop

        if(MODE == Mode.DUMP) { MergeOut = false; }    // don't merge out for debug!

        int checkStatus = this.check();     // check file
        if(checkStatus != 0) {return checkStatus;}

        EmptyTrack = true;

        int readStatus = this.read();       // read file to buffer
        if(readStatus !=0) {return readStatus;}

        int fixstatus = this.fix();         // try to fix data in non corrupted file
        if(fixstatus !=0) {return fixstatus;}

        if(OUT == Out.DATABASE) {

            return fixstatus;

        } else {
            this.format();   // format output to write in file
            return this.write();
        }
    }

    private void converter_clear() {
        activity.clear();
        Buffer.clear();
        StartTimeFlag = false;
    }

    private int fix() {             // fix various error and hole in data (#1, #13, #17)

        if (EmptyTrack && !SaveIfEmpty) {
            return 201;
        }

        switch (MODE) {
            case CSV: // Table output - CSV format
            case GPX: // Standart Garmin point exchange format GPX

                String last_lat = "";
                String last_lon = "";
                String last_ele = "";
                Double last_dist = 0.0;
                Double prev_dist = 0.0;
                Date date = new Date();
                Date prev_date = new Date();

                for(Map.Entry<String,Map<String,String>> m: Buffer.entrySet()) {
                    try {
                        date = DateFormatCSV.parse(m.getKey());
                    } catch (ParseException ignore) {}

                    Map<String,String> row1 = m.getValue();

                    if(row1.containsKey("heart_rate") && (row1.get("heart_rate").equals("0"))) {    // generic: zero value of heart rate
                        row1.remove("heart_rate");
                    }

                    // use altitude only if it present and enhanced_altitude not (#13)              // generic: alt -> enh_alt if it not present
                    if (row1.get("altitude") != null && row1.get("enhanced_altitude") == null) {
                        row1.put("enhanced_altitude", row1.get("altitude"));
                        row1.put("fixed",append(row1.get("fixed"),"no-enh-ele,"));
                    }

                    // use speed only if it present and enhanced_speed not (#13)              // generic: speed -> enh_speed if it not present
                    if (row1.get("speed") != null && row1.get("enhanced_speed") == null) {
                        row1.put("enhanced_speed", row1.get("speed"));
                        row1.put("fixed",append(row1.get("fixed"),"no-enh-speed,"));
                    }

                    // fix BRYTON hole in data: lat/lon (#1)
                    double speed;

                    if(row1.containsKey("distance")) {
                        try {
                            last_dist = Double.parseDouble(row1.get("distance"));
                        } catch (Exception ignore) {
                            last_dist = 0.0;
                        }
                    }

                    if(row1.containsKey("enhanced_speed")) {
                        try {
                            speed = Double.parseDouble(row1.get("enhanced_speed"));
                        } catch (Exception ignore) {                              // generic: Speed from parce error
                            speed = 0.0;
                            row1.put("speed","0.0");
                            row1.put("enhanced_speed","0.0");
                            row1.put("fixed",append(row1.get("fixed"),"non-number-speed-to-zero,"));
                        }
                    }  else {                                                     // generic: Speed from null
                        speed = 0.0;
                        row1.put("speed","0.0");
                        row1.put("enhanced_speed","0.0");
                        row1.put("fixed",append(row1.get("fixed"),"empty-speed-to-zero,"));
                    }

                    if(speed == 0.0) {                                            // generic: Speed from distance if speed = 0 and distance incremented
                        if (last_dist > prev_dist) {
                            speed = (last_dist - prev_dist) / (date.getTime() - prev_date.getTime()) * 1000;
                            row1.put("speed",String.valueOf(speed));
                            row1.put("enhanced_speed",String.valueOf(speed));
                            row1.put("fixed",append(row1.get("fixed"),"speed-from-distance,"));
                        }
                    }

                    prev_date = date;

                    if (row1.containsKey("position_lat") && row1.containsKey("position_long")) {        // Fix 01-Bryton-hole-ele/Bryton-hole-coord - Bryton hole fix
                        last_lat = row1.get("position_lat");
                        last_lon = row1.get("position_long");

                        EmptyTrack = false;
                    } else if (!last_lat.equals("") && !last_lon.equals("") && speed == 0.0 && (last_dist.equals(prev_dist))) {  // fix (01) only if distance not incremended
                        row1.put("position_lat", last_lat);
                        row1.put("position_long", last_lon);
                        row1.put("fixed",append(row1.get("fixed"),"Bryton-hole-coord,"));
                    }
                    prev_dist = last_dist;

                    // fix BRYTON hole in data: elevation (#1)
                    if (row1.containsKey("enhanced_altitude")) {
                        last_ele = row1.get("enhanced_altitude");
                    } else if (!last_ele.equals("")) {
                        row1.put("altitude", last_ele);
                        row1.put("enhanced_altitude", last_ele);
                        row1.put("fixed",append(row1.get("fixed"),"Bryton-hole-ele,"));
                    }

                    Buffer.put(m.getKey(), new HashMap<>() { { row1.forEach(this::put); } });   // write change to buffer
                    row1.clear();
                }                                                                            // End 01-Bryton-hole-ele/Bryton-hole-coord

                // fill all null lat/lon data before first real coordinates to this
                for(Map.Entry<String,Map<String,String>> map02b: Buffer.entrySet()) {        // Fix 02-Bryton-start-coord - Bryton start without coordinates fix
                    if(map02b.getValue().get("position_lat") != null && map02b.getValue().get("position_long") != null) {
                        String first_latlon = map02b.getKey();
                        String lat = map02b.getValue().get("position_lat");
                        String lon = map02b.getValue().get("position_long");

                        for(Map.Entry<String,Map<String,String>> map02b_i: Buffer.entrySet()) {
                            if(!map02b_i.getKey().equals(first_latlon)) {
                                Map<String,String> row2 = map02b_i.getValue();
                                row2.put("position_lat",lat);
                                row2.put("position_long",lon);
                                row2.put("fixed",append(row2.get("fixed"),"Bryton-start-coord,"));
                                Buffer.put(map02b_i.getKey(), new HashMap<>() { { row2.forEach(this::put); } });   // write change to buffer
                                row2.clear();
                            } else {
                                break;
                            }
                        }
                        break;
                    }
                }                                                                           // End 02-Bryton-start-coord

                // fill all null elevation data before first real ele to this ele
                for(Map.Entry<String,Map<String,String>> map03b: Buffer.entrySet()) {        // Fix 03-Bryton-start-ele - Bryton start without elevation fix
                    if(map03b.getValue().get("enhanced_altitude") != null) {
                        String first_ele = map03b.getKey();
                        String ele = map03b.getValue().get("altitude");

                        for(Map.Entry<String,Map<String,String>> map03b_i: Buffer.entrySet()) {
                            if(!map03b_i.getKey().equals(first_ele)) {
                                Map<String,String> row3 = map03b_i.getValue();
                                row3.put("enhanced_altitude",ele);
                                row3.put("altitude",ele);
                                row3.put("fixed",append(row3.get("fixed"),"Bryton-start-ele,"));

                                Buffer.put(map03b_i.getKey(), new HashMap<>() { { row3.forEach(this::put); } });   // write change to buffer
                                row3.clear();
                            } else {
                                break;
                            }
                        }
                        break;
                    }
                }                                                                             // End 03-Bryton-start-ele

                double last_lat_d = 0.0;                                                      // Fix 04-Swim-no-coord - empty coordinates for Swim, if distance increment
                double last_lon_d = 0.0;
                last_dist = 0.0;


                for(Map.Entry<String,Map<String,String>> m: Buffer.entrySet()) {
                    double lat;
                    double lon;
                    double dist = 0.0;

                    Map<String, String> row0 = m.getValue();
                    String start;
                    String end;

                    if(row0.get("position_lat") != null && row0.get("position_long") != null && row0.get("distance") != null) {
                        last_lat_d = checkD(row0.get("position_lat"));
                        last_lon_d = checkD(row0.get("position_long"));
                        last_dist = checkD(row0.get("distance"));
                    }

                    if(row0.get("position_lat") == null && row0.get("position_long") == null) {     // Search for first entry fith empty coordinates
                        start = m.getKey();

                        ArrayList<Double> dist_steps = new ArrayList<>();

                        for(Map.Entry<String,Map<String,String>> n: Buffer.subMap(start, Buffer.lastKey()+1).entrySet()) {
                            Map<String, String> row00 = n.getValue();
                            if(row00.get("distance") != null) {
                                try {
                                    Double d = Double.parseDouble(row00.get("distance"));
                                    dist_steps.add(d - last_dist);
                                } catch (Exception ignore) { }
                            }

                            if(row00.get("distance") != null) {
                                dist = checkD(row00.get("distance"));
                            }
                            if(row00.get("position_lat") != null && row00.get("position_long") != null && (dist > last_dist) ) {  // Search for end of hole
                                lat = checkD(row00.get("position_lat"));
                                lon = checkD(row00.get("position_long"));
                                end = n.getKey();

                                Double delta_dist = dist - last_dist;
                                Double delta_lat = lat - last_lat_d;
                                Double delta_lon = lon - last_lon_d;

                                int st = 0;
                                for(Map.Entry<String,Map<String,String>> insert: Buffer.subMap(start,end).entrySet()) {
                                    Map<String,String> row_insert = insert.getValue();

                                    Double step_dist_persent = (dist_steps.get(st)/delta_dist);
                                    Double step_lat = last_lat_d + (delta_lat * step_dist_persent);   // increase lat/lon proportionally increasing distance
                                    Double step_lon = last_lon_d + (delta_lon * step_dist_persent);

                                    row_insert.put("position_lat", String.valueOf(step_lat));
                                    row_insert.put("position_long", String.valueOf(step_lon));
                                    row_insert.put("fixed",append(row_insert.get("fixed"),"Swim-no-coord,"));

                                    Buffer.put(insert.getKey(), new HashMap<>() { { row_insert.forEach(this::put); } });   // write change to buffer
                                    row_insert.clear();
                                    st++;
                                }
                                dist_steps.clear();
                                break;
                            }
                        }
                    }
                }                                                                           // End 04-Swim-no-coord

                break; // end of GPX, CSV format
        }
        return 0;
    }

    private static String append(Object obj, String string){
        if(obj instanceof String){
            return obj + string;
        } else {
            return string;
        }
    }

    private static double checkD(String D) {    // check Double
        double d;
        if( D.equals("") ) {return -1.0;}
        try {
            d = Double.parseDouble(D.replace(",","."));
        } catch (NumberFormatException ignoreD) {
            return -1.0;
        }
        return d;
    }

    private int check() {   // этап проверки доступности файла

        Decode decode = new Decode();

        try {
            InputFITfile = new File(InputFITfileName);
            InputStream = new FileInputStream(InputFITfile);

        } catch (IOException e) {
            System.err.println(tr.getString("Error_") + InputFITfileName + tr.getString("NotFoundOrNotFile"));
            //System.exit(66);
            return 66;
        }

        try {
            if (!decode.checkFileIntegrity(InputStream)) {
                throw new RuntimeException(tr.getString("file_") + InputFITfileName + tr.getString("_corrupt"));
            }
        } catch (RuntimeException e) {
            System.err.print(tr.getString("FileCheckError_"));
            System.err.println(e.getMessage());

            try {
                InputStream.close();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            //System.exit(65);
            return 65;
        }

        return 0;
    }

    public String SHA1(String sha1) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
            byte[] array = md.digest(sha1.getBytes(StandardCharsets.UTF_8));
            StringBuilder s = new StringBuilder();
            for (byte b : array) { s.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3); }
            return s.toString().toUpperCase();
        } catch (java.security.NoSuchAlgorithmException ignored) {}
        return "";
    }

    private int read() {    // Try to read input file // Чукча-читатель

        Decode decode = new Decode();

        BufferedMesgBroadcaster mesgBroadcaster = new BufferedMesgBroadcaster(decode);

        FileIdMesgListener fileIdMesgListener = mesg -> {

            String _Product = "";
            String _Manufacturer;

            if (mesg.getTimeCreated() != null) {
                FileTimeStamp = new Date(mesg.getTimeCreated().getTimestamp() * 1000 + DateTime.OFFSET + (timeOffset * 1000));
            }

            int __product = 0;
            int __manufacturer = 0;

            if (mesg.getManufacturer() != null) {
                __manufacturer = mesg.getManufacturer();
                _Manufacturer = " (" + Manufacturer.getStringFromValue(mesg.getManufacturer()) + ")";

                if (mesg.getProduct() != null) {

                    __product = mesg.getProduct();

                    if(__manufacturer == Manufacturer.GARMIN) {
                        _Product = GarminProduct.getStringFromValue(mesg.getGarminProduct()) + _Manufacturer;
                    } else if (mesg.getManufacturer() == Manufacturer.FAVERO_ELECTRONICS ){
                        _Product = FaveroProduct.getStringFromValue(mesg.getFaveroProduct()) + _Manufacturer;
                    } else if (mesg.getManufacturer() == Manufacturer.BRYTON) {
                        _Product = FitTools.BrytonProduct(__product) + _Manufacturer;
                    } else {
                        _Product = "Device ID: " + mesg.getProduct() + _Manufacturer;
                    }
                }
            }

            if(mesg.getSerialNumber() != null) {
                SerialNumber = mesg.getSerialNumber();
            }

            DeviceCreator = _Product;
            SimpleDateFormat hashDate = new SimpleDateFormat("yyyyMMddHHmmss");
            hashDate.setTimeZone(TimeZone.getTimeZone("UTC"));

            String hashString = (String.valueOf(__manufacturer) + String.valueOf(__product) + hashDate.format(FileTimeStamp) + String.valueOf(SerialNumber));
            hashActivity = SHA1(hashString);

        };

        MesgListener mesgListener = mesg -> {

            switch (MODE) {

                case CSV: // Table output - CSV format
                case GPX: // Standart Garmin point exchange format GPX
                case CSV_HR: // Table output - Only HR and Time from actyvites

                    if (mesg.getFieldStringValue("timestamp") != null && mesg.getName().equals("record")) {

                        Map<String,String> fields = new HashMap<>();

                        TimeStamp = new Date((mesg.getFieldLongValue("timestamp") * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                        if(!StartTimeFlag) { StartTime = TimeStamp; StartTimeFlag = true; }

                        long duration_ms = (TimeStamp.getTime() - StartTime.getTime());
                        fields.put("duration", String.format("%02d:%02d:%02d", (duration_ms / (1000*60*60)) , ((duration_ms / (1000*60)) % 60) , ((duration_ms / 1000) % 60) ));

                        // search all known fields (array fieldnames)
                        for(String field: fields_for_search) {
                            if(mesg.getFieldStringValue(field) != null) {
                                String value = mesg.getFieldStringValue(field);
                                if(field.equals("position_lat") || field.equals("position_long")) {
                                    value = semicircleToDegree(mesg.getField(field)).toString();
                                }
                                // fields with multiply values
                                if(field.equals("left_power_phase") || field.equals("right_power_phase") ||
                                        field.equals("left_power_phase_peak") || field.equals("right_power_phase_peak")) {
                                    fields.put(field + "_start",mesg.getFieldStringValue(field,0));
                                    fields.put(field + "_end",mesg.getFieldStringValue(field,1));
                                } else if (field.equals("left_right_balance")) {
                                    fields.put(field,value);
                                    // human readable balance
                                    fields.put(field + "_persent",String.valueOf((mesg.getFieldDoubleValue("left_right_balance") / 3.6) - 50.0));
                                } else {
                                    fields.put(field,value);
                                    // System.out.println(field + "|" + value);
                                }
                            }
                        }

                        // for field without name and unknown fields use list of indexes
                        for(Integer field: fieldindex_for_search) {
                            if(mesg.getFieldStringValue(field) != null) {
                                String value = mesg.getFieldStringValue(field);
                                switch (field) {
                                    case 108:
                                        fields.put("respiratory", value);
                                        break;
                                    case 90:
                                        fields.put("performance_contition", value);
                                        break;
                                    default:
                                        fields.put("field_num_" + field, value);
                                        break;
                                }
                            }
                        }

                        String RecordedDate = DateFormatCSV.format(TimeStamp);

                        // if records with this time already present, then merge existing key=value to current set
                        // part of Bryton fixes
                        if(Buffer.containsKey(RecordedDate)) {
                            for(String key: Buffer.get(RecordedDate).keySet()) {
                                fields.put(key, Buffer.get(RecordedDate).get(key));
                            }
                        }

                        if(fields.containsKey("position_lat") && fields.containsKey("position_long")) { EmptyTrack = false; }   // flag for track
                        if((MODE == Mode.CSV_HR) && fields.containsKey("heart_rate")) { EmptyTrack = false; }                     // flag for HR only

                        Buffer.put(RecordedDate, new HashMap<>() {                                         // write all field to buffer
                            {
                                // GPXtime - need to use in GPX output only, not sensitive to --iso-date=y/n !
                                put("GPXtime",DateFormatGPX.format(TimeStamp));
                                fields.forEach(this::put);
                            }
                        });
                    }

                    break;

                case MONITOR_HR: // monitor HR data

                    if(mesg.getName().equals("monitoring")) {

                        if (mesg.getFieldStringValue("timestamp") != null) {
                            mesgTimestamp = mesg.getFieldLongValue("timestamp");
                        } else if (mesg.getFieldStringValue("timestamp_16") != null) {
                            mesgTimestamp += (mesg.getFieldLongValue("timestamp_16") - (mesgTimestamp & 0xFFFF)) & 0xFFFF;
                        }

                        if (mesg.getFieldStringValue("timestamp_16") != null && mesg.getFieldStringValue("heart_rate") != null) {

                            if(mesg.getFieldIntegerValue("heart_rate") != 0) {
                                TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                                Buffer.put(DateFormatCSV.format(TimeStamp), new HashMap<>() {
                                    {
                                        put("heart_rate", mesg.getFieldStringValue("heart_rate"));
                                    }
                                });
                                EmptyTrack = false;
                            }
                        }
                    }
                    break;

                case HRV: // HRV: R-R data

                    if (mesg.getName().equals("event") ) {
                        if (mesg.getFieldStringValue("timestamp") != null && mesg.getFieldIntegerValue("event_type") == 0) {
                            mesgTimestamp = mesg.getFieldLongValue("timestamp"); // timestamp of "START" event
                        }
                    }

                    if (mesg.getName().equals("hrv") ) {
                        if(mesgTimestamp == null) { mesgTimestamp = 946684800L - (DateTime.OFFSET/1000); } // if device not started

                        int index = 0;

                        while (mesg.getFieldStringValue("time",index) != null) {
                            int flag;

                            HrvTime += mesg.getFieldDoubleValue("time", index);
                            TimeStamp = new Date( (mesgTimestamp * 1000) + (long)(HrvTime * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                            currentRR = mesg.getFieldDoubleValue("time", index);

                            if(lastGoodRR == 999.0) { lastGoodRR = currentRR; }
                            deltaFilterHRV = Math.abs((1.0-(currentRR/lastGoodRR))*100.0);

                            if( deltaFilterHRV < thresholdFilterHRV) {
                                lastGoodRR = currentRR;
                                flag = 0;
                            } else {
                                flag = 1;
                            }

                            int finalFlag = flag;
                            Buffer.put(DateFormatCSVms.format(TimeStamp), new HashMap<>() {                         {
                                put("time", rounds(HrvTime, 3));
                                put("RR", String.valueOf(currentRR));
                                put("HR", String.valueOf(round(60.0 / currentRR, 3)));
                                put("filter", String.valueOf(finalFlag));
                            } });

                            index++;
                        }
                        EmptyTrack = false;
                    }
                    break;

                case MONITOR_SPO2:  // monitor SpO2 data

                    if(mesg.getNum() == 269) { // 269 - Oxygenation SpO2

                        if (mesg.getField(253) != null) {
                            mesgTimestamp = mesg.getFieldLongValue(253);
                        }

                        if (mesg.getField(0) != null ) {
                            TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));
                            Buffer.put(DateFormatCSV.format(TimeStamp), new HashMap<>() { { put("SPO2", mesg.getFieldStringValue(0)); } });
                            EmptyTrack = false;
                        }
                    }
                    break;

                case MONITOR_GSI:  // monitor Garmin Stress Index (GSI) data

                    if(mesg.getNum() == 227) { // 227 - Garmin Stress Index

                        Map<String,String> fields = new HashMap<>();

                        if (mesg.getField(1) != null) {
                            mesgTimestamp = mesg.getFieldLongValue(1);
                        }

                        if (mesg.getFieldIntegerValue(0) > 0 ) { // "-1" - no data; "-2" - active time
                            fields.put("GSI",mesg.getFieldStringValue(0));

                            TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                            if(mesg.getFieldIntegerValue(3) != null) { // 227.3 - body battery
                                fields.put("BODY_BATTERY",mesg.getFieldStringValue(3)); }

                            if(mesg.getFieldStringValue(2) != null) { // 227.2 - delta
                                fields.put("DELTA",mesg.getFieldStringValue(2)); }

                            if(mesg.getFieldStringValue(4) != null) { // 227.4 - ?, always = 1?
                                fields.put("gsi_227_4",mesg.getFieldStringValue(4)); }

                            Buffer.put(DateFormatCSV.format(TimeStamp), new HashMap<>() { { fields.forEach(this::put); } });
                            fields.clear();
                            EmptyTrack = false;
                        }
                    }
                    break;

                case DUMP:  // Full Debug

                    StringBuilder line = new StringBuilder();
                    line.append("Message number: ").append(mesg.getNum()).append("\tName: \"").append(mesg.getName()).append("\"\tFields: ").append(mesg.getNumFields()).append("\n");

                    for(Field field:mesg.getFields()) {
                        line.append("\tField num: ").append(field.getNum()).append("\tName: \"").append(field.getName()).append("\"\tUnits: (").append(field.getUnits()).append(")\t");

                        int index = 0;
                        line.append("[");
                        while (field.getStringValue(index) != null) {
                            if(index > 0) { line.append("|"); }
                            line.append(field.getStringValue(index));
                            index++;
                        }
                        line.append("]\n");
                    }
                    EmptyTrack = false;
                    activity.add(line.toString());
                    break;
            }
        };

        HrMesgListener hrListener = mesg -> {};

        mesgBroadcaster.addListener(fileIdMesgListener);
        mesgBroadcaster.addListener(mesgListener);

        if(MODE != Mode.DUMP) {                                                    // not merge all appened HR data in "Dump" mode!
            mesgBroadcaster.addListener(hrListener);                                // disable plugin HR to Dump
            MesgBroadcastPlugin hr_plugin = new HrToRecordMesgBroadcastPlugin();
            mesgBroadcaster.registerMesgBroadcastPlugin(hr_plugin);
        }

        try {
            mesgBroadcaster.run(new BufferedInputStream(new FileInputStream(InputFITfile)));
            mesgBroadcaster.broadcast();
        } catch (FitRuntimeException e) {
            System.err.print(tr.getString("ErrorParsingFile_") + InputFITfile + ": ");
            System.err.println(e.getMessage());

            try {
                InputStream.close();
            } catch (IOException f) {
                throw new RuntimeException(f);
            }

            return 199;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            InputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private void format() {     // format output from buffer to text

        if(EmptyTrack && !SaveIfEmpty) {
            return;
        }

        if(firstElement || !MergeOut) {
            switch (MODE) {                                // format header of file
                case CSV:     // Table output - CSV format
                    activity.clear();
                    StringBuilder head = new StringBuilder("time");
                    for (String name : activities_fiels) {
                        head.append(";").append(name);
                    }
                    activity.add(head.toString());
                    break;
                case GPX:     // Standart Garmin point exchange format GPX
                    activity.clear();
                    activity.add(out_gpx_head.replace("{creator}", DeviceCreator));
                    activity.add(out_gpx_head1.replace("{time}", DateFormatGPX.format(FileTimeStamp)));
                    break;
                case HRV:     // activity: HRV (R-R)
                    activity.clear();
                    activity.add("Timestamp" + Arrays.toString(hrv_fields).replace("[", "").replace("]", "").trim()); // заголовок IBI файла вариабельности ЧСС
                    break;
                case MONITOR_HR:     // monitor: HR
                case MONITOR_SPO2:     // monitor: SpO2
                case MONITOR_GSI:     // monitor: Garmin Stress Index
                case CSV_HR:     // activity: Only HR
                    activity.clear();
                    break;
            }
            firstElement = false;
        }

        switch (MODE) {                                   // format body
            case CSV:     // Table output - CSV format
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()){
                    StringBuilder line;
                    line = new StringBuilder(m.getKey());
                    Map<String, String> ff = m.getValue();
                    for(String s1: activities_fiels) {
                        line.append(";");
                        if(ff.containsKey(s1)) {
                            line.append(ff.get(s1));
                        }
                    }
                    activity.add(line.toString());
                }
                break;

            case GPX:     // Standart Garmin point exchange format GPX
                activity.add(out_gpx_head2.replace("{FTIFile}", InputFITfile.getName()).replace("{serialnumber}", String.valueOf(SerialNumber)));

                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    if(m.getValue().get("position_lat") != null && m.getValue().get("position_long") != null) {
                        activity.add("   <trkpt lat=\"{lat}\"".replace("{lat}", m.getValue().get("position_lat")) + " lon=\"{lon}\">".replace("{lon}", m.getValue().get("position_long")));
                    } else {
                        activity.add("   <trkpt lat=\"\"" + " lon=\"\">");
                    }
                    activity.add("    <time>{time}</time>".replace("{time}",m.getValue().get("GPXtime")));
                    if(m.getValue().get("enhanced_altitude") != null) { activity.add("    <ele>{enhanced_altitude}</ele>".replace("{enhanced_altitude}",m.getValue().get("enhanced_altitude"))); }
                    boolean extention = m.getValue().get("power") != null || m.getValue().get("enhanced_speed") != null;
                    boolean tpextention = m.getValue().get("temperature") != null || m.getValue().get("heart_rate") != null || m.getValue().get("cadence") != null || m.getValue().get("enhanced_speed") != null || m.getValue().get("distance") != null;
                    if(extention || tpextention) { activity.add("    <extensions>");
                        if(m.getValue().get("power") != null) { activity.add("     <power>{power}</power>".replace("{power}",m.getValue().get("power"))); }
                        if(m.getValue().get("enhanced_speed") != null) { activity.add("     <nmea:speed>{enhanced_speed}</nmea:speed>".replace("{enhanced_speed}",m.getValue().get("enhanced_speed") ));}
                        if(tpextention) { activity.add("     <gpxtpx:TrackPointExtension>");
                            if(m.getValue().get("temperature") != null) {activity.add("      <gpxtpx:atemp>{temperature}</gpxtpx:atemp>".replace("{temperature}",m.getValue().get("temperature")));}
                            if(m.getValue().get("heart_rate") != null) {activity.add("      <gpxtpx:hr>{heart_rate}</gpxtpx:hr>".replace("{heart_rate}",m.getValue().get("heart_rate")));}
                            if(m.getValue().get("cadence") != null) {activity.add("      <gpxtpx:cad>{cadence}</gpxtpx:cad>".replace("{cadence}",m.getValue().get("cadence")));}
                            if(m.getValue().get("enhanced_speed") != null) {activity.add("      <gpxtpx:speed>{enhanced_speed}</gpxtpx:speed>".replace("{enhanced_speed}",m.getValue().get("enhanced_speed")));}
                            if(m.getValue().get("distance") != null) {activity.add("      <gpxtpx:course>{distance}</gpxtpx:course>".replace("{distance}",m.getValue().get("distance")));}
                            activity.add("     </gpxtpx:TrackPointExtension>"); }
                        activity.add("    </extensions>"); }
                    activity.add("   </trkpt>");
                }
                activity.add(out_gpx_tail1);
                break;

            case CSV_HR:     // activity: Only HR
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    if(m.getValue().containsKey("heart_rate")) {
                        String duration = "";
                        if(m.getValue().containsKey("duration")) { duration = m.getValue().get("duration"); }
                        activity.add(m.getKey() + ";" + m.getValue().get("heart_rate") + ";" + duration);
                    }
                }
                break;

            case MONITOR_HR:     // monitor: HR
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    activity.add(m.getKey() + ";" + m.getValue().get("heart_rate"));
                }
                break;

            case MONITOR_SPO2:     // monitor: SpO2
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    activity.add(m.getKey() + ";" + m.getValue().get("SPO2"));
                }
                break;

            case HRV:     // activity: HRV (R-R)    // TODO flag!
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    boolean flag = (m.getValue().get("filter").equals("0"));
                    if(useFilterHRV) {
                        if (flag && !useFlagHRV) {
                            activity.add(m.getKey() + "," + m.getValue().get("time") + "," + m.getValue().get("RR") + "," + m.getValue().get("HR"));
                        } else if (useFlagHRV) {
                            activity.add(m.getKey() + "," + m.getValue().get("time") + "," + m.getValue().get("RR") + "," + m.getValue().get("HR") + ',' + m.getValue().get("filter"));
                        }
                    }else {
                        activity.add(m.getKey() + "," + m.getValue().get("time") + "," + m.getValue().get("RR") + "," + m.getValue().get("HR"));
                    }
                }
                break;

            case MONITOR_GSI:     // monitor: Garmin Stress Index
                for(Map.Entry<String, Map<String, String>> m: Buffer.entrySet()) {
                    activity.add(m.getKey() + ";" + m.getValue().get("GSI") + ";" + m.getValue().get("BODY_BATTERY") + ";" + m.getValue().get("DELTA") + ";" + m.getValue().get("gsi_227_4"));
                }
                break;
        }
    }

    void writeEndfile() {
        final ArrayList<String> tail = new ArrayList<>();
        if (MODE == Mode.GPX) {
            tail.add(out_gpx_tail2);
        }

        if(tail.size() > 0) {
            try {
                File OutputFile = new File(OutputFileName);
                if (!OutputFile.exists()) {return;}

                try { // пытаемся записать в файл
                    BufferedWriter OutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile, true), StandardCharsets.UTF_8));
                    for (String s : tail) {
                        OutWriter.write(s + System.lineSeparator());
                    }
                    OutWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
                //System.exit(73);
            }
        }
    }

    private int write() {   // Try to write output file // Чукча-писатель

        if (EmptyTrack && !SaveIfEmpty) {
            return 201;
        }

        if (EmptyTrack) {
            if(MODE == Mode.CSV) {
                OutputFileName = InputFITfileName + ".empty.csv";
            } else {
                OutputFileName = InputFITfileName + ".empty";
            }

        } else {

            String m = "";
            if(MergeOut) { m = ".merged"; }

            switch (MODE) {
                case CSV:
                    OutputFileName = InputFITfileName + m + ".csv";
                    break;
                case GPX:
                    OutputFileName = InputFITfileName + m + ".gpx";
                    break;
                case MONITOR_HR:
                    OutputFileName = InputFITfileName + m + ".monitor-HR.csv";
                    break;
                case HRV:
                    OutputFileName = InputFITfileName + m + ".HRV.csv";
                    break;
                case MONITOR_SPO2:
                    OutputFileName = InputFITfileName + m + ".SpO2.csv";
                    break;
                case MONITOR_GSI:
                    OutputFileName = InputFITfileName + m + ".GSI.csv";
                    break;
                case CSV_HR:
                    OutputFileName = InputFITfileName + m + ".HR.csv";
                    break;
                case DUMP:
                    OutputFileName = InputFITfileName + m + ".DUMP.txt";
                    break;
            }
        }
        boolean append = false;

        if(MergeOut) {              // TODO определить стратегию формирования имени выходного файла
            if (OutputFileNameMerged.equals("")) {
                OutputFileNameMerged = OutputFileName;
                append = false;
            } else {
                OutputFileName = OutputFileNameMerged;
                append = true;
            }
        }
        // {System.out.println("Output File Name: " + OutputFileName); }
        try {

            File OutputFile = new File(OutputFileName);

            if (!OutputFile.exists()) {
                if(!OutputFile.createNewFile()) {return 73;}
            }

            try { // пытаемся записать в файл
                BufferedWriter OutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile, append), StandardCharsets.UTF_8));
                for (String s : activity) {
                    OutWriter.write(s + System.lineSeparator());
                }
                OutWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
            //System.exit(73);
        }

        if (EmptyTrack) {return 200;}

        return 0;
    }

}
