/**
 Copyright © 2015 by Maks Vasilev

 created 30.07.2016
 http://velo100.ru/garmin-fit-to-gpx

 */

import com.garmin.fit.*;

import javax.print.DocFlavor;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FitReader {

    public static void main(String[] args) throws IOException {

        File FITFile;

        Parser parser = new Parser();

        if (args.length == 1) {
            parser.setFITFileName(args[0]);
            parser.read();

        } else {
            Help.reader_usage();
        }

    }

    private static class Parser{
        private String FITFileName;             // полное имя файла для чтения
        private FileInputStream InputStream;    // поток для тения файла

        final Decode decode = new Decode();

        void setFITFileName(String fitFileName) {
            FITFileName = fitFileName;
            this.chechFITFile();

        }

        void chechFITFile() {

            try {
                InputStream = new FileInputStream(FITFileName);
            } catch (FileNotFoundException e) {
                System.err.println("Ошибка: " + FITFileName + " не найден или не является файлом!");

                e.printStackTrace();    // DEBUG

                System.exit(66);
            }

            /*

            try {
                if (!decode.checkFileIntegrity(InputStream)) {
                    throw new RuntimeException("файл " + FITFileName + " не является FIT файлом или повреждён!");
                }
            } catch (RuntimeException e) {
                System.err.print("Ошибка проверки файла: ");
                System.err.println(e.getMessage());
                System.exit(65);
                try {
                    InputStream.close();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
                System.exit(65);

            } */
        }

        public void read() {
            MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);

            MesgListener mesgListener = new MesgListener() {

                @Override
                public void onMesg(Mesg mesg) {

                    System.out.println("[" + mesg.getLocalNum() + "] " + mesg.getName());

                 //   if(mesg.getLocalNum() == 128) {
                        //System.out.println("12\n" + mesg.getNumFields());

                       // System.out.println(mesg.getFields());
                        for (Field f: mesg.getFields() ) {
                            if (f.getNumValues() == 1) {
                                System.out.println("\t[" + f.getNum() + "] " + f.getName() + " " + f.getValue() + " " + f.getUnits());
                            } else {
                                String values="";
                     //           System.out.println(f.getType());
                     //           System.out.println(f.getProfileType());
                                for (int sf =0 ; sf < f.getNumValues(); sf++) {
                                    if(f.getProfileType() == Profile.Type.STRING) {
                                        values += f.getStringValue(sf).toString() + " ";
                                    } else {
                                        values += f.getDoubleValue(sf).toString() + " ";
                                    }
                                }
                                System.out.println("\t\t" + f.getNumValues() + "x[" + f.getNum() + "] " + values + " " + f.getUnits());
                            }
                        //    System.out.println(f.getValue());
                        //    System.out.println(f.getUnits());


                        }
                 //   }

                }
            };

            mesgBroadcaster.addListener(mesgListener);

            try {
                mesgBroadcaster.run(new BufferedInputStream(InputStream));
            } catch (FitRuntimeException e) {
                System.err.print("Ошибка обработки файла " + FITFileName + ": ");
                System.err.println(e.getMessage());

                try {
                    InputStream.close();
                } catch (IOException f) {
                    throw new RuntimeException(f);
                }

                System.exit(199);
            }

        }

    }


}
