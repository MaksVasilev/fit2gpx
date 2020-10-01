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

import format.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static javax.swing.UIManager.setLookAndFeel;

public class fit2gpx extends Component {

    static final String _version_ = "0.1.7";

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
        String[] Filter;

        Converter converter = new Converter();
        ConverterResult converterResult = new ConverterResult();
        Database database = Database.NONE;
        String db_connect = "";
        String db_prefix = "";

        for (String arg:args) {
            if(xDebug) { System.out.println("argument: " + arg); }
            if ( arg.equals("--help") || arg.equals("-h")) { Help.usage(); }
            if ( arg.equals("--statistic") || arg.equals("-s")) {  StatisticEnable = true; }
            if ( arg.equals("--csv") || arg.equals("-c")) {  converter.setMode(Mode.CSV); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--monitor-hr") || arg.equals("-mh")) {  converter.setMode(Mode.MONITOR_HR);}
            if ( arg.equals("--hrv") || arg.equals("-vr")) {  converter.setMode(Mode.HRV); }
            if ( arg.equals("--hrv-filter") || arg.equals("-vf")) {  converter.setMode(Mode.HRV); converter.setUseFilterHRV(true); }
            if ( arg.equals("--monitor-oxy") || arg.equals("-spo")) { converter.setMode(Mode.MONITOR_SPO2); }
            if ( arg.equals("--monitor-stress") || arg.equals("-si")) { converter.setMode(Mode.MONITOR_GSI); }
            if ( arg.equals("--hr-only") || arg.equals("-hr")) {  converter.setMode(Mode.CSV_HR); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--merge") || arg.equals("-m")) { converter.setMergeOut(true); converter.setOUT(Out.MERGED_FILES); }
            if ( arg.equals("--no-dialog") || arg.equals("-nd") ) { DialogMode = false; }
            if ( arg.equals("--save-empty") || arg.equals("-se") ) { converter.setSaveIfEmpty(true); }
            if ( arg.equals("--db-sqlite") || arg.equals("-dbs") ) { database = Database.SQLITE; }
            if ( arg.equals("--db-pgsql") || arg.equals("-dbp") ) { database = Database.POSTGRESQL; }
            if ( arg.equals("--full-dump")) { converter.setMode(Mode.DUMP); }
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
                converter.setUseISOdate(!isodate[1].equals("no") && !isodate[1].equals("n"));
            }
            if ( arg.startsWith("--db-connect=")) {
                String[] connect = arg.split("=", 2);
                db_connect = connect[1];
            }
            if ( arg.startsWith("--db-prefix=")) {
                String[] connect = arg.split("=", 2);
                db_prefix = connect[1];
            }
        }

        if(database != Database.NONE && !db_connect.equals("")) {
            DB DataBase = new DB(converter.getMODE());
            DataBase.setCreatePolicy(DB_Create_Policy.CREATE_IF_NOT_FOUND);
            DataBase.setAppendPolicy(DB_Append_Policy.REPLACE_ALL);

            if (!DataBase.connctDB(database,db_connect,db_prefix)) {
                System.out.println("Database can't open!");
                System.exit(13);
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
            chooser.setApproveButtonText(tr.getString("Open"));
            chooser.setPreferredSize(new Dimension(1200,600));
            chooser.setApproveButtonToolTipText(tr.getString("OpenTip"));
            chooser.setMultiSelectionEnabled(true);

            FileNameExtensionFilter filter;

            switch (converter.getMODE()) {
                case GPX:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitle"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
                    break;
                case CSV:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleCSV"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
                    break;
                case CSV_HR:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleHR"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
                    break;
                case HRV:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleHRV"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
                    break;
                case MONITOR_HR:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleM"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTmon"), "FIT", "fit");
                    break;
                case MONITOR_SPO2:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleOxy"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTmon"), "FIT", "fit");
                    break;
                case MONITOR_GSI:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleStress"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTmon"), "FIT", "fit");
                    break;
                default:
                    chooser.setDialogTitle(_version_ + " | " + tr.getString("OpenTitleDebug"));
                    filter = new FileNameExtensionFilter(tr.getString("OpenEXTdefault"), "FIT", "fit");
                    break;
            }

            chooser.setFileFilter(filter);

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

}
