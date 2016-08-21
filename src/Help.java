/*
Copyright © 2015 by Maks Vasilev

created 7.02.2015

http://velo100.ru/garmin-fit-to-gpx

*/

class Help {

    private static final String Version = "\nКонвертер Garmin .FIT в .GPX/.CVS, Версия 0.0.3а";

    static void usage() {
        System.out.println(Version + "\n\n© Maks Vasilev, 2015-2016, http://velo100.ru/garmin-fit-to-gpx\nFIT Software Development Kit (SDK), http://www.thisisant.com/resources/fit\n");
        System.out.println("Информация по использованию:\n\tjava -jar fit2gpx.jar --help\n");
        System.out.println("Консольный режим:\n\tjava -jar fit2gpx.jar [--statistic|-s] <файл Garmin .FIT> [[<файл Garmin .FIT>] …]\n");
        System.out.println("Графический диалоговый режим:\n\tjava -jar fit2gpx.jar [--statistic|-s]\n");
        System.out.println("\t\t--statistic\t| -s\tвывод итоговой статистики в консоль");
        System.out.println("\t\t--csv\t\t| -c\tвыходной файл будет в формате CSV");
        System.out.println("\t\t--hr-only\t\t\tвыходной файл будет в формате CSV, содержимое: только ЧСС и время");
        System.out.println("\t\t--monitor\t| -m\tразбор файлов мониторинга пульса (не тренировки), выходной файл будет в формате CSV");
        System.out.println("\nКонвертер имеет два режима работы: консольный и графический. При запуске без параметров запускается диалоговое окно");
        System.out.println("выбора файла для конвертации. Если при запуске в качестве аргумента передать имя файла, то происходит конвертация");
        System.out.println("без запуска диалоговых окон. В случае успешной конвертации никакой информации в консоль не возвращается.");
        System.out.println("Консольный режим работы можно использовать в различных скриптах для автоматизации.");
        System.out.println("\nКонвертер поддерживает режим пакетной обработки файлов, для этого в консольном режиме укажите имена всех файлов");
        System.out.println("в качестве аргументов программы. В диалоговом режиме просто выберите несколько файлов в диалоге выбора.");
        System.out.println("\nДамп всех заголовков файла (отладочный режим):");
        System.out.println("\tjava -cp fit2gpx.jar DebugDecode <файл Garmin .FIT>\n");

        System.exit(64);
    }

    static void reader_usage() {
        System.out.println(Version + "\n\n© Maks Vasilev, 2016, http://velo100.ru/garmin-fit-to-gpx\nFIT Software Development Kit (SDK), http://www.thisisant.com/resources/fit\n");
        System.out.println("Информация по использованию:\n\tjava -jar FitReader.jar <файл Garmin .FIT> > <выходной файл>\n");
        System.exit(64);
    }

}

