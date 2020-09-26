# fit2gpx

Конвертер файлов Garmin .FIT в GPX, CVS, HR, HRV, SpO2, Stress. Версия 0.1.4

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
                --merge         | -m    объединить все выходные файлы в один
                         Внимание! в объединённом файле данные могут быть не в хронологическом порядке! (см. issue #21)
                --iso-date=[yes|y|no|n] использовать для CSV формата дату в формате ГОСТ ИСО 8601-2001 (ISO 8601) (по умолчанию 'yes')
                --hr-only       | -hr   выходной файл будет в формате CSV, содержимое: только ЧСС и время
                --monitor-hr    | -mh   разбор файлов мониторинга пульса (не тренировки), выходной файл будет в формате CSV
                --hrv           | -vr   разбор файлов тренировки и запись в CSV интервалов R-R для анализа вариабельности
                --hrv-filter    | -vf   тоже самое, что и --hrv, но используется пороговый фильтр для устранения всплесков
                        --filter=n      где: n - уставка порогового фильтра (для -f) в % от 1 до 99 (по умолчанию 35)

                --monitor-oxy           | -spo  разбор файлов мониторинга и запись в CSV значений оксигенации SpO2
                --monitor-stress        | -si   разбор файлов мониторинга и запись в CSV значений уровня стресса
                        поля в CSV: Дата время; Индекс Стресса Garmin; Уровень батареи тела; дельта; неизвестно

                --no-dialog     | -nd   не отображать диалоговое окно выбора файлов
                --save-empty    | -se   сохранять файлы без координат (пустые треки)
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

# Возможности добавленные после релиза 0.1.0

- 0.1.4 - параметры командной строки и сокращённые параметры изменены!
- new (0.1.4) - объединение всех выходных файлов в один с помощью --merge (данные могут быть не в хронологическом порядке!)
- new (0.1.4) - исправление данных велокомпьютера Bryton Rider (дыры в данных и нулевые значения), проверено на 310 модели
- new (0.1.4) - [внутренний] интерфейс fix() для исправления данных
- new (0.1.3) - добавлена Garmin Running Dinamics в выводе CSV
- new (0.1.2) - используется дата в формате ГОСТ/ISO "yyyy-mm-ddThh:mm:ss" для всего (для GPX всегда), для старого формата "yyyy.mm.dd hh:mm:ss" используйте --iso-date=no
- new (0.1.1) - добавлен Garmin Stress Index (вариация Индекса Баевского)

# English

Converter Garmin .FIT files to GPX, CVS, HR, HRV, SpO2, Stress. Version 0.1.4

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
                --merge         | -m    merge all output files in one file
                         Attention! the data in the merged file may not be in chronological order! (see issue #21)
                --iso-date=[yes|y|no|n] use for CSV date in GOST ISO 8601 format (by default 'yes')
                --hr-only       | -hr   the output file will be in CSV format, content: heart rate and time only
                --monitor-hr    | -mh   parsing heart rate monitoring files (not training), the output file will be in CSV format
                --hrv           | -vr   parsing training files and writing R-R intervals to CSV for variability analysis
                --hrv-filter    | -vf   same as --hrv, but threshold filter is used to eliminate spikes
                        --filter=n      where: n - threshold filter value (for -f) in % from 1 to 99 (default 35)

                --monitor-oxy           | -spo  parsing monitoring files and writing SpO2 oxygenation values in CSV
                --monitor-stress        | -si   parsing monitoring files and writing Stress values in CSV
                        fields in CSV: Date time; Garmin Stress Index; Body Battery; delta; unknown

                --no-dialog     | -nd   do not display the file selection dialog
                --save-empty    | -se   save files without coordinates (empty tracks)
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

# Features after 0.1.0 release

- 0.1.4 - options and short options chenged! See help
- new (0.1.4) -  merge all output in one file with --merge (data in the merged file may not be in chronological order!)
- new (0.1.4) - fix Bryton Rider data (holes and null values), tested on 310 model
- new (0.1.4) - [internal] interface fix() to repair bad data
- new (0.1.3) - Garmin Running Dinamics added in CSV output
- new (0.1.2) - now use ISO date format "yyyy-mm-ddThh:mm:ss" for all (in GPX allways), for use old date format "yyyy.mm.dd hh:mm:ss" use --iso-date=no
- new (0.1.1) - Garmin Stress Index (variation of Baevsky Index) added
