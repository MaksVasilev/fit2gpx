import com.garmin.fit.*;

public class FitTools {

    public static String BrytonProduct(int brytonProduct) {
        switch (brytonProduct) {
            case 1505: return "Rider 310";
            case 1508: return "Rider 530";
            default: return "Device ID: " + brytonProduct;
        }
    }
    
    public static String productById(int garminProduct) {
        switch (garminProduct) {
            case GarminProduct.HRM1:    return "HRM1";
            case GarminProduct.ALF04:   return "ALF04";
            case GarminProduct.AMX:     return "AMX";
            case GarminProduct.ANDROID_ANTPLUS_PLUGIN:  return "Android ANT+ Plugin";
            case GarminProduct.AXB01:   return "AXB01";
            case GarminProduct.AXB02:   return "AXB02";
            case GarminProduct.AXH01:   return "AXH01 HRM chipset";
            case GarminProduct.BCM:     return "датчик каденса (вело) BCM ANT+";
            case GarminProduct.BSM:     return "датчик скорости (вело) BSM ANT+";
            case GarminProduct.CHIRP:   return "CHIRP";
            case GarminProduct.CONNECT: return "сайт Garmin Connect";
            case GarminProduct.DSI_ALF01:   return "DSI_ALF01";
            case GarminProduct.DSI_ALF02:   return "DSI_ALF02";
            case GarminProduct.EDGE1000:    return "Edge 1000";
            case GarminProduct.EDGE200:     return "Edge 200";
            case GarminProduct.EDGE200_TAIWAN:  return "Edge 200 (Тайвань)";
            case GarminProduct.EDGE500:     return "Edge 500";
            case GarminProduct.EDGE500_CHINA:   return "Edge 500 (Китай)";
            case GarminProduct.EDGE500_JAPAN:   return "Edge 500 (Япония)";
            case GarminProduct.EDGE500_KOREA:   return "Edge 500 (Корея)";
            case GarminProduct.EDGE500_TAIWAN:  return "Edge 500 (Тайвань)";
            case GarminProduct.EDGE510:     return "Edge 510";
            case GarminProduct.EDGE510_ASIA:    return "Edge 510 (Азия)";
            case GarminProduct.EDGE510_JAPAN:   return "Edge 510 (Япония)";
            case GarminProduct.EDGE510_KOREA:   return "Edge 510 (Корея)";
            case GarminProduct.EDGE800:     return "Edge 800";
            case GarminProduct.EDGE800_CHINA:   return "Edge 800 (Китай)";
            case GarminProduct.EDGE800_JAPAN:   return "Edge 800 (Япония)";
            case GarminProduct.EDGE800_KOREA:   return "Edge 800 (Корея)";
            case GarminProduct.EDGE800_TAIWAN:  return "Edge 800 (Тайвань)";
            case GarminProduct.EDGE810:     return "Edge 810";
            case GarminProduct.EDGE810_CHINA:   return "Edge 810 (Китай)";
            case GarminProduct.EDGE810_JAPAN:   return "Edge 810 (Япония)";
            case GarminProduct.EDGE810_TAIWAN:  return "Edge 810 (Тайвань)";
            case GarminProduct.EDGE_REMOTE:     return "Edge Remote";
            case GarminProduct.EDGE_TOURING:    return "Edge Touring/Touring+";
            case GarminProduct.EPIX:    return "Epix";
            case GarminProduct.FENIX:   return "Fenix";
            case GarminProduct.FENIX2:  return "Fenix2";
            case GarminProduct.FR10:    return "Forerunner 10";
            case GarminProduct.FR10_JAPAN:  return "Forerunner 10 (Япония)";
            case GarminProduct.FR110:   return "Forerunner 110";
            case GarminProduct.FR110_JAPAN: return "Forerunner 110 (Япония)";
            case GarminProduct.FR15:    return "Forerunner 10";
            case GarminProduct.FR210_JAPAN: return "Forerunner 210 (Япония)";
            case GarminProduct.FR220:   return "Forerunner 220";
            case GarminProduct.FR220_CHINA: return "Forerunner 220 (Китай)";
            case GarminProduct.FR220_JAPAN: return "Forerunner 220 (Япония)";
            case GarminProduct.FR301_CHINA: return "Forerunner 301 (Китай)";
            case GarminProduct.FR301_JAPAN: return "Forerunner 301 (Япония)";
            case GarminProduct.FR301_KOREA: return "Forerunner 301 (Корея)";
            case GarminProduct.FR301_TAIWAN:    return "Forerunner 301 (Тайвань)";
            case GarminProduct.FR310XT:     return "Forerunner 310XT";
            case GarminProduct.FR310XT_4T:  return "Forerunner 310XT-4T";
            case GarminProduct.FR405:       return "Forerunner 405";
            case GarminProduct.FR405_JAPAN: return "Forerunner 405 (Япония)";
            case GarminProduct.FR50:        return "Forerunner 50";
            case GarminProduct.FR60:        return "Forerunner 60";
            case GarminProduct.FR610:       return "Forerunner 610";
            case GarminProduct.FR610_JAPAN: return "Forerunner 610 (Япония)";
            case GarminProduct.FR620:       return "Forerunner 620";
            case GarminProduct.FR620_CHINA: return "Forerunner 620 (Китай)";
            case GarminProduct.FR620_JAPAN: return "Forerunner 620 (Япония)";
            case GarminProduct.FR70:        return "Forerunner 60";
            case GarminProduct.FR910XT:     return "Forerunner 910XT";
            case GarminProduct.FR910XT_CHINA:   return "Forerunner 910XT (Китай)";
            case GarminProduct.FR910XT_JAPAN:   return "Forerunner 910XT (Япония)";
            case GarminProduct.FR910XT_KOREA:   return "Forerunner 910XT (Корея)";
            case GarminProduct.FR920XT:     return "Forerunner 910XT";
            case GarminProduct.HRM2SS:  return "нагрудный датчик пульса HRM2-SS";
            case GarminProduct.HRM3SS:  return "нагрудный датчик пульса HRM3-SS";
            case GarminProduct.HRM_RUN: return "нагрудный датчик пульса для бега HRM-Run";
            case GarminProduct.HRM_RUN_SINGLE_BYTE_PRODUCT_ID: return "нагрудный датчик пульса HRM-Run ANT+";
            case GarminProduct.SDM4:    return "датчик бега SDM4";
            case GarminProduct.SWIM:    return "Swim";
            case GarminProduct.TEMPE:   return "датчик температуры Tempe";
            case GarminProduct.TRAINING_CENTER: return "ПО Training Center";
            case GarminProduct.VECTOR_CP:   return "измеритель мощности Vector (контктные педали)";
            case GarminProduct.VECTOR_SS:   return "измеритель мощности Vector (односторонний датчик)";
            case GarminProduct.VIRB_ELITE:  return "камера Virb Elite";
            case GarminProduct.VIRB_REMOTE: return "пульт д/у Virb Remote";
            case GarminProduct.VIVO_FIT:    return "Vivo Fit";
            case GarminProduct.VIVO_FIT2:   return "Vivo Fit2";
            case GarminProduct.VIVO_KI:     return "Vivo KI";
            case GarminProduct.VIVO_SMART:  return "Vivo Smart";
         }
        return "неизвестное устройство";
    }
    
    public static String deviceByType(int deviceType) {
        switch (deviceType) {
            case AntplusDeviceType.ANTFS:           return "ANTFS";
            case AntplusDeviceType.BIKE_CADENCE:    return "датчик каденса (вело)";
            case AntplusDeviceType.BIKE_POWER:      return "измеритель мощности (вело)";
            case AntplusDeviceType.BIKE_SPEED:      return "датчик скорости (вело)";
            case AntplusDeviceType.BIKE_SPEED_CADENCE:  return "датчик скорость+каденс (вело)";
            case AntplusDeviceType.BLOOD_PRESSURE:  return "измеритель кровяного давления";
            case AntplusDeviceType.CONTROL:         return "CONTROL";
            case AntplusDeviceType.ENV_SENSOR:      return "датчик окружающей среды";
            case AntplusDeviceType.ENVIRONMENT_SENSOR_LEGACY:   return "датчик окружающей среды (старый)";
            case AntplusDeviceType.FITNESS_EQUIPMENT:   return "тренировочное оборудование";
            case AntplusDeviceType.GEOCACHE_NODE:   return "точка геокешинга";
            case AntplusDeviceType.HEART_RATE:      return "датчик пульса";
            case AntplusDeviceType.LIGHT_ELECTRIC_VEHICLE:  return "световое оборудование транспорта";
            case AntplusDeviceType.MULTI_SPORT_SPEED_DISTANCE:  return "датчик скорость/дистанция (мультиспорт)";
            case AntplusDeviceType.RACQUET: return "Ракетка";
            case AntplusDeviceType.STRIDE_SPEED_DISTANCE:   return "датчик скорость/дистанция (бег)";
            case AntplusDeviceType.WEIGHT_SCALE:    return "весы";
        }
        return "неизвестный тип";
    }
    
    public static String manufacturerById(int manufacturer) {
        switch (manufacturer) {
            case Manufacturer._1PARTCARBON: return "1partCarbon";
            case Manufacturer._4IIIIS:      return "4iiii";
            case Manufacturer.A_AND_D:      return "A&D";
            case Manufacturer.ACE_SENSOR:   return  "Ace Sensor";
            case Manufacturer.ACORN_PROJECTS_APS:   return "Acorn Projects ApS";
            case Manufacturer.ACTIGRAPHCORP:    return "ActiGraph Corp";
            case Manufacturer.ALATECH_TECHNOLOGY_LTD:   return "Alatech Technology Ltd";
            case Manufacturer.ARCHINOETICS: return "Archinoetics";
            case Manufacturer.BEURER:   return "Beurer";
            case Manufacturer.BF1SYSTEMS:   return "bf1systems Ltd";
            case Manufacturer.BKOOL:    return "Bkool";
            case Manufacturer.BONTRAGER:    return "Bontrager";
            case Manufacturer.BREAKAWAY:    return "Breakaway";
            case Manufacturer.BRIM_BROTHERS:    return "Brim Brothers";
            case Manufacturer.CARDIOSPORT:  return "Cardiosport";
            case Manufacturer.CATEYE:       return "CatEye";
            case Manufacturer.CICLOSPORT:   return "Ciclosport";
            case Manufacturer.CITIZEN_SYSTEMS:  return "Citizen Systems";
            case Manufacturer.CLEAN_MOBILE: return "Clean Mobile";
            case Manufacturer.CONCEPT2: return "Concept2";
            case Manufacturer.DAYTON:   return "Dayton";
            case Manufacturer.DEVELOPMENT:  return "Development";
            case Manufacturer.DEXCOM:   return "Dexcom";
            case Manufacturer.DIRECTION_TECHNOLOGY: return "Direction Technology Co";
            case Manufacturer.DK_CITY:  return "DK City";
            case Manufacturer.DYNASTREAM: return "Dynastream Innovations Inc";
            case Manufacturer.DYNASTREAM_OEM:   return "Dynastream Innovations Inc (OEM)";
            case Manufacturer.ECHOWELL: return "Echowell";
            case Manufacturer.ELITE:    return "Elite";
            case Manufacturer.GARMIN:   return "Garmin Ltd";
            case Manufacturer.GARMIN_FR405_ANTFS:   return "Garmin internal Forerunner 405 ANTFS";
            case Manufacturer.GEONAUTE: return "Geonaute";
            case Manufacturer.GPULSE:   return "G.Pulse International Co";
            case Manufacturer.HEALTHANDLIFE:    return "Health & Life";
            case Manufacturer.HMM:  return "Hyundai Merchant Marine Co";
            case Manufacturer.HOLUX:    return "Holux Technology Inc";
            case Manufacturer.IBIKE:    return "iBike";
            case Manufacturer.ID_BIKE:  return "IDbike";
            case Manufacturer.IDT:  return "Integrated Device Technology";
            case Manufacturer.IFOR_POWELL:  return "Ifor Powell";
            case Manufacturer.INSIDE_RIDE_TECHNOLOGIES: return "Inside Ride Technologies";
            case Manufacturer.LEMOND_FITNESS:   return "LeMond Fitness";
            case Manufacturer.LEZYNE:   return "Lezyne";
            case Manufacturer.LIFEBEAM: return "LifeBEAM";
            case Manufacturer.MAGELLAN: return "Magellan Navigation";
            case Manufacturer.MAGTONIC: return "Magtonic";
            case Manufacturer.MAGURA:   return "MAGURA, Gustav Magenwirth GmbH & Co";
            case Manufacturer.MAXWELL_GUIDER:   return "Maxwell Guider Technology Co";
            case Manufacturer.METALOGICS:   return "MetaLogics Corp";
            case Manufacturer.METRIGEAR:    return "MetriGear";
            case Manufacturer.MIO_TECHNOLOGY_EUROPE:    return "Mio Technology Europe";
            case Manufacturer.MOXY: return "Moxy Monitor";
            case Manufacturer.NAUTILUS: return "Nautilus";
            case Manufacturer.NIELSEN_KELLERMAN:    return "Nielsen-Kellerman Co";
            case Manufacturer.NORTH_POLE_ENGINEERING:   return "North Pole Engineering Inc";
            case Manufacturer.OCTANE_FITNESS:   return "Octane Fitness";
            case Manufacturer.ONE_GIANT_LEAP:   return "One Giant Leap";
            case Manufacturer.OSYNCE:   return "O-synce";
            case Manufacturer.PEAKSWARE:    return "Peaksware LLC";
            case Manufacturer.PEDAL_BRAIN:  return "Pedal Brain";
            case Manufacturer.PERCEPTION_DIGITAL:   return "Perception Digital Ltd";
            case Manufacturer.PERIPEDAL:    return "PeriPedal";
            case Manufacturer.PHYSICAL_ENTERPRISES: return "Physical Enterprise";
            case Manufacturer.PIONEER:  return "Pioneer Corp";
            case Manufacturer.POWERBAHN:    return "POWERbahn";
            case Manufacturer.QUARQ:    return "Quarq";
            case Manufacturer.ROTOR:    return "Rotor Bike Components";
            case Manufacturer.SARIS:    return "Saris Cycling Group";
            case Manufacturer.SAXONAR:  return "Saxonar GmbH";
            case Manufacturer.SCOSCHE:  return "Scosche";
            case Manufacturer.SCRIBE_LABS:  return "runScribe Scribe Labs";
            case Manufacturer.SEIKO_EPSON:  return "Seiko Epson Corp";
            case Manufacturer.SEIKO_EPSON_OEM:  return "Seiko Epson Corp (OEM)";
            case Manufacturer.SIGMASPORT:   return "Sigma Sport";
            case Manufacturer.SPANTEC:  return "Spantec";
            case Manufacturer.SPARK_HK: return "Spark Technology Ltd";
            case Manufacturer.SPECIALIZED:  return "Specialized Bicycle Components";
            case Manufacturer.SRM:  return "SRM GmbH";
            case Manufacturer.STAGES_CYCLING:   return "Stages Cycling";
            case Manufacturer.STAR_TRAC:    return "Star Trac";
            case Manufacturer.SUUNTO:   return "Suunto";
            case Manufacturer.TACX: return "Tacx International";
            case Manufacturer.TANITA:   return "Tanita Corp";
            case Manufacturer.THE_HURT_BOX: return "The Hurt Box";
            case Manufacturer.THITA_ELEKTRONIK: return "Thita Elektronik ApS";
            case Manufacturer.TIMEX:    return "Timex Group";
            case Manufacturer.TOMTOM:   return "TomTom International";
            case Manufacturer.WAHOO_FITNESS:    return "Wahoo Fitness";
            case Manufacturer.WATTBIKE: return "Wattbike Ltd";
            case Manufacturer.WELLGO:   return "Wellgo Pedal's Corp";
            case Manufacturer.WOODWAY:  return "Woodway";
            case Manufacturer.WTEK: return "WTEk";
            case Manufacturer.XELIC:    return "Xelic Inc";
            case Manufacturer.XPLOVA:   return "Xplova Inc";
            case Manufacturer.ZEPHYR:   return "Zephyr Technology";
            case Manufacturer.ZWIFT:    return "Zwift";
         }
        return "неизвестный производитель";
    }
    
    public static String sourceTypeById(SourceType sourcetype) {
        switch (sourcetype) {
            case ANT:    return "ANT";
            case ANTPLUS:   return "ANT+";
            case BLUETOOTH: return "Bluetooth";
            case BLUETOOTH_LOW_ENERGY: return "Bluetooth Smart";
            case WIFI:  return "Wi-Fi";
            case LOCAL: return "локальный";
            case INVALID:   return "неизвестный тип";
         }
        return "неизвестный тип";
    }
    
    public static String antNetworkById(AntNetwork antNetwork) {
        switch (antNetwork) {
            case PUBLIC:    return "открытая сеть";
            case ANTPLUS:   return "сеть ANT+";
            case ANTFS:     return "сеть ANT-FS";
            case PRIVATE:   return "закрытая сеть";
            case INVALID:   return "неизвестная сеть";
        }
        return "неизвестная сеть";
    }
    
    public static String fileTypeById(com.garmin.fit.File file) {
        switch (file) {
            case DEVICE:    return "Устройство";
            case SETTINGS:  return "Настройки";
            case SPORT:     return "Спорт";
            case ACTIVITY:  return "Занятие";
            case WORKOUT:   return "Тренировка";
            case COURSE:    return "Дистанция";
            case SCHEDULES: return "План";
            case WEIGHT:    return "Вес";
            case TOTALS:    return "Сводка";
            case GOALS:     return "Цель";
            case BLOOD_PRESSURE:    return "Кровяное давление";
            case MONITORING_A:      return "Мониторинг (А)";    //?
            case ACTIVITY_SUMMARY:  return "Сводка занятия";
            case MONITORING_DAILY:  return "Суточный мониторинг";
            case MONITORING_B:      return "Мониторинг (B)";    //?
            case MFG_RANGE_MIN:     return "MFG RANGE MIN"; //?
            case MFG_RANGE_MAX:     return "MFG RANGE MAX"; //?
            case INVALID:   return "неизвестный тип файла";
        }
        return "неизвестный тип файла";
    }
    
    public static String genderById(Gender gender) {
        switch (gender) {
            case FEMALE:    return "женщина";
            case MALE:      return "мужчина";
            case INVALID:   return "оно не определилось";
        }
        return "оно не определилось";
    }
    
    public static String batteryStatusById(short batteryStatus) {
        switch (batteryStatus) {
            case BatteryStatus.NEW:     return "новая";
            case BatteryStatus.GOOD:    return "хорошая";
            case BatteryStatus.OK:      return "нормальная";
            case BatteryStatus.LOW:     return "низкий уровень";
            case BatteryStatus.CRITICAL:    return "критический уровень!";
        }
        return "неизвестный статус";
    }
    
    public static String unitsById(DisplayMeasure units) {
        switch (units) {
            case METRIC:    return "метрические";
            case STATUTE:   return "устаревшие (дюймы, вершки)";
            case INVALID:   return "неизвестный тип";
        }
        return "неизвестный тип";
    }
    
    public static String coordinateUnitById(DisplayPosition units) {
        switch (units) {

            case DEGREE:                return "Градусы";
            case DEGREE_MINUTE:         return "Градусы, Минуты";
            case DEGREE_MINUTE_SECOND:  return "Градусы, Минуты, Секунды";
            case AUSTRIAN_GRID:         return "Австралийская сетка";
            case BRITISH_GRID:          return "Британская сетка";
            case DUTCH_GRID:            return "Нидерландская сетка";
            case HUNGARIAN_GRID:        return "Венгерская сетка";
            case FINNISH_GRID:          return "Финская сетка";
            case GERMAN_GRID:           return "Германская сетка";
            case ICELANDIC_GRID:        return "Исландская сетка";
            case INDONESIAN_EQUATORIAL: return "Индонезийская экваториальная сетка";
            case INDONESIAN_IRIAN:      return "Индонезийская западная (Irian) сетка";
            case INDONESIAN_SOUTHERN:   return "Индонезийская южная сетка";
            case INDIA_ZONE_0:          return "Индийская сетка, зона 0";
            case INDIA_ZONE_IA:         return "Индийская сетка, зона IA";
            case INDIA_ZONE_IB:         return "Индийская сетка, зона IB";
            case INDIA_ZONE_IIA:        return "Индийская сетка, зона IIA";
            case INDIA_ZONE_IIB:        return "Индийская сетка, зона IIB";
            case INDIA_ZONE_IIIA:       return "Индийская сетка, зона IIIA";
            case INDIA_ZONE_IIIB:       return "Индийская сетка, зона IIIB";
            case INDIA_ZONE_IVA:        return "Индийская сетка, зона IVA";
            case INDIA_ZONE_IVB:        return "Индийская сетка, зона IVB";
            case IRISH_TRANSVERSE:      return "Ирландская поперечная сетка";
            case IRISH_GRID:            return "Ирландская сетка";
            case LORAN:                 return "Лоран";
            case MAIDENHEAD_GRID:       return "QTH-локатор";
            case MGRS_GRID:             return "Военная сетка НАТО";
            case NEW_ZEALAND_GRID:      return "Новозеландская сетка";
            case NEW_ZEALAND_TRANSVERSE:return "Новозеландская поперечная сетка";
            case QATAR_GRID:            return "Катарская сетка";
            case MODIFIED_SWEDISH_GRID: return "Модифицированная Шведская сетка";
            case SWEDISH_GRID:          return "Шведская сетка";
            case SOUTH_AFRICAN_GRID:    return "Южноафриканская сетка";
            case SWISS_GRID:            return "Швейцарская сетка";
            case TAIWAN_GRID:           return "Тайваньская сетка";
            case UNITED_STATES_GRID:    return "США сетка";
            case UTM_UPS_GRID:          return "Полярная Стереографическая сетка";
            case WEST_MALAYAN:          return "Малазийская западная сетка";
            case BORNEO_RSO:            return "сетка Борнео RSO";
            case ESTONIAN_GRID:         return "Эстонская сетка";
            case LATVIAN_GRID:          return "Латышская сетка";
            case SWEDISH_REF_99_GRID:   return "Шведская-REF-99 сетка";
            case INVALID:               return "координатная сетка не определена";
        }
        return "координатная сетка не определена";
    }
    
    public static String activityClassById(ActivityClass user) {
        switch (user) {
            case LEVEL:     return "LEVEL";     //?
            case LEVEL_MAX: return "LEVEL_MAX"; //?
            case ATHLETE:   return "Атлет";
            case INVALID:   return "не определён";
        }
        return "не определён";
    }
    
    public static String languageById(Language language) {
        switch (language) {

            case ENGLISH:   return "английский";
            case FRENCH:    return "французкий";
            case ITALIAN:   return "итальянский";
            case GERMAN:    return "немецкий";
            case SPANISH:   return "испанский";
            case CROATIAN:  return "хорватский";
            case CZECH:     return "чешский";
            case DANISH:    return "датский";
            case DUTCH:     return "нидерландский";
            case FINNISH:   return "финский";
            case GREEK:     return "греческий";
            case HUNGARIAN: return "венгерский";
            case NORWEGIAN: return "норвежский";
            case POLISH:    return "польский";
            case PORTUGUESE:return "португальский";
            case SLOVAKIAN: return "словацкий";
            case SLOVENIAN: return "словенский";
            case SWEDISH:   return "шведский";
            case RUSSIAN:   return "русский";
            case TURKISH:   return "турецкий";
            case LATVIAN:   return "латышский";
            case UKRAINIAN: return "украинский";
            case ARABIC:    return "арабский";
            case FARSI:     return "фарси";
            case BULGARIAN: return "болгарский";
            case ROMANIAN:  return "румынский";
            case CUSTOM:    return "произвольный";
            case INVALID:   return "не определён";
        }
        return "не определён";
    }
    
    public static String displayHeartById(DisplayHeart heart) {
        switch (heart) {

            case BPM:       return "установлены в уд/мин";
            case MAX:       return "расчитаны от максимального пульса";
            case RESERVE:   return "расчитаны от резерва ЧСС";
            case INVALID:   return "не определены";
        }
        return "не определены";
    }
    
    public static String displayPowerById(DisplayPower power) {
        switch (power) {

            case WATTS:         return "установлены в Ваттах";
            case PERCENT_FTP:   return "в процентах от FTP";
            case INVALID:       return "не определены";
        }
        return "не определены";
    }
}
