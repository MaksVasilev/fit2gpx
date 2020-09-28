/*
Copyright © 2015-2020 by Maks Vasilev

created 7.02.2015
http://velo100.ru/garmin-fit-to-gpx
https://github.com/MaksVasilev/fit2gpx

exit code:

0 - ok
64 - help or invalid usage
65 - file invalid
66 - file not found
199 - data read filed
200 - track is empty, but writed (default for console mode)
201 - track is empty and not writed (default for dialog mode)
204 - no file selected
209 - debug break
*/

import com.garmin.fit.*;
import com.garmin.fit.plugins.HrToRecordMesgBroadcastPlugin;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.swing.UIManager.setLookAndFeel;

public class fit2gpx extends Component {

    static final String _version_ = "0.1.6";

    static ResourceBundle tr = ResourceBundle.getBundle("locale/tr", Locale.getDefault());

    public static void main(String[] args) {

        try {
            setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception ignored1){
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored2) { }
        }

        File[] MultipleFilesList;
        ArrayList<String> FileList = new ArrayList<>();
        boolean DialogMode = true;
        boolean StatisticEnable = false;
        boolean xDebug = false;
        String[] Filter = new String[3];

        String[] OpenTitle = new String[100];
        OpenTitle[0] = tr.getString("OpenTitleCSV");
        OpenTitle[1] = tr.getString("OpenTitle");
        OpenTitle[2] = tr.getString("OpenTitleM");
        OpenTitle[3] = tr.getString("OpenTitleHRV");
        OpenTitle[4] = tr.getString("OpenTitleOxy");
        OpenTitle[5] = tr.getString("OpenTitleStress");
        OpenTitle[6] = tr.getString("OpenTitleHR");
        OpenTitle[99] = tr.getString("OpenTitleDebug");

        Converter converter = new Converter();
        ConverterResult converterResult = new ConverterResult();

        for (String arg:args) {
            if(xDebug) { System.out.println("argument: " + arg); }
            if ( arg.equals("--help") || arg.equals("-h")) { Help.usage(); }
            if ( arg.equals("--statistic") || arg.equals("-s")) {  StatisticEnable = true; }
            if ( arg.equals("--csv") || arg.equals("-c")) {  converter.setOutputFormat(0); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--monitor-hr") || arg.equals("-mh")) {  converter.setOutputFormat(2); }
            if ( arg.equals("--hrv") || arg.equals("-vr")) {  converter.setOutputFormat(3); }
            if ( arg.equals("--hrv-filter") || arg.equals("-vf")) {  converter.setOutputFormat(3); converter.setUseFilterHRV(true); }
            if ( arg.equals("--monitor-oxy") || arg.equals("-spo")) { converter.setOutputFormat(4);  }
            if ( arg.equals("--monitor-stress") || arg.equals("-si")) { converter.setOutputFormat(5);  }
            if ( arg.equals("--hr-only") || arg.equals("-hr")) {  converter.setOutputFormat(6); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--merge") || arg.equals("-m")) { converter.setMergeOut(true); }
            if ( arg.equals("--no-dialog") || arg.equals("-nd") ) {  DialogMode = false; }
            if ( arg.equals("--save-empty") || arg.equals("-se") ) { converter.setSaveIfEmpty(true); }
            if ( arg.equals("--full-dump")) { converter.setOutputFormat(99);  }
            if ( arg.equals("-x") ) { xDebug = true; }
            if ( !arg.startsWith("-") ) {
                FileList.add(arg);
                DialogMode = false;
            }
            if ( arg.startsWith("--filter=")) {
                Filter = arg.split("=",2);
                try {
                    converter.setFilterHRV(Integer.parseInt(Filter[1]));
                } catch (Exception ignored3) {}
            }
            if ( arg.startsWith("--iso-date=")) {
                String[] isodate = arg.split("=", 2);
                if(isodate[1].equals("no") || isodate[1].equals("n")) {
                    converter.setUseISOdate(false);
                } else converter.setUseISOdate(true);
            }
        }

        if(!DialogMode) {
            if (FileList.isEmpty()) {
                Help.error_no_file();
                System.exit(204);
            }

            if(xDebug) { System.out.println("Files: " + FileList.size()); }
            if(FileList.size() < 2) { converter.setMergeOut(false); }
            if(xDebug) { System.out.println("Merge: " + converter.getMergeOut()); }

            converter.setFirstElement(true);    // for format header

            for (String f : FileList) {
                if(xDebug) { System.out.println("file: " + f); }

                converter.setInputFITfileName(f);    // file to work
                converterResult.add(converter.run(), converter.getInputFITfileName());      // run and get result
            }
            if(xDebug) {System.out.println("Good files: " + converterResult.getGoodFilesCount()); }

            if(converterResult.getGoodFilesCount() != 0) {
                converter.writeEndfile();    // write tail of file
            }

            if(StatisticEnable) {
                System.out.println(converterResult.getSummaryByString());
            }
        }

        if(DialogMode || FileList.isEmpty()) {

            UIManager.put("FileChooser.cancelButtonText",tr.getString("Cancel"));
            UIManager.put("FileChooser.cancelButtonToolTipText",tr.getString("CancelTip"));
            UIManager.put("FileChooser.fileNameLabelText",tr.getString("FileName"));
            UIManager.put("FileChooser.filesOfTypeLabelText",tr.getString("FileType"));
            UIManager.put("FileChooser.lookInLabelText",tr.getString("Dir"));

            JFileChooser chooser = new JFileChooser();
            chooser.setLocale(Locale.getDefault());

            chooser.setDialogTitle((OpenTitle[converter.OutputFormat]));
            chooser.setApproveButtonText(tr.getString("Open"));
            chooser.setPreferredSize(new Dimension(1200,600));

            chooser.setApproveButtonToolTipText(tr.getString("OpenTip"));
            chooser.setMultiSelectionEnabled(true);

            if(converter.OutputFormat == 2 || converter.OutputFormat == 4 || converter.OutputFormat == 5) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(tr.getString("OpenEXTmon"), "FIT", "fit");
                chooser.setFileFilter(filter);
            } else {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
                chooser.setFileFilter(filter);
            }

            int returnVal = chooser.showOpenDialog(chooser.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                MultipleFilesList = chooser.getSelectedFiles();

                if(xDebug) { System.out.println("Files: " + MultipleFilesList.length); }
                if(MultipleFilesList.length < 2) { converter.setMergeOut(false); }
                if(xDebug) { System.out.println("Merge: " + converter.getMergeOut()); }

                converter.setFirstElement(true);    // for format header

                for (File file : MultipleFilesList) {
                    if(xDebug) { System.out.println("file: " + file); }

                    converter.setInputFITfileName(file.getAbsoluteFile().getAbsolutePath());    // file to work
                    converterResult.add(converter.run(), converter.getInputFITfileName());      // run and get result
                }

                if(xDebug) {System.out.println("Good files: " + converterResult.getGoodFilesCount()); }

                if(converterResult.getGoodFilesCount() != 0) {
                    converter.writeEndfile();    // write tail of file
                }

            } else {
                System.exit(204);
            }

            if(StatisticEnable) {
                System.out.println(converterResult.getSummaryByString());
            }

            int MessageType = JOptionPane.INFORMATION_MESSAGE;
            if(converterResult.getEmptyFilesCount() > 0) {MessageType = JOptionPane.WARNING_MESSAGE;}
            if(converterResult.getBadFilesCount() > 0) {MessageType = JOptionPane.ERROR_MESSAGE;}

            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), converterResult.getSummaryByString(), tr.getString("ConvResult"), MessageType);
        }
    }

    private static class Converter {

        /*
0: Table output - CSV format
1: Standart Garmin point exchange format GPX
2: monitor: HR data (no headers)
3: HRV: R-R data
4: monitor: SpO2 data (no headers)
5: monitor: Garmin Stress Index (GSI) data (no headers)
6: Table output CSV format - Only HR and Time from actyvites (no headers)
99: Full Debug - all records (only text output)
         */

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
        private final String out_gpx_head2 = " <trk>\n  <name>{FTIFile}</name>\n  <trkseg>";
        private final String out_gpx_tail1 = "  </trkseg>\n </trk>";
        private final String out_gpx_tail2 = "</gpx>";

        final ArrayList<String> activity = new ArrayList<>();
        private final TreeMap<String,String> short_buffer = new TreeMap<>();    // buffer for read pair "key = value" - monitoring HR, SpO2..
        private final TreeMap<String,String[]> array_buffer = new TreeMap<>();  // buffer for read pair "key = value1,value2..." - HRV RR from activity
        private final TreeMap<String,Map<String,String>> full_buffer = new TreeMap<>(); // buffer for read full info as "key = set of (field = value)" - all data to CSV, GPX

        private static final String[] fieldnames = {"position_lat","position_long","altitude","enhanced_altitude","speed","enhanced_speed",
                "vertical_oscillation","stance_time_percent","stance_time","vertical_ratio","stance_time_balance","step_length",    // running dinamics
                "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
                "left_right_balance","left_power_phase","right_power_phase","left_power_phase_peak","right_power_phase_peak",       // bike dinamics
                "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness","left_pco","right_pco"};

        private static final Integer[] fieldindex = {
                108,    // Respiratory
                90,     // Performance Contition
                61, 66    // ?
        };

        private static final String[] fieldnames_for_out = {"duration","position_lat","position_long","altitude","enhanced_altitude","speed","enhanced_speed",
                "vertical_oscillation","stance_time_percent","stance_time","vertical_ratio","stance_time_balance","step_length",    // running dinamics
                "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
                "left_right_balance","left_right_balance_persent","left_power_phase_start","left_power_phase_end","right_power_phase_start",
                "right_power_phase_end","left_power_phase_peak_start","left_power_phase_peak_end","right_power_phase_peak_start","right_power_phase_peak_end",
                "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness","left_pco","right_pco",
                "respiratory","performance_contition","field_num_61","field_num_66",
                "fixed"};

        private Date TimeStamp = new Date();
        private final SimpleDateFormat nonISODateFormatCSV = new SimpleDateFormat("yyyy.MM.dd' 'HH:mm:ss");  // формат вывода в csv
        private final SimpleDateFormat nonISODateFormatCSVms = new SimpleDateFormat("yyyy.MM.dd' 'HH:mm:ss.SSS");  // формат вывода в csv с милисекундами
        private final SimpleDateFormat ISODateFormatCSV = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  // формат вывода в csv ISO/ГОСТ
        private final SimpleDateFormat ISODateFormatCSVms = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");  // формат вывода в csv с милисекундами ISO/ГОСТ
        private final SimpleDateFormat DateFormatGPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  // формат вывода в gpx
        private final SimpleDateFormat NewFileTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  // формат даты начала, если надо сместить
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
        private Long Local_Timestamp = 0L;      //
        private Long mesgTimestamp;
        private double HrvTime = 0.0;

        private Date FileTimeStamp = new Date();
        private Date StartTime = new Date();
        private boolean StartTimeFlag = false;
        private String DeviceCreator = "";
        
        private long timeOffset = 0L;   // смещение времени, для коррекции треков, в секундах

        private int OutputFormat = 1;

        private double lastGoodRR = 999.0;
        private double currentRR;
        private double thresholdFilterHRV = 35.0;
        private double deltaFilterHRV;
        private boolean useFilterHRV = false;



        void setOutputFormat(int outputFormat) {
            OutputFormat = outputFormat;
        }
        void setSaveIfEmpty(boolean saveIfEmpty) {SaveIfEmpty = saveIfEmpty;}
        void setMergeOut(boolean merge) { MergeOut = merge; }
        boolean getMergeOut() {return MergeOut; }
        void setFirstElement(boolean b) { firstElement = b; }
        void setUseFilterHRV(boolean useFilter) {useFilterHRV = useFilter;}
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
        
        int run() {  // Основной поэтапный цикл работы конвертера

            if(this.OutputFormat == 99) { MergeOut = false; }    // don't merge out for debug!

            int checkStatus = this.check();     // check file
            if(checkStatus != 0) {return checkStatus;}

            EmptyTrack = true;

            int readStatus = this.read();       // read file to buffer
            if(readStatus !=0) {return readStatus;}

            int fixstatus = this.fix();         // try to fix data in non corrupted file
            if(fixstatus !=0) {return fixstatus;}

            int formatstatus = this.format(0,0);   // format output to write in file
            // if(formatstatus !=0) {return formatstatus;}

            int writeStatus = this.write();     // write buffer to out

            converter_clear();           // clean for reuse in loop

            return writeStatus;
        }

        private void converter_clear() {
            activity.clear();
            short_buffer.clear();
            full_buffer.clear();
            array_buffer.clear();
            StartTimeFlag = false;
        }

        private int fix() {             // fix various error and hole in data (#1, #13, #17)

            if (EmptyTrack && !SaveIfEmpty) {
                return 201;
            }

            switch (OutputFormat) {
                case 0: // Table output - CSV format
                case 1: // Standart Garmin point exchange format GPX

                    String last_lat = ""; 
                    String last_lon = "";
                    String last_ele = "";
                    Double last_dist = 0.0;
                    Double prev_dist = 0.0;
                    Date date = new Date();
                    Date prev_date = new Date();

                    for(Map.Entry<String,Map<String,String>> m:full_buffer.entrySet()) {
                        try {
                            date = DateFormatCSV.parse(m.getKey());
                        } catch (ParseException ignore) {}

                        Map<String,String> row1 = m.getValue();

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
                        Double speed;

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
                                speed = (last_dist - prev_dist) / ((date.getTime() - prev_date.getTime()) / 1000);
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

                        full_buffer.put(m.getKey(), new HashMap<>() { { row1.forEach(this::put); } });   // write change to buffer
                        row1.clear();
                    }                                                                            // End 01-Bryton-hole-ele/Bryton-hole-coord

                    // fill all null lat/lon data before first real coordinates to this
                    for(Map.Entry<String,Map<String,String>> map02b:full_buffer.entrySet()) {        // Fix 02-Bryton-start-coord - Bryton start without coordinates fix
                        if(map02b.getValue().get("position_lat") != null && map02b.getValue().get("position_long") != null) {
                            String first_latlon = map02b.getKey();
                            String lat = map02b.getValue().get("position_lat");
                            String lon = map02b.getValue().get("position_long");
                            
                            for(Map.Entry<String,Map<String,String>> map02b_i:full_buffer.entrySet()) {
                                if(!map02b_i.getKey().equals(first_latlon)) {
                                    Map<String,String> row2 = map02b_i.getValue();
                                    row2.put("position_lat",lat);
                                    row2.put("position_long",lon);
                                    row2.put("fixed",append(row2.get("fixed"),"Bryton-start-coord,"));
                                    full_buffer.put(map02b_i.getKey(), new HashMap<>() { { row2.forEach(this::put); } });   // write change to buffer
                                    row2.clear();
                                 } else {
                                    break;
                                }
                            }
                            break;
                        }
                    }                                                                           // End 02-Bryton-start-coord

                    // fill all null elevation data before first real ele to this ele
                    for(Map.Entry<String,Map<String,String>> map03b:full_buffer.entrySet()) {        // Fix 03-Bryton-start-ele - Bryton start without elevation fix
                        if(map03b.getValue().get("enhanced_altitude") != null) {
                            String first_ele = map03b.getKey();
                            String ele = map03b.getValue().get("altitude");
                            
                            for(Map.Entry<String,Map<String,String>> map03b_i:full_buffer.entrySet()) {
                                if(!map03b_i.getKey().equals(first_ele)) {
                                    Map<String,String> row3 = map03b_i.getValue();
                                    row3.put("enhanced_altitude",ele);
                                    row3.put("altitude",ele);
                                    row3.put("fixed",append(row3.get("fixed"),"Bryton-start-ele,"));

                                    full_buffer.put(map03b_i.getKey(), new HashMap<>() { { row3.forEach(this::put); } });   // write change to buffer
                                    row3.clear();
                                } else {
                                    break;
                                }
                            }
                            break;
                        }
                    }                                                                             // End 03-Bryton-start-ele

                    Double last_lat_d = 0.0;                                                      // Fix 04-Swim-no-coord - empty coordinates for Swim, if distance increment
                    Double last_lon_d = 0.0;
                    last_ele = "";
                    last_dist = 0.0;


                    for(Map.Entry<String,Map<String,String>> m:full_buffer.entrySet()) {
                        Double lat = 0.0;
                        Double lon = 0.0;
                        Double dist = 0.0;

                        Map<String, String> row0 = m.getValue();
                        String start = "";
                        String end= "";

                         if(row0.get("position_lat") != null && row0.get("position_long") != null && row0.get("distance") != null) {
                            last_lat_d = checkD(row0.get("position_lat"));
                            last_lon_d = checkD(row0.get("position_long"));
                            last_dist = checkD(row0.get("distance"));
                        }

                        if(row0.get("position_lat") == null && row0.get("position_long") == null) {     // Search for first entry fith empty coordinates
                            start = m.getKey();

                            ArrayList<Double> dist_steps = new ArrayList<>();

                            for(Map.Entry<String,Map<String,String>> n: full_buffer.subMap(start,full_buffer.lastKey()+1).entrySet()) {
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
                                    for(Map.Entry<String,Map<String,String>> insert: full_buffer.subMap(start,end).entrySet()) {
                                        Map<String,String> row_insert = insert.getValue();

                                        Double step_dist_persent = (dist_steps.get(st)/delta_dist);
                                        Double step_lat = last_lat_d + (delta_lat * step_dist_persent);   // increase lat/lon proportionally increasing distance
                                        Double step_lon = last_lon_d + (delta_lon * step_dist_persent);

                                        row_insert.put("position_lat", String.valueOf(step_lat));
                                        row_insert.put("position_long", String.valueOf(step_lon));
                                        row_insert.put("fixed",append(row_insert.get("fixed"),"Swim-no-coord,"));

                                        full_buffer.put(insert.getKey(), new HashMap<>() { { row_insert.forEach(this::put); } });   // write change to buffer
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

        private int read() {    // Try to read input file // Чукча-читатель

            Decode decode = new Decode();

            BufferedMesgBroadcaster mesgBroadcaster = new BufferedMesgBroadcaster(decode);

            FileIdMesgListener fileIdMesgListener = mesg -> {

                String _Product = "";
                String _Manufacturer = "";

                if (mesg.getTimeCreated() != null) {

                    FileTimeStamp = new Date(mesg.getTimeCreated().getTimestamp() * 1000 + DateTime.OFFSET + (timeOffset * 1000));
                }

                if (mesg.getManufacturer() != null) {
                    _Manufacturer = " (" + Manufacturer.getStringFromValue(mesg.getManufacturer()) + ")";
                }

                if (mesg.getProduct() != null) {
                    _Product = GarminProduct.getStringFromValue(mesg.getProduct()) + _Manufacturer;
                }
                DeviceCreator = _Product;
            };

            MesgListener mesgListener = mesg -> {

                    switch (OutputFormat) {

                        case 0: // Table output - CSV format
                        case 1: // Standart Garmin point exchange format GPX
                        case 6: // Table output - Only HR and Time from actyvites

                            if (mesg.getFieldStringValue("timestamp") != null && mesg.getName().equals("record")) {

                                Map<String,String> fields = new HashMap<>();

                                TimeStamp = new Date((mesg.getFieldLongValue("timestamp") * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                                if(!StartTimeFlag) { StartTime = TimeStamp; StartTimeFlag = true; }

                                long duration_ms = (TimeStamp.getTime() - StartTime.getTime());
                                fields.put("duration", String.format("%02d:%02d:%02d", (duration_ms / (1000*60*60)) , ((duration_ms / (1000*60)) % 60) , ((duration_ms / 1000) % 60) ));

                                // search all known fields (array fieldnames)
                                for(String field:fieldnames) {
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
                                for(Integer field:fieldindex) {
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
                                if(full_buffer.containsKey(RecordedDate)) {
                                    for(String key:full_buffer.get(RecordedDate).keySet()) {
                                        fields.put(key, full_buffer.get(RecordedDate).get(key));
                                    }
                                }

                                if(fields.containsKey("position_lat") && fields.containsKey("position_long")) { EmptyTrack = false; }   // flag for track
                                if((OutputFormat == 6) && fields.containsKey("heart_rate")) { EmptyTrack = false; }                     // flag for HR only

                                full_buffer.put(RecordedDate, new HashMap<>() {                                         // write all field to buffer
                                    {
                                        // GPXtime - need to use in GPX output only, not sensitive to --iso-date=y/n !
                                        put("GPXtime",DateFormatGPX.format(TimeStamp));
                                        fields.forEach(this::put);
                                    }
                                });
                            }

                            break;

                        case 2: // monitor HR data

                            if(mesg.getName().equals("monitoring")) {

                                if (mesg.getFieldStringValue("timestamp") != null) {
                                    mesgTimestamp = mesg.getFieldLongValue("timestamp");
                                } else if (mesg.getFieldStringValue("timestamp_16") != null) {
                                    mesgTimestamp += (mesg.getFieldLongValue("timestamp_16") - (mesgTimestamp & 0xFFFF)) & 0xFFFF;
                                }

                                if (mesg.getFieldStringValue("timestamp_16") != null && mesg.getFieldStringValue("heart_rate") != null) {

                                    TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));
                                    short_buffer.put(DateFormatCSV.format(TimeStamp),mesg.getFieldStringValue("heart_rate"));
                                    EmptyTrack = false;
                                }
                            }
                            break;

                        case 3: // HRV: R-R data

                            if (mesg.getName().equals("event") ) {
                                if (mesg.getFieldStringValue("timestamp") != null && mesg.getFieldIntegerValue("event_type") == 0) {
                                    mesgTimestamp = mesg.getFieldLongValue("timestamp"); // timestamp of "START" event
                                }
                            }

                            if (mesg.getName().equals("hrv") ) {
                                if(mesgTimestamp == null) { mesgTimestamp = 946684800L - (DateTime.OFFSET/1000); } // if device not started

                                int index = 0;

                                while (mesg.getFieldStringValue("time",index) != null) {

                                    HrvTime += mesg.getFieldDoubleValue("time", index);
                                    TimeStamp = new Date( (mesgTimestamp * 1000) + (long)(HrvTime * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                                    if(useFilterHRV) {
                                        currentRR = mesg.getFieldDoubleValue("time", index);

                                        if(lastGoodRR == 999.0) {
                                            lastGoodRR = currentRR;
                                        }
                                        deltaFilterHRV = Math.abs((1.0-(currentRR/lastGoodRR))*100.0);

                                        if( deltaFilterHRV < thresholdFilterHRV) {
                                            lastGoodRR = currentRR;
                                            String[] l = {rounds(HrvTime, 3), String.valueOf(lastGoodRR), String.valueOf(round(60.0 / lastGoodRR, 3))};
                                            array_buffer.put(DateFormatCSVms.format(TimeStamp),l);
                                        }
                                    } else {
                                        lastGoodRR = mesg.getFieldDoubleValue("time", index);
                                        String[] l = {rounds(HrvTime, 3), String.valueOf(lastGoodRR), String.valueOf(round(60.0 / lastGoodRR, 3))};
                                        array_buffer.put(DateFormatCSVms.format(TimeStamp),l);
                                    }
                                    index++;
                                }
                                EmptyTrack = false;
                            }
                            break;

                        case 4:  // monitor SpO2 data

                            if(mesg.getNum() == 269) { // 269 - Oxygenation SpO2

                                if (mesg.getField(253) != null) {
                                    mesgTimestamp = mesg.getFieldLongValue(253);
                                }

                                if (mesg.getField(0) != null ) {
                                    TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));
                                    short_buffer.put(DateFormatCSV.format(TimeStamp),mesg.getFieldStringValue(0));
                                    EmptyTrack = false;
                                }
                            }
                            break;

                        case 5:  // monitor Garmin Stress Index (GSI) data

                            if(mesg.getNum() == 227) { // 227 - Garmin Stress Index

                                if (mesg.getField(1) != null) {
                                    mesgTimestamp = mesg.getFieldLongValue(1);
                                }

                                if (mesg.getFieldIntegerValue(0) > 0 ) { // "-1" - no data; "-2" - active time

                                    TimeStamp = new Date((mesgTimestamp * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                                    String gsi_227_2 = "";
                                    String gsi_227_3 = "";
                                    String gsi_227_4 = "";

                                    if(mesg.getFieldIntegerValue(3) != null) { // 227.3 - body battery
                                        gsi_227_3 = mesg.getFieldStringValue(3); }

                                    if(mesg.getFieldStringValue(2) != null) { // 227.2 - delta
                                        gsi_227_2 = mesg.getFieldStringValue(2); }

                                    if(mesg.getFieldStringValue(4) != null) { // 227.4 - ?, always = 1?
                                        gsi_227_4 = mesg.getFieldStringValue(4); }

                                    String[] l = {mesg.getFieldStringValue(0), gsi_227_3,gsi_227_2,gsi_227_4};
                                    array_buffer.put(DateFormatCSV.format(TimeStamp),l);
                                    EmptyTrack = false;
                                }
                            }
                            break;

                        case 99:  // Full Debug

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

            if(OutputFormat != 99) {                                                    // not merge all appened HR data in "Dump" mode!
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

        private int format(int readstatus, int fixstatus) {     // format output from buffer to text

            if(EmptyTrack) {
                return 100;
            }

            if(firstElement) {
                switch (OutputFormat) {                                // format header of file
                    case 0:     // Table output - CSV format
                        activity.clear();
                        StringBuilder head = new StringBuilder("time");
                        for (String name : fieldnames_for_out) {
                            head.append(";").append(name);
                        }
                        activity.add(head.toString());
                        break;
                    case 1:     // Standart Garmin point exchange format GPX
                        activity.clear();
                        activity.add(out_gpx_head.replace("{creator}", DeviceCreator));
                        activity.add(out_gpx_head1.replace("{time}", DateFormatGPX.format(FileTimeStamp)));
                        break;
                    case 3:     // activity: HRV (R-R)
                        activity.clear();
                        activity.add(0, "Timestamp,Time,RR,HR"); // заголовок IBI файла вариабельности ЧСС
                        break;
                    case 2:     // monitor: HR
                    case 4:     // monitor: SpO2
                    case 5:     // monitor: Garmin Stress Index
                    case 6:     // activity: Only HR
                        activity.clear();
                        break;
                }
                firstElement = false;
            }

            switch (OutputFormat) {                                   // format body
                case 0:     // Table output - CSV format
                    for(Map.Entry<String, Map<String, String>> m: full_buffer.entrySet()){
                        StringBuilder line;
                        line = new StringBuilder(m.getKey());
                        Map<String, String> ff = m.getValue();
                        for(String s1: fieldnames_for_out) {
                            line.append(";");
                            if(ff.containsKey(s1)) {
                                line.append(ff.get(s1));
                            }
                        }
                        activity.add(line.toString());
                    }
                    break;

                case 1:     // Standart Garmin point exchange format GPX
                    activity.add(out_gpx_head2.replace("{FTIFile}", InputFITfile.getName()));

                    for(Map.Entry<String, Map<String, String>> m: full_buffer.entrySet()) {
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

                case 6:     // activity: Only HR
                    for(Map.Entry<String, Map<String, String>> m: full_buffer.entrySet()) {
                        if(m.getValue().containsKey("heart_rate")) {
                            String duration = "";
                            if(m.getValue().containsKey("duration")) { duration = m.getValue().get("duration"); }
                            activity.add(m.getKey() + ";" + m.getValue().get("heart_rate") + ";" + duration);
                        }
                    }
                    break;

                case 2:     // monitor: HR
                case 4:     // monitor: SpO2
                    for(Map.Entry<String,String> m: short_buffer.entrySet()){
                        activity.add(m.getKey() + ";" + m.getValue());
                    }
                    break;

                case 3:     // activity: HRV (R-R)
                    for(Map.Entry<String,String[]> m: array_buffer.entrySet()){
                        StringBuilder line = new StringBuilder(m.getKey());
                        String[] l = m.getValue();
                        for(String s:l){
                            line.append(",").append(s);
                        }
                        activity.add(line.toString());
                    }
                    break;

                case 5:     // monitor: Garmin Stress Index
                    for(Map.Entry<String,String[]> m: array_buffer.entrySet()){
                        StringBuilder line = new StringBuilder(m.getKey());
                        String[] l = m.getValue();
                        for(String s:l){
                            line.append(";").append(s);
                        }
                        activity.add(line.toString());
                    }
                    break;
            }
            return 0;
        }

        private int writeEndfile() {
            final ArrayList<String> tail = new ArrayList<>();
            if (OutputFormat == 1) {
                tail.add(out_gpx_tail2);
            }

            if(tail.size() > 0) {
                try {
                    File OutputFile = new File(OutputFileName);
                    if (!OutputFile.exists()) {return 73;}

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
                return 0;
        }

        private int write() {   // Try to write output file // Чукча-писатель

            if (EmptyTrack && !SaveIfEmpty) {
                return 201;
            }

            if (EmptyTrack) {
                if(OutputFormat == 0) {
                    OutputFileName = InputFITfileName + ".empty.csv";
                } else {
                    OutputFileName = InputFITfileName + ".empty";
                }

            } else {

                String m = "";
                if(MergeOut) { m = ".merged"; }

                switch (OutputFormat) {
                    case 0:
                        OutputFileName = InputFITfileName + m + ".csv";
                        break;
                    case 1:
                        OutputFileName = InputFITfileName + m + ".gpx";
                        break;
                    case 2:
                        OutputFileName = InputFITfileName + m + ".monitor-HR.csv";
                        break;
                    case 3:
                        OutputFileName = InputFITfileName + m + ".HRV.csv";
                        break;
                    case 4:
                        OutputFileName = InputFITfileName + m + ".SpO2.csv";
                        break;
                    case 5:
                        OutputFileName = InputFITfileName + m + ".GSI.csv";
                        break;
                    case 6:
                        OutputFileName = InputFITfileName + m + ".HR.csv";
                        break;
                    case 99:
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

    private static class ConverterResult {
        private final ArrayList<String> GoodFiles = new ArrayList<>();
        private final ArrayList<String> EmptyFiles = new ArrayList<>();
        private final ArrayList<String> BadFiles = new ArrayList<>();

        public int getGoodFilesCount() {return GoodFiles.size();}
        int getEmptyFilesCount() {return EmptyFiles.size();}
        int getBadFilesCount() {return BadFiles.size();}

        void add(int result, String file) {
            if(result == 0) {GoodFiles.add(file);}
            if(result == 200 || result == 201) {EmptyFiles.add(file);}
            if(result == 65) {BadFiles.add(file + tr.getString("_file_corrupt"));}
            if(result == 66) {BadFiles.add(file + tr.getString("_file_not_found"));}
            if(result == 73) {BadFiles.add(file + tr.getString("_file_save_error"));}
            if(result == 199) {BadFiles.add(file + tr.getString("_read_data_from_file_error"));}
        }

        String getSummaryByString() {

            StringBuilder result = new StringBuilder(tr.getString("FilesParcedOk_") + GoodFiles.size());
            if(GoodFiles.size() < 11) {
                for (String GoodFile : GoodFiles) {
                    result.append("\n    ").append(GoodFile);
                }
            } else {
                for(int g = 0; g < 11; g++) {
                    result.append("\n    ").append(GoodFiles.get(g));
                }
                result.append("\n  ").append(tr.getString("_more_files_")).append((GoodFiles.size() - 10));
            }

            result.append("\n\n").append(tr.getString("FilesNoTrack_")).append(EmptyFiles.size());
            if(EmptyFiles.size() < 11) {
                for (String EmptyFile : EmptyFiles) {
                    result.append("\n    ").append(EmptyFile);
                }
            } else {
                for(int g = 0; g < 11; g++) {
                    result.append("\n    ").append(EmptyFiles.get(g));
                }
                result.append("\n  ").append(tr.getString("_more_files_")).append((EmptyFiles.size() - 10));
            }

            result.append("\n\n").append(tr.getString("FilesWithError_")).append(BadFiles.size());
            if(BadFiles.size() < 11) {
                for (String BadFile : BadFiles) {
                    result.append("\n    ").append(BadFile);
                }
            } else {
                for(int g = 0; g < 11; g++) {
                    result.append("\n    ").append(BadFiles.get(g));
                }
                result.append("\n  ").append(tr.getString("_more_files_")).append((BadFiles.size() - 10));
            }

            return result.toString();
        }

    }
}
