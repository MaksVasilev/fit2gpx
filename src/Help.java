/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

*/

public class Help {

    public static final String Version = "\nКонвертер Garmin .FIT в .GPX, Версия 0.0.¾";

    public static void usage() {
        System.out.println(Version + "\n\nИспользование программы:\n\tjava -jar fit2gpx.jar <файл Garmin .FIT>\n");

        System.exit(0);

    }
}
