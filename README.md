# fit2gpx

Конвертер файлов Garmin .FIT в GPX, CVS, HR, HRV, SpO2, Stress, SQLite. Версия 0.1.9

© Maks Vasilev, 2015-2020, http://velo100.ru/garmin-fit-to-gpx

FIT Software Development Kit (SDK), http://www.thisisant.com

Информация по использованию:

        java -jar fit2gpx.jar --help

Консольный режим:

        java -jar fit2gpx.jar [параметры] <файл Garmin .FIT> [[<файл Garmin .FIT>] …]

Графический диалоговый режим:

        java -jar fit2gpx.jar [параметры]

параметры:

        --statistic             | -s    вывод итоговой статистики в консоль
        --gpx                   | -g    Режим по умолчанию. Выходной файл: трек в формате GPX
        --csv                   | -c    выходной файл будет в формате CSV
        --merge                 | -m    объединить все выходные файлы в один
                 Внимание! в объединённом файле данные могут быть не в хронологическом порядке! (см. issue #21)

        --iso-date=[yes|y|no|n] использовать для CSV формата дату в формате ГОСТ ИСО 8601-2001 (ISO 8601) (по умолчанию 'yes')
        --hr-only               | -hr   выходной файл будет в формате CSV, содержимое: только ЧСС и время
        --hrv                   | -vr   разбор файлов тренировки и запись в CSV интервалов R-R для анализа вариабельности
        --hrv-filter            | -vf   тоже самое, что и --hrv, но используется пороговый фильтр для устранения всплесков
                --filter=n      где: n - уставка порогового фильтра (для -vf) в % от 1 до 99 (по умолчанию 30)
                --hrv-mark-filter       записывать в выходной файл все интервалы, только отмечать плохие в поле "fileter"

        Файлы мониторинга (не тренировок):
        --monitor-all           | -ma   синоним: --monitor-hr --monitor-oxy --monitor-stress
        --monitor-hr            | -mh   разбор файлов мониторинга пульса (не тренировки), выходной файл будет в формате CSV
        --monitor-oxy           | -mo   разбор файлов мониторинга и запись в CSV значений оксигенации SpO2
        --monitor-stress        | -ms   разбор файлов мониторинга и запись в CSV значений уровня стресса
                поля в CSV: Дата время; Индекс Стресса Garmin; Уровень батареи тела; дельта; неизвестно

        --db-sqlite             | -dbs  сохранять результат не в файл, а в базу данных SQLite
                --db-connect=база       строка подключения к базе данных. Для SQLite "база" - путь к файлу
                        базы данных, если не указан, то по умолчанию "fit_db.sqlite3" в директории запуска программы
                --db-prefix=префикс     префикс имён таблиц/схемы для сохранения в базе измерений нескольких человек,
                         если не задан, по умолчанию в SQLite данные будут сохраняться в таблицы с префиксом "_no_person"
                --tags=                 тэги для файла или группы файлов, при сохранении в базу, для поиска и обработки

        --save-empty            | -se   сохранять файлы без координат (пустые треки)
        --full-dump                     сделать полный дамп записей файла в тестовый файл 

Конвертер имеет два режима работы: консольный и графический. При запуске без указания файлов для конвертации
запускается диалоговое окно выбора файлов для конвертации. Если при запуске в качестве аргумента передать имя файла, то
происходит конвертация без запуска диалоговых окон. В случае успешной конвертации никакой информации в консоль не возвращается.
Консольный режим работы можно использовать в различных скриптах для автоматизации.

Конвертер поддерживает режим пакетной обработки файлов, для этого в консольном режиме укажите имена всех файлов
в качестве аргументов программы. В диалоговом режиме просто выберите несколько файлов в диалоге выбора.

Дамп всех заголовков файла (отладочный режим):

        java -cp fit2gpx.jar DebugDecode <файл Garmin .FIT>

Минимальная версия Java - 11.0

# Примеры использования:

RU: https://github.com/MaksVasilev/fit2gpx/blob/master/Examples_RU.md

# Актуальный релиз:

https://github.com/MaksVasilev/fit2gpx/releases/latest

# текущая бинарная сборка (возможно не стабильная):

https://github.com/MaksVasilev/fit2gpx/raw/master/out/jar/fit2gpx.jar

# Возможности добавленные после релиза 0.1.5

- 0.1.9 - обработка всех заданных режимов, можно указать несколько параметров для работы одновременно
- 0.1.8 - запись измерений HRV (RR, HR, фильтр) в базу SQLite
- 0.1.8 - запись параметров пульса из тренировок (HR-only) в SQLite
- 0.1.7 - запись параметров мониторинга (HR, SpO2, GSI) в базу данных SQLite
- 0.1.6 - для HRV с фильтром запись всех значений R-R и отметка плохих в поле filter

# English

Converter Garmin .FIT files to GPX, CVS, HR, HRV, SpO2, Stress, SQLite. Version 0.1.9

© Maks Vasilev, 2015-2020, http://velo100.ru/garmin-fit-to-gpx

FIT Software Development Kit (SDK), http://www.thisisant.com

Usage:

        java -jar fit2gpx.jar --help

Console mode:

        java -jar fit2gpx.jar [options] <file Garmin .FIT> [[<file Garmin .FIT>] …]

Graphic dialog mode:

        java -jar fit2gpx.jar [options]

options:

        --statistic             | -s    output of final statistics to the console
        --gpx                   | -g    Default mode. Output file: GPX track
        --csv                   | -c    the output file will be in CSV format
        --merge                 | -m    merge all output files in one file
                 Attention! the data in the merged file may not be in chronological order! (see issue #21)

        --iso-date=[yes|y|no|n] use for CSV date in GOST ISO 8601 format (by default 'yes')
        --hr-only               | -hr   the output file will be in CSV format, content: heart rate and time only
        --hrv                   | -vr   parsing training files and writing R-R intervals to CSV for variability analysis
        --hrv-filter            | -vf   same as --hrv, but threshold filter is used to eliminate spikes
                --filter=n      where: n - threshold filter value (for -vf) in % from 1 to 99 (default 30)
                --hrv-mark-filter       write all RR intervals to out, but mark bad in "field" field

        Monitoring files (activity tracking, not a activities):
        --monitor-all           | -ma   alias for: --monitor-hr --monitor-oxy --monitor-stress
        --monitor-hr            | -mh   parsing heart rate monitoring files (not training), the output file will be in CSV format
        --monitor-oxy           | -mo   parsing monitoring files and writing SpO2 oxygenation values in CSV
        --monitor-stress        | -ms   parsing monitoring files and writing Stress values in CSV
                fields in CSV: Date time; Garmin Stress Index; Body Battery; delta; unknown

        --db-sqlite             | -dbs  save the result to a SQLite database instead of a file
                --db-connect=base       the connection string to the database. For SQLite, "base" is the path to the database
                        file, if omitted, the default is "fit_db.sqlite3" in the program launch directory
                --db-prefix=prefix      prefix names of the tables/schema to store in the database the dimensions of a few people,
                        if omitted, by default, data in SQLite will be saved in tables with the prefix "_no_person"
                --tags=                 tags for a file or group of files, when saving to the database, for search and processing

        --save-empty            | -se   save files without coordinates (empty tracks)
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

# Examples of usage:

RU: https://github.com/MaksVasilev/fit2gpx/blob/master/Examples_EN.md

# Actual release:

https://github.com/MaksVasilev/fit2gpx/releases/latest

# Last binary (possible unstable):

https://github.com/MaksVasilev/fit2gpx/raw/master/out/jar/fit2gpx.jar

# Features after 0.1.5 release

- 0.1.9 - processing all set modes, you can specify several parameters to work
- 0.1.8 - store HRV measurements (RR, HR, filter) to SQLite database
- 0.1.8 - store heart rate parameters from training sessions (HR-only) in SQLite
- 0.1.7 - store monitoring parameters (HR, SpO2, GSI) to the SQLite database
- 0.1.6 - for HRV with a filter, record all R-R values and mark the bad ones in the filter field