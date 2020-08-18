# fit2gpx

Конвертер файлов Garmin .FIT в GPX, CVS, HR, HRV, SpO2, Stress. Версия 0.1.1

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
                --hr-only       | -r    выходной файл будет в формате CSV, содержимое: только ЧСС и время
                --monitor       | -m    разбор файлов мониторинга пульса (не тренировки), выходной файл будет в формате CSV
                --hrv           | -v    разбор файлов тренировки и запись в CSV интервалов R-R для анализа вариабельности
                --hrv-filter    | -f    тоже самое, что и --hrv, но используется пороговый фильтр для устранения всплесков
                        --filter=n      где: n - уставка порогового фильтра (для -f) в % от 1 до 99 (по умолчанию 35)

                --oxy           | -o    разбор файлов мониторинга и запись в CSV значений оксигенации SpO2
                --stress        | -i    разбор файлов мониторинга и запись в CSV значений уровня стресса
                        поля в CSV: Дата время; Индекс Стресса Garmin; Уровень батареи тела; неизвестно; неизвестно

                --no-dialog     | -n    не отображать диалоговое окно выбора файлов
                --save-empty    | -e    сохранять файлы без координат (пустые треки)
                --full-dump             сделать полный дамп записей файла в тестовый файл  

Конвертер имеет два режима работы: консольный и графический. При запуске без указания файлов для конвертации
запускается диалоговое окно выбора файлов для конвертации. Если при запуске в качестве аргумента передать имя файла, то
происходит конвертация без запуска диалоговых окон. В случае успешной конвертации никакой информации в консоль не возвращается.
Консольный режим работы можно использовать в различных скриптах для автоматизации.

Конвертер поддерживает режим пакетной обработки файлов, для этого в консольном режиме укажите имена всех файлов
в качестве аргументов программы. В диалоговом режиме просто выберите несколько файлов в диалоге выбора.

Дамп всех заголовков файла (отладочный режим):

        java -cp fit2gpx.jar DebugDecode <файл Garmin .FIT>

Минимальная версия Java - 11.0

# English

Converter Garmin .FIT files to GPX, CVS, HR, HRV, SpO2, Stress. Version 0.1.1

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
                --hr-only       | -r    the output file will be in CSV format, content: heart rate and time only
                --monitor       | -m    parsing heart rate monitoring files (not training), the output file will be in CSV format
                --hrv           | -v    parsing training files and writing R-R intervals to CSV for variability analysis
                --hrv-filter    | -f    same as --hrv, but threshold filter is used to eliminate spikes
                        --filter=n      where: n - threshold filter value (for -f) in % from 1 to 99 (default 35)

                --oxy           | -o    parsing monitoring files and writing SpO2 oxygenation values in CSV
                --stress        | -i    parsing monitoring files and writing Stress values in CSV
                        fields in CSV: Date time; Garmin Stress Index; Body Battery; unknown; unknown

                --no-dialog     | -n    do not display the file selection dialog
                --save-empty    | -e    save files without coordinates (empty tracks)
                --full-dump             create full text dump of all messages  

The Converter has two modes of operation: console and graphic. When running without files in parameters, a dialog box opens
for selecting the file to convert. If you use the file name as an argument at startup, the conversion occurs without launching dialog boxes.
If the conversion is successful, no information is returned to the console.
The console mode can be used in various automation scripts.

Converter supports batch mode file processing for this in console mode, specify all file names as arguments to the program.
In dialog mode, simply select multiple files in the selection dialog.

Dump all the headers of the file (debug mode):

        java -cp fit2gpx.jar DebugDecode <file Garmin .FIT>
      
The minimum Java version is 11.0
