/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

*/

import com.garmin.fit.Decode;
import com.garmin.fit.Field;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgListener;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class fit2gpx {

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

    public static int round(double d) {
        return (int)Math.round(d);
    }

    static final ArrayList<String> activity = new ArrayList<String>();
    static Date TimeStamp = new Date();

    //static SimpleDateFormat DateFormatText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat DateFormatGPX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

    static String InputFITfileName;
    static String OutputGPXfileName;

    static boolean FirstLine = true;
    static boolean EmptyTrack = true;

    public static void main(String[] args) throws IOException {

        if(args.length != 1) {
            Help.usage();
        }

        InputFITfileName = String.valueOf(args[0]);

        File InputFITfile = new File(InputFITfileName);


        if (InputFITfile.exists() & InputFITfile.isFile()) {


            final Decode decode = new Decode();
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

            decode.read(new BufferedInputStream(new FileInputStream(InputFITfile)));

            activity.add(tail);

            if(EmptyTrack) {
                OutputGPXfileName = InputFITfileName + ".empty";
            } else {
                OutputGPXfileName = InputFITfileName + ".gpx";
            }

            File OutputGPXfile = new File(OutputGPXfileName);

            try {
                if(!OutputGPXfile.exists()) {
                    OutputGPXfile.createNewFile();
                }

                PrintWriter OutWriter = new PrintWriter(OutputGPXfile.getAbsoluteFile());

                try {
                     for(String str : activity) {
                         OutWriter.write(str);
                    }

                } finally {
                    OutWriter.close();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Файл " + args[0] + " не найден!");
        }

    }
}
