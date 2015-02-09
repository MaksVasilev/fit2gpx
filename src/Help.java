/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

*/

public class Help {

    public static final String Version = "\nКонвертер Garmin .FIT в .GPX, Версия 0.0.¾";

    public static void usage() {
        System.out.println(Version + "\n\n© Maks Vasilev, 2015, http://velo100.ru/garmin-fit-to-gpx\nFIT Software Development Kit (SDK), http://www.thisisant.com/resources/fit\n\nКонсольный режим:\n\tjava -jar fit2gpx.jar <файл Garmin .FIT>\n");
        System.out.println("Графический диалоговый режим:\n\tjava -jar fit2gpx.jar\n");
        System.out.println("Конвертер имеет два режима работы: консольный и графический. При запуске без параметров запускается диалоговое окно");
        System.out.println("выбора файла для конвертации. Если при запуске в качестве аргумента передать имя файла, то происходит конвертация");
        System.out.println("без запуска диалоговых окон. В случае успешной конвертации никакой информации в консоль не возвращается.");
        System.out.println("Консольный режим работы можно использовать в различных скриптах для автоматизации.");

        System.exit(0);
    }
}
