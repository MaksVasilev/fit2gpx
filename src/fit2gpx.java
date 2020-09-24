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

    static final String _version_ = "0.1.3 - unstable!";

    static ResourceBundle tr = ResourceBundle.getBundle("locale/tr", Locale.getDefault());

    public static void main(String[] args) {

        try {
            setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception ignored){
         /*   try {
                setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch(Exception ignored2){} */
        }

        File[] MultipleFilesList;
        ArrayList<String> FileList = new ArrayList<>();
        boolean DialogMode = true;
        boolean StatisticEnable = false;
     //   boolean OutputCSV = false;
     //   boolean MonitoringFIT = false;
     //   boolean HrvFIT = false;
     //   boolean SpO2FIT = false;
     //   boolean SilentMode = false;
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
            if ( arg.equals("--monitor") || arg.equals("-m")) {  converter.setOutputFormat(2); }
            if ( arg.equals("--hrv") || arg.equals("-v")) {  converter.setOutputFormat(3); }
            if ( arg.equals("--hrv-filter") || arg.equals("-f")) {  converter.setOutputFormat(3); converter.setUseFilterHRV(true); }
            if ( arg.equals("--oxy") || arg.equals("-o")) { converter.setOutputFormat(4);  }
            if ( arg.equals("--stress") || arg.equals("-i")) { converter.setOutputFormat(5);  }
            if ( arg.equals("--hr-only") || arg.equals("-r")) {  converter.setOutputFormat(6); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--no-dialog") || arg.equals("-n") ) {  DialogMode = false; }
            if ( arg.equals("--save-empty") || arg.equals("-e") ) { converter.setSaveIfEmpty(true); }
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

            for (String f : FileList) {
                if(xDebug) { System.out.println("file: " + f); }

                converter.setInputFITfileName(f);
                converterResult.add(converter.run(), converter.getInputFITfileName());
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

                for (File file : MultipleFilesList) {
                    converter.setInputFITfileName(file.getAbsoluteFile().getAbsolutePath());
                    converterResult.add(converter.run(), converter.getInputFITfileName());
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
        private final String out_gpx_tail = "  </trkseg>\n </trk>\n</gpx>";

        final ArrayList<String> activity = new ArrayList<>();
        private final Map<String,String> short_buffer = new TreeMap<>();
        private final Map<String,String[]> array_buffer = new TreeMap<>();
        private final Map<String,Map<String,String>> full_buffer = new TreeMap<>();

        private static final String[] fieldnames = {"position_lat","position_long","altitude","enhanced_altitude","speed","enhanced_speed",
                "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
                "left_right_balance","left_power_phase","right_power_phase","left_power_phase_peak","right_power_phase_peak",
                "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness","left_pco","right_pco"};

        private static final Integer[] fieldindex = {
                108,    // Respiratory
                90,     // Performance Contition
                61,     // ?
                66      // ?
        };

        private static final String[] fieldnames_for_out = {"position_lat","position_long","altitude","enhanced_altitude","speed","enhanced_speed",
                "grade","cadence","fractional_cadence","distance","temperature","calories","heart_rate","power","accumulated_power",
                "left_right_balance","left_right_balance_persent","left_power_phase_start","left_power_phase_end","right_power_phase_start",
                "right_power_phase_end","left_power_phase_peak_start","left_power_phase_peak_end","right_power_phase_peak_start","right_power_phase_peak_end",
                "left_torque_effectiveness","right_torque_effectiveness","left_pedal_smoothness","right_pedal_smoothness","left_pco","right_pco",
                "respiratory","performance_contition","field_num_61","field_num_66"};

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

        private boolean EmptyTrack = true;      // признак того, что трек не содержит координат
        private boolean SaveIfEmpty = false;     // разрешить сохранение пустого трека без точек
        private Long Local_Timestamp = 0L;      //
        private Long mesgTimestamp;
        private double HrvTime = 0.0;

        private Date FileTimeStamp = new Date();
        private Date NewFileTime = new Date();  // Дата и время начала трека, если необходимо сдвинуть время
        private String DeviceCreator = "";
        
        private long timeOffset = 0L;   // смещение времени, для коррекции треков, в секундах
        private boolean needOffset = false;

        private int OutputFormat = 1;   // формат вывода, по умолчанию 1 = gpx, 0 = csv, 2 = hr-csv, 3 = hrv-csv

        private double lastGoodRR = 999.0;
        private double currentRR;
        private double thresholdFilterHRV = 35.0;
        private double deltaFilterHRV;
        private boolean useFilterHRV = false;

        void setOutputFormat(int outputFormat) {
            OutputFormat = outputFormat;
        }
        void setSaveIfEmpty(boolean saveIfEmpty) {SaveIfEmpty = saveIfEmpty;}
        void setUseFilterHRV(boolean useFilter) {useFilterHRV = useFilter;}
        void setFilterHRV(Integer FilterFactor) {
            if(FilterFactor != null &&  FilterFactor > 0 && FilterFactor < 100) {
                thresholdFilterHRV = (double)FilterFactor;
            }
        }
        void setInputFITfileName(String inputFITfileName) {InputFITfileName = String.valueOf(inputFITfileName);}
        String getInputFITfileName() {return InputFITfileName;}
        
        public void setNewFileTime(String newtime) {
            try {
                NewFileTime = NewFileTimeFormat.parse(newtime);
                needOffset = true;
            } catch (ParseException e) {
                System.err.println(tr.getString("ErrorTimeToShift"));
                needOffset = false;
                
                //e.printStackTrace();
            }
        }

        void setUseISOdate(boolean b) {
            if(b) {
                DateFormatCSV = ISODateFormatCSV;
                DateFormatCSVms = ISODateFormatCSVms;
            } else {
                DateFormatCSV = nonISODateFormatCSV;
                DateFormatCSVms = nonISODateFormatCSVms;
            }
        }
        
        void setNewOffset(long newOffset) {
            timeOffset = newOffset;
        }

        int run() {  // Основной поэтапный цикл работы конвертера

            int checkStatus = this.check();
            if(checkStatus != 0) {return checkStatus;}

            EmptyTrack = true;

            int readStatus = this.read();
            if(readStatus !=0) {return readStatus;}

            int writeStatus = this.write();
            activity.clear();
            short_buffer.clear();
            full_buffer.clear();
            array_buffer.clear();

            return writeStatus;
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

        private int read() {    // попытка прочитать входной файл

            Decode decode = new Decode();

            MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);

            FileIdMesgListener fileIdMesgListener = mesg -> {

                String _Product = "";
                String _Manufacturer = "";

                if (mesg.getTimeCreated() != null) {

                    if(needOffset) {
                        setNewOffset((NewFileTime.getTime() / 1000) - mesg.getTimeCreated().getTimestamp() - (DateTime.OFFSET / 1000));
                    }

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

                            if (mesg.getFieldStringValue("timestamp") != null && mesg.getName().equals("record")) {

                                Map<String,String> fields = new HashMap<>();

                                TimeStamp = new Date((mesg.getFieldLongValue("timestamp") * 1000) + DateTime.OFFSET + (timeOffset * 1000));

                                for(String field:fieldnames) {
                                    if(mesg.getFieldStringValue(field) != null) {
                                        String value = mesg.getFieldStringValue(field);
                                        if(field.equals("position_lat") || field.equals("position_long")) {
                                            value = semicircleToDegree(mesg.getField(field)).toString();
                                        }
                                        if(field.equals("left_power_phase") || field.equals("right_power_phase") ||
                                                field.equals("left_power_phase_peak") || field.equals("right_power_phase_peak")) {
                                            fields.put(field + "_start",mesg.getFieldStringValue(field,0));
                                            fields.put(field + "_end",mesg.getFieldStringValue(field,1));
                                        } else if (field.equals("left_right_balance")) {
                                            fields.put(field,value);
                                            fields.put(field + "_persent",String.valueOf((mesg.getFieldDoubleValue("left_right_balance") / 3.6) - 50.0));
                                        } else {
                                            fields.put(field,value);
                                            }
                                        }
                                    }
                                for(Integer field:fieldindex) {
                                    if(mesg.getFieldStringValue(field) != null) {
                                        String value = mesg.getFieldStringValue(field);
                                        if(field == 108) {
                                            fields.put("respiratory",value);
                                        } else if(field == 90) {
                                            fields.put("performance_contition",value);
                                        } else {
                                            fields.put("field_num_" + field, value);
                                        }
                                    }
                                }
                                if(fields.containsKey("position_lat") && fields.containsKey("position_long")) { EmptyTrack = false; }

                                String RecordedDate = DateFormatCSV.format(TimeStamp);

                                if(full_buffer.containsKey(RecordedDate)) {
                                    for(String key:full_buffer.get(RecordedDate).keySet()) {
                                        fields.put(key, full_buffer.get(RecordedDate).get(key));
                                    }
                                }

                                full_buffer.put(RecordedDate, new HashMap<>() {
                                    {
                                        put("GPXtime",DateFormatGPX.format(TimeStamp));
                                        for (Map.Entry<String, String> n : fields.entrySet()) {
                                            put(n.getKey(), n.getValue());
                                        }
                                    }
                                });
                            }
                            break;

                        case 6: // Table output - Only HR and Time from actyvites

                            if (mesg.getFieldStringValue("timestamp") != null && mesg.getName().equals("record")) {
                                if (mesg.getFieldStringValue("heart_rate") != null) {
                                    TimeStamp = new Date((mesg.getFieldLongValue("timestamp") * 1000) + DateTime.OFFSET + (timeOffset * 1000));
                                    short_buffer.put(DateFormatCSV.format(TimeStamp), mesg.getFieldStringValue("heart_rate"));
                                    EmptyTrack = false;
                                }
                            }
                           break;

                        case 2:  // monitor HR data

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

                        case 3: // R-R data

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
            
            mesgBroadcaster.addListener(fileIdMesgListener);
            mesgBroadcaster.addListener(mesgListener);
            
            try {
                mesgBroadcaster.run(new BufferedInputStream(new FileInputStream(InputFITfile)));
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

            switch (OutputFormat) {     // format output from buffer to text

                case 0:
                    StringBuilder head = new StringBuilder("time");
                    for(String name: fieldnames_for_out) {
                        head.append(";").append(name);
                    }
                    activity.add(0, head.toString());

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

                case 1:
                    activity.add(0, out_gpx_head.replace("{creator}", DeviceCreator));
                    activity.add(1, out_gpx_head1.replace("{time}", DateFormatGPX.format(FileTimeStamp)));
                    activity.add(2, out_gpx_head2.replace("{FTIFile}", InputFITfile.getName()));

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
                    activity.add(out_gpx_tail);
                    break;

                case 2:     // monitor: HR
                case 4:     // monitor: SpO2
                case 6:     // activity: Only HR
                    for(Map.Entry<String,String> m: short_buffer.entrySet()){
                        activity.add(m.getKey() + ";" + m.getValue());
                    }
                    break;

                case 3:     // activity: HRV (R-R)
                    activity.add(0, "Timestamp,Time,RR,HR"); // заголовок IBI файла вариабельности ЧСС
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

        private int write() {   // попытка записать выходной файл

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

                switch (OutputFormat) {
                    case 0:
                        OutputFileName = InputFITfileName + ".csv";
                        break;
                    case 1:
                        OutputFileName = InputFITfileName + ".gpx";
                        break;
                    case 2:
                        OutputFileName = InputFITfileName + ".monitor-HR.csv";
                        break;
                    case 3:
                        OutputFileName = InputFITfileName + ".HRV.csv";
                        break;
                    case 4:
                        OutputFileName = InputFITfileName + ".SpO2.csv";
                        break;
                    case 5:
                        OutputFileName = InputFITfileName + ".GSI.csv";
                        break;
                    case 6:
                        OutputFileName = InputFITfileName + ".HR.csv";
                        break;
                    case 99:
                        OutputFileName = InputFITfileName + ".DUMP.txt";
                        break;
                }
            }

            try {

                File OutputFile = new File(OutputFileName);

                if (!OutputFile.exists()) {
                    if(!OutputFile.createNewFile()) {return 73;}
                }

                try { // пытаемся записать в файл
                    BufferedWriter OutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputFile, false), StandardCharsets.UTF_8));
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

        //public int getGoodFilesCount() {return GoodFiles.size();}
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
