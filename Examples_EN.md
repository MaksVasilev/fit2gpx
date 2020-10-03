# Examples

## FIT to GPX

    java -jar fit2gpx.jar AAA.fit

_result:_ GPX file: **AAA.fit.gpx**

### 2 or more FIT files in one GPX

    java -jar fit2gpx.jar --merge AAAA.fit BBBB.fit

_result:_ GPX file: **AAAA.fit.merged.gpx**

### multiple FIT files to GPX with statistics

    java -jar fit2gpx.jar --statistic AAA.fit BBB.fit CCCC.fit DDDDD.fit

_result:_ **3** GPX files: **AAA.fit.gpx, BBB.fit.gpx, CCCC.fit.gpx**

additional output to the console:

    Successfully processed files: 3
        /home/test/520+/AAA.fit
        /home/test/520+/BBB.fit
        /home/test/520+/CCCC.fit

    Files without tracks: 1
        /home/test/520+/DDDDD.fit

    Files with errors: 0

## FIT to CVS

    java -jar fit2gpx.jar --csv AAA.fit

_result:_ СSV table file: **AAA.fit.csv**

> 1. time;duration;position_lat;position_long;gps_accuracy;altitude;enhanced_altitude...
> 2. 2020-03-21T19:02:37;00:00:00;44.42104107700288;34.051622953265905;;78.20000000...
> 3. 2020-03-21T19:02:38;00:00:01;44.421038730069995;34.05161842703819;;78.20000...
> 4. ...
>
### FIT to CSV with datetime in non-ISO/GOST format

    java -jar fit2gpx.jar --csv --iso-date=no AAA.fit

_result:_ СSV table file: **AAA.fit.csv**

> 1. time;duration;position_lat;position_long;gps_accuracy;altitude;enhanced_altitude...
> 2. **2020.03.21 19:02:37**;00:00:00;44.42104107700288;34.051622953265905;;78.20000000...
> 3. **2020.03.21 19:02:38**;00:00:01;44.421038730069995;34.05161842703819;;78.20000...
> 4. ...

### FIT to CSV - save only the heart rate from activities

    java -jar fit2gpx.jar --hr-only AAA.fit

_result:_ СSV table file: **AAA.fit.HR.csv**

fields: data time, heart rate, duration in activity

> 1. 2020-03-21T19:02:37;49;00:00:00
> 2. 2020-03-21T19:02:38;49;00:00:01
> 3. 2020-03-21T19:02:39;49;00:00:02
> 4. ...

## FIT the monitoring file, do not exercise (activity tracking)

 
### FIT to CSV with heart rate

    java -jar fit2gpx.jar --monitor-hr XXXX.fit
    
_result:_ СSV table file: **XXXX.fit.monitor-HR.csv**

> 1. 2020-06-01T00:02:00;48
> 2. 2020-06-01T00:03:00;45
> 3. 2020-06-01T00:05:00;44
> 4. ...

The same is true for all monitoring parameters.

## FIT to file with recording intervals (R-R) for analyzing heart rate variability (HRV)

To save information about the R-R interval, R-R interval recording must be enabled on your device!

    java -jar fit2gpx.jar --hrv AAA.fit

_result:_ СSV table file: **AAA.fit.HRV.csv**

> 1. Timestampserial, time, RR, HR, filter
> 2. 2020-03-21T19:02:38.215,1.215,1.215,49.383
> 3. 2020-03-21T19:02:39.402,2.402,1.187,50.548
> 4. 2020-03-21T19:02:40.606,3.606,1.204,49.834
> 5. ...

The HRV.csv file uses a non-standard "," field separator for compatibility with the IBI CSV format!

### FIT to HRV with filtration

Currently, only the threshold filter is implemented. To save only filtered values, use the --hrv-filter parameter
instead of --hrv. you can also specify the filter threshold value as a percentage --filter=

To write all values to the file, including bad ones, but with a quality mark:

    java -jar fit2gpx.jar --hrv-filter --filter=10 --hrv-mark-filter AAA.fit

_result:_ СSV table file: **AAA.fit.HRV.csv**

> 1. 2020-03-21T19:08:23.637,346.637,0.76,78.947,0
> 2. 2020-03-21T19:08:24.402,347.402,0.765,78.431,0
> 3. 2020-03-21T19:08:25.263,348.263,0.861,69.686,**1**
> 4. 2020-03-21T19:08:26.136,349.136,0.873,68.729,**1**
> 5. ...

## FIT to the SQLite database

When saving to the SQLite database, all the same parameters apply, only the database selection parameters are added

### ЧСС (мониторинг) from FIT to SQLite

Saving heart rate (activity tracking) records to the my_activities.sqlite3 database file from user Abrahaam:

    java -jar fit2gpx.jar --db-sqlite --db-connect=/home/test/DB/my_activities.sqlite3 --db-prefix=Abrahaam --monitor-hr XXXX.fit

_result:_ the database file **/home/test/DB/my_activities.sqlite3** will be created if it was not present,
the heart rate data will be saved in the table **Abrahaam_HR_monitor**

### Heart rate from FIT activities to SQLITE

    java -jar fit2gpx.jar --db-sqlite --db-connect=/home/test/DB/my_activities.sqlite3 --db-prefix=Abrahaam --hr-only AAA.fit BBB.fit <...>

_result:_ the database file **/home/test/DB/my_activities.sqlite3** will be created if it was not present,
the heart rate data will be saved in the table  **Abrahaam_activities_HR_only**

### Process a large number of files and write them to the SQLite database

You can list any number of files on the command line. The file masks *.fit are not supported (yet).

You can not specify the file, and only specify the settings for the database connection and mode of operation, in this case,
 the start selection dialog files. You can select one, several, or all files (Ctrl+A) in the directory.

    java -jar fit2gpx.jar --db-sqlite --db-connect=/home/test/DB/my_activities.sqlite3 --db-prefix=Abrahaam --hr-only
    
### HRV (R-R) measurements FIT to SQLite

    java -jar fit2gpx.jar --hrv --db-sqlite --db-prefix=Abrahaam --tags=утром,стоя,лёжа AAA.fit

_result:_ the record of RR intervals is saved in the table **Abrahaam_HRV**, the service table **_hrv** stores information
about this record with the time of file creation, a unique number, the person for which the measurement was made, and tags.

 