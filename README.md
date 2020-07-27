# fit2gpx

Converter Garmin .FIT files to GPX, CVS, HR, HRV. Version 0.0.5

© Maks Vasilev, 2015-2020, http://velo100.ru/garmin-fit-to-gpx

FIT Software Development Kit (SDK), http://www.thisisant.com

Usage:

        java -jar fit2gpx.jar --help

Console mode:

        java -jar fit2gpx.jar [options] <file Garmin .FIT> [[<file Garmin .FIT>] …]

Graphic dialog mode:

        java -jar fit2gpx.jar [options]

options:

                --statistic     | -s    output of final statistics to the console
                --csv           | -c    the output file will be in CSV format
                --hr-only               the output file will be in CSV format, content: heart rate and time only
                --monitor       | -m    parsing heart rate monitoring files (not training), the output file will be in CSV format
                --hrv           | -v    parsing training files and writing R-R intervals to CSV for variability analysis
                --no-dialog     | -n    do not display the file selection dialog
                --save-empty    | -e    save files without coordinates (empty tracks)

The Converter has two modes of operation: console and graphic. When running without files in parameters, a dialog box opens
for selecting the file to convert. If you use the file name as an argument at startup, the conversion occurs without launching dialog boxes.
If the conversion is successful, no information is returned to the console.
The console mode can be used in various automation scripts.

Converter supports batch mode file processing for this in console mode, specify all file names as arguments to the program.
In dialog mode, simply select multiple files in the selection dialog.

Dump all the headers of the file (debug mode):

        java -cp fit2gpx.jar DebugDecode <file Garmin .FIT>
      
The minimum Java version is 11.0

# Russian

Конвертер файлов Garmin .FIT в GPX, CVS, HR, HRV. Версия 0.0.5

© Maks Vasilev, 2015-2020, http://velo100.ru/garmin-fit-to-gpx

FIT Software Development Kit (SDK), http://www.thisisant.com

Информация по использованию:

        java -jar fit2gpx.jar --help

Консольный режим:

        java -jar fit2gpx.jar [параметры] <файл Garmin .FIT> [[<файл Garmin .FIT>] …]

Графический диалоговый режим:

        java -jar fit2gpx.jar [параметры]

параметры:

                --statistic     | -s    вывод итоговой статистики в консоль
                --csv           | -c    выходной файл будет в формате CSV
                --hr-only               выходной файл будет в формате CSV, содержимое: только ЧСС и время
                --monitor       | -m    разбор файлов мониторинга пульса (не тренировки), выходной файл будет в формате CSV
                --hrv           | -v    разбор файлов тренировки и запись в CSV интервалов R-R для анализа вариабельности
                --no-dialog     | -n    не отображать диалоговое окно выбора файлов
                --save-empty    | -e    сохранять файлы без координат (пустые треки)

Конвертер имеет два режима работы: консольный и графический. При запуске без указания файлов для конвертации
запускается диалоговое окно выбора файлов для конвертации. Если при запуске в качестве аргумента передать имя файла, то
происходит конвертация без запуска диалоговых окон. В случае успешной конвертации никакой информации в консоль не возвращается.
Консольный режим работы можно использовать в различных скриптах для автоматизации.

Конвертер поддерживает режим пакетной обработки файлов, для этого в консольном режиме укажите имена всех файлов
в качестве аргументов программы. В диалоговом режиме просто выберите несколько файлов в диалоге выбора.

Дамп всех заголовков файла (отладочный режим):

        java -cp fit2gpx.jar DebugDecode <файл Garmin .FIT>

Минимальная версия Java - 11.0
