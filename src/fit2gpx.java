/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

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

    static private Number semicircleToDegree(Field field) {
        if (field != null && "semicircles".equals(field.getUnits())) {
            final long semicircle = field.getLongValue();
            return semicircle * (180.0 / Math.pow(2.0,31.0)); // degrees = semicircles * ( 180 / 2^31 )
        } else {
            return null;
        }
    }

    public static double round(double d, int p) {
        double dd=Math.pow(10, p);
        return Math.round(d*dd) / dd;
    }

//    public static int round(double d) {
//        return (int)Math.round(d);
//    }

    static String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx creator=\"fit2gpx\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
            "http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 " +
            "http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 " +
            "http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 " +
            "http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 " +
            "http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\" " +
            "xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" " +
            "xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\">\n" +
            " <metadata>\n  <time>{time}</time>\n </metadata>\n";
    static String head2 = " <trk>\n  <name>{FTIFile}</name>\n  <trkseg>";
    static String tail = "\n  </trkseg>\n </trk>\n</gpx>";

    static final ArrayList<String> activity = new ArrayList<String>();
    static Date TimeStamp = new Date();

    static SimpleDateFormat DateFormatGPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static String InputFITfileName;
    static String OutputGPXfileName;

    static FileInputStream InputStream;
    static File InputFITfile;

    static boolean FirstLine = true;
    static boolean EmptyTrack = true;
    static boolean UseDialog = false;

    public static void main(String[] args) throws IOException {

        final Decode decode = new Decode();


        if(args.length == 1) {
            InputFITfileName = String.valueOf(args[0]);
        }

        if(args.length == 0) {

            UseDialog = true;

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "файлы занятий Garmin FIT (.fit)", "FIT", "fit");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(chooser.getParent());
            if(returnVal == JFileChooser.APPROVE_OPTION) {

                InputFITfileName = chooser.getSelectedFile().getAbsoluteFile().getAbsolutePath();
                // System.out.println("You chose to open this file: " + InputFITfileName);

            } else {
                System.exit(0);
            }

            if(InputFITfileName.equals("")) {
                System.exit(0);
            }
        }

        if(args.length > 1) {
            Help.usage();
        }

        try {
            InputFITfile = new File(InputFITfileName);
            InputStream = new FileInputStream(InputFITfile);

        } catch (IOException e) {
            System.err.println("Ошибка: " + InputFITfileName + " не найден или не является файлом!");
            return;
        }

        try {
            if (!Decode.checkIntegrity(InputStream)) {
                throw new RuntimeException("файл " + InputFITfileName + " повреждён!");
            }
        }  catch (RuntimeException e) {
            System.err.print("Ошибка проверки файла: ");
            System.err.println(e.getMessage());

            try {
                InputStream.close();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            return;
        }

        decode.addListener(new MesgListener() {

            @Override
            public void onMesg(Mesg mesg) {

                String line = "";

                if(mesg.getFieldStringValue("timestamp") != null) {


                    TimeStamp = new Date((Long.parseLong(mesg.getFieldStringValue("timestamp")) + 631065600) * 1000);

                    if(FirstLine) {
                        line = head.replace("{time}",DateFormatGPX.format(TimeStamp));
                        line += head2.replace("{FTIFile}",InputFITfileName);
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

                        if(mesg.getFieldStringValue("altitude") != null) {
                            line += "\n    <ele>" + round(mesg.getFieldDoubleValue("altitude"),2) + "</ele>";
                        }

                        if(mesg.getFieldStringValue("temperature") != null || mesg.getFieldStringValue("heart_rate") != null
                                || mesg.getFieldStringValue("cadence") != null || mesg.getFieldStringValue("speed") != null) {

                            line += "\n    <extensions>\n     <gpxtpx:TrackPointExtension>";

                            if(mesg.getFieldStringValue("temperature") != null) {
                                line += "\n      <gpxtpx:atemp>" + mesg.getFieldStringValue("temperature") + "</gpxtpx:atemp>";
                            }
                            if(mesg.getFieldStringValue("heart_rate") != null) {
                                line += "\n      <gpxtpx:hr>" + mesg.getFieldStringValue("heart_rate") + "</gpxtpx:hr>";
                            }
                            if(mesg.getFieldStringValue("cadence") != null) {
                                line += "\n      <gpxtpx:cad>" + mesg.getFieldStringValue("cadence") + "</gpxtpx:cad>";
                            }
                            if(mesg.getFieldStringValue("speed") != null) {
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
        }

        try {
            InputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        activity.add(tail);

        if(EmptyTrack && UseDialog) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Файл " + InputFITfileName + " не содержит трека,\nпустой файл не был сохранён.", "Трек отсутствует", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }

        if(EmptyTrack) {
            OutputGPXfileName = InputFITfileName + ".empty";
        } else {
            OutputGPXfileName = InputFITfileName + ".gpx";
        }

        try {

           File OutputGPXfile = new File(OutputGPXfileName);

            if(!OutputGPXfile.exists()) {
                OutputGPXfile.createNewFile();
            }

            PrintWriter OutWriter = new PrintWriter(OutputGPXfile.getAbsoluteFile());

            try {

                for (String str : activity) {
                    OutWriter.write(str);
                }

                if(UseDialog) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Файл GPX с треком сохранён как:\n" + OutputGPXfileName, "Трек успешно обработан", JOptionPane.INFORMATION_MESSAGE );
                }

            } finally {
                OutWriter.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

    }
}
