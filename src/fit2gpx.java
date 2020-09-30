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

import format.Mode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

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
            if ( arg.equals("--csv") || arg.equals("-c")) {  converter.setOutputFormat(0); converter.setMode(Mode.CSV); converter.setSaveIfEmpty(true); }
            if ( arg.equals("--monitor-hr") || arg.equals("-mh")) {  converter.setOutputFormat(2); converter.setMode(Mode.MONITOR_HR);}
            if ( arg.equals("--hrv") || arg.equals("-vr")) {  converter.setOutputFormat(3); converter.setMode(Mode.HRV); }
            if ( arg.equals("--hrv-filter") || arg.equals("-vf")) {  converter.setOutputFormat(3); converter.setMode(Mode.HRV); converter.setUseFilterHRV(true); }
            if ( arg.equals("--monitor-oxy") || arg.equals("-spo")) { converter.setOutputFormat(4); converter.setMode(Mode.MONITOR_SPO2); }
            if ( arg.equals("--monitor-stress") || arg.equals("-si")) { converter.setOutputFormat(5); converter.setMode(Mode.MONITOR_GSI); }
            if ( arg.equals("--hr-only") || arg.equals("-hr")) {  converter.setOutputFormat(6); converter.setMode(Mode.CSV_HR); converter.setSaveIfEmpty(true); }
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

            FileNameExtensionFilter filter;
            if(converter.OutputFormat == 2 || converter.OutputFormat == 4 || converter.OutputFormat == 5) {
                filter = new FileNameExtensionFilter(tr.getString("OpenEXTmon"), "FIT", "fit");
            } else {
                filter = new FileNameExtensionFilter(tr.getString("OpenEXTact"), "FIT", "fit");
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
