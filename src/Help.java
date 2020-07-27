/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

*/

class Help {

    private static final String Version = "\n" + fit2gpx.tr.getString("Help_Version") + fit2gpx._version_;

    static void usage() {
        System.out.println(Version + "\n\n" + fit2gpx.tr.getString("Help_Author"));
        System.out.println(fit2gpx.tr.getString("Help_line01"));
        System.out.println(fit2gpx.tr.getString("Help_line02"));
        System.out.println(fit2gpx.tr.getString("Help_line03"));
        System.out.println(fit2gpx.tr.getString("Help_line04"));

        System.out.println(fit2gpx.tr.getString("Help_line09"));
        System.out.println(fit2gpx.tr.getString("Help_line10"));
        System.out.println(fit2gpx.tr.getString("Help_line11"));

        System.exit(64);
    }

    static void reader_usage() {
        System.out.println(Version + "\n\n" + fit2gpx.tr.getString("Help_Author"));
        System.out.println("Информация по использованию:\n\tjava -jar FitReader.jar <файл Garmin .FIT> > <выходной файл>\n");
        System.exit(64);
    }

    static void error_no_file() {
        System.out.println(Version + "\n\n" + fit2gpx.tr.getString("Help_Author"));
        System.out.println(fit2gpx.tr.getString("Help_line01"));
        System.out.println(fit2gpx.tr.getString("Help_error01"));
    }

}
