public class Help {

    public static final String Version = "\nКонвертер Garmin .FIT в .GPX, Версия 0.0.½";

    public static void usage() {
        System.out.println(Version + "\n\nИспользование программы:\n\tfit2gpx <файл Garmin .FIT>\n");

        System.exit(0);

    }
}
