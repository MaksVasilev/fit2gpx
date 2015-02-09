/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015
http://velo100.ru/garmin-fit-to-gpx

exit code:

0 - ok
64 - help or invalid usage
65 - file invalid
66 - file not found
200 - track is empty
204 - no file selected
209 - debug break

*/

import com.garmin.fit.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class fit2gpx extends Component {

    public static void main(String[] args) throws IOException {

        File[] MultipleFilesList;

        Converter converter = new Converter();
        //            converter.setInputFITfileName(String.valueOf(args[0]));;


        if (args.length == 1) {
             if (String.valueOf(args[0]).equals("--help") || String.valueOf(args[0]).equals("-h") ) {
                 Help.usage();
             } else {
                 converter.setInputFITfileName(String.valueOf(args[0]));
                 converter.run();
             }
        }

        if (args.length == 0) {

            converter.setUseDialog(true);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("FIT -> GPX: Выберите файл для преобразования в GPX");
            chooser.setApproveButtonText("Открыть");
            chooser.setApproveButtonToolTipText("Открыть выбранный файл и преобразовать");
            chooser.setMultiSelectionEnabled(true);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("файлы занятий Garmin FIT (.fit)", "FIT", "fit");
            chooser.setFileFilter(filter);

            int returnVal = chooser.showOpenDialog(chooser.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                MultipleFilesList = chooser.getSelectedFiles();

                //if (MultipleFilesList.length == 1) {
                //    converter.setInputFITfileName(MultipleFilesList[0].getAbsoluteFile().getAbsolutePath());

                //} else {
                    //String[] FileList = new String[MultipleFilesList.length];
                    for (int f = 0; f < MultipleFilesList.length; f++) {
                        //FileList[f] = MultipleFilesList[f].getAbsoluteFile().getAbsolutePath();
                        converter.setInputFITfileName(MultipleFilesList[f].getAbsoluteFile().getAbsolutePath());
                        converter.run();
                    }
                    //MultiFileBatch(FileList);
                //}

            } else {
                System.exit(204);
            }

          }

        if (args.length > 1) {
            for(int f = 0; f < args.length; f++) {
                converter.setInputFITfileName(args[f]);
                converter.run();
            }
        }

    }

    static class Converter {

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

/*        private void MultiFileBatch(String[] fileList) {

            int MultipleFilesNonEmpty = 0;

            System.out.println("Выбрано файлов: " + fileList.length);

            for (int i = 0; i < fileList.length; i++) {
                System.out.println(fileList[i]);
                //String workerArgs[] = {MultipleFilesList[i].getAbsoluteFile().getAbsolutePath()};
                final fit2gpx worker = new fit2gpx();
                //worker
            }
            System.exit(209);
        }
*/

        private String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gpx creator=\"Converted by fit2gpx, http://velo100.ru/garmin-fit-to-gpx\" version=\"1.1\" " +
                "xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                "xmlns:gpxtrx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" " +
                "xmlns:gpxx=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" " +
                "xmlns:nmea=\"http://trekbuddy.net/2009/01/gpx/nmea\">" +
                "\n <metadata>\n  <time>{time}</time>\n </metadata>\n";
        private String head2 = " <trk>\n  <name>{FTIFile}</name>\n  <trkseg>";
        private String tail = "\n  </trkseg>\n </trk>\n</gpx>";

        private final ArrayList<String> activity = new ArrayList<String>();
        private Date TimeStamp = new Date();

        private SimpleDateFormat DateFormatGPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        private String InputFITfileName;
        private String OutputGPXfileName;

        private FileInputStream InputStream;
        private File InputFITfile;

        private boolean FirstLine = true;
        private boolean EmptyTrack = true;
        private boolean UseDialog = false;

        public void setInputFITfileName(String inputFITfileName) {InputFITfileName = String.valueOf(inputFITfileName);}
        public void setUseDialog(boolean useDialog) {UseDialog = useDialog;}

        public void run() {
            this.check();
            this.read();
            this.write();

            return;
        }

        private void check() {

            try {
                InputFITfile = new File(InputFITfileName);
                InputStream = new FileInputStream(InputFITfile);

            } catch (IOException e) {
                System.err.println("Ошибка: " + InputFITfileName + " не найден или не является файлом!");
                System.exit(66);
                //return;
            }

            try {
                if (!Decode.checkIntegrity(InputStream)) {
                    throw new RuntimeException("файл " + InputFITfileName + " повреждён!");
                }
            } catch (RuntimeException e) {
                System.err.print("Ошибка проверки файла: ");
                System.err.println(e.getMessage());

                try {
                    InputStream.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
                System.exit(65);
                //return;
            }
        }

        private void read() {

            final Decode decode = new Decode();
            decode.addListener(new MesgListener() {

                @Override
                public void onMesg(Mesg mesg) {

                    String line = "";

                    if (mesg.getFieldStringValue("timestamp") != null) {


                        TimeStamp = new Date((Long.parseLong(mesg.getFieldStringValue("timestamp")) + 631065600) * 1000);

                        if (FirstLine) {
                            line = head.replace("{time}", DateFormatGPX.format(TimeStamp));
                            line += head2.replace("{FTIFile}", InputFITfile.getName());
                            FirstLine = false;
                            activity.add(line);
                            line = "";
                        }

                        final Number lat = semicircleToDegree(mesg.getField("position_lat"));
                        final Number lon = semicircleToDegree(mesg.getField("position_long"));

                        if (lat != null && lon != null) {

                            EmptyTrack = false;

                            line += "\n   <trkpt lat=\"" + lat.toString() + "\" lon=\"" + lon.toString() + "\">\n" +
                                    "    <time>" + DateFormatGPX.format(TimeStamp) + "</time>";

                            if (mesg.getFieldStringValue("altitude") != null) {
                                line += "\n    <ele>" + round(mesg.getFieldDoubleValue("altitude"), 3) + "</ele>";
                            }

                            if (mesg.getFieldStringValue("temperature") != null || mesg.getFieldStringValue("heart_rate") != null
                                    || mesg.getFieldStringValue("cadence") != null || mesg.getFieldStringValue("speed") != null) {

                                line += "\n    <extensions>";

                                if (mesg.getFieldStringValue("speed") != null) {
                                    line += "\n     <nmea:speed>" + mesg.getFieldStringValue("speed") + "</nmea:speed>";
                                }
                                line += "\n     <gpxtpx:TrackPointExtension>";

                                if (mesg.getFieldStringValue("temperature") != null) {
                                    line += "\n      <gpxtpx:atemp>" + mesg.getFieldStringValue("temperature") + "</gpxtpx:atemp>";
                                }
                                if (mesg.getFieldStringValue("heart_rate") != null) {
                                    line += "\n      <gpxtpx:hr>" + mesg.getFieldStringValue("heart_rate") + "</gpxtpx:hr>";
                                }
                                if (mesg.getFieldStringValue("cadence") != null) {
                                    line += "\n      <gpxtpx:cad>" + mesg.getFieldStringValue("cadence") + "</gpxtpx:cad>";
                                }
                                if (mesg.getFieldStringValue("speed") != null) {
                                    line += "\n      <gpxtpx:speed>" + mesg.getFieldStringValue("speed") + "</gpxtpx:speed>";
                                }

                                line += "\n     </gpxtpx:TrackPointExtension>\n    </extensions>";
                            }

                            // неоприходованные поля
                            // mesg.getFieldStringValue("distance")

                            line += "\n   </trkpt>";

                            activity.add(line);
                        }
                    }

                }
            });

            try {
                decode.read(new BufferedInputStream(new FileInputStream(InputFITfile)));
            } catch (FitRuntimeException e) {
                System.err.print("Ошибка обработки файла " + InputFITfile + ": ");
                System.err.println(e.getMessage());

                try {
                    InputStream.close();
                } catch (IOException f) {
                    throw new RuntimeException(f);
                }

                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                InputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            activity.add(tail);
        }

        private void write() {
            if (EmptyTrack && UseDialog) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Файл " + InputFITfileName + " не содержит трека,\nпустой файл не был сохранён.", "Трек отсутствует", JOptionPane.WARNING_MESSAGE);
                System.exit(200);
            }

            if (EmptyTrack) {
                OutputGPXfileName = InputFITfileName + ".empty";
            } else {
                OutputGPXfileName = InputFITfileName + ".gpx";
            }

            try {

                File OutputGPXfile = new File(OutputGPXfileName);

                if (!OutputGPXfile.exists()) {
                    OutputGPXfile.createNewFile();
                }

                PrintWriter OutWriter = new PrintWriter(OutputGPXfile.getAbsoluteFile());

                try {

                    for (String str : activity) {
                        OutWriter.write(str);
                    }

                    if (UseDialog) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Файл GPX с треком сохранён как:\n" + OutputGPXfileName, "Трек успешно обработан", JOptionPane.INFORMATION_MESSAGE);
                    }

                } finally {
                    OutWriter.close();
                    if (EmptyTrack) {
                        System.exit(200);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
                //System.exit(73);
            }
        }

    }
}
