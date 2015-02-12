////////////////////////////////////////////////////////////////////////////////
// Copyright 2015 Dynastream Innovations Inc.
// Copyright © 2015 by Maks Vasilev
//
////////////////////////////////////////////////////////////////////////////////


//package com.garmin.fit.examples;

import com.garmin.fit.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class DebugDecode {
    
  //  private static class Listener implements FileIdMesgListener, UserProfileMesgListener, DeviceInfoMesgListener, MonitoringMesgListener {

    private static class FileIdListener implements FileIdMesgListener {

            @Override
            public void onMesg(FileIdMesg mesg) {
                System.out.println("Информация о файле:");

                if (mesg.getType() != null) {
                    System.out.print("   Тип файла: ");
                    System.out.println(FitTools.fileTypeById(mesg.getType()));
                }
                if (mesg.getGarminProduct() != null) {
                    System.out.print("   Устройство Garmin: ");
                    System.out.println(FitTools.productById(mesg.getGarminProduct()) + " [" + mesg.getGarminProduct() + "]");

                }
                if (mesg.getTimeCreated() != null) {
                    System.out.print("   Время создания: ");
                    System.out.println(mesg.getTimeCreated());
                }
                if (mesg.getManufacturer() != null) {
                    System.out.print("   Производитель: ");
                    System.out.println(FitTools.manufacturerById(mesg.getManufacturer()) + " [" + mesg.getManufacturer() + "]");
                }

                if (mesg.getProduct() != null) {
                    System.out.print("   Устройство: ");
                    System.out.println(FitTools.productById(mesg.getProduct()) + " [" + mesg.getProduct() + "]");
                }

                if (mesg.getSerialNumber() != null) {
                    System.out.print("   Серийный номер: ");
                    System.out.println(mesg.getSerialNumber());
                }

                if (mesg.getNumber() != null) {
                    System.out.print("   Номер: ");
                    System.out.println(mesg.getNumber());
                }
            }
        }

    private static class UserProfileListener implements UserProfileMesgListener {

        @Override
        public void onMesg(UserProfileMesg mesg) {
            System.out.println("Профиль пользователя:");

            if ((mesg.getFriendlyName() != null)) {
                System.out.print("   Краткое имя: ");
                System.out.println(mesg.getFriendlyName());
            }
            if ((mesg.getLanguage() != null)) {
                System.out.print("   Язык: ");
                System.out.println(FitTools.languageById(mesg.getLanguage()));
            }
            if ((mesg.getActivityClass() != null)) {
                System.out.print("   Класс подготовки: ");
                System.out.println(FitTools.activityClassById(mesg.getActivityClass()));
            }
            if (mesg.getAge() != null) {
                System.out.print("   Возраст [лет]: ");
                System.out.println(mesg.getAge());
            }

            if (mesg.getGender() != null) {
                System.out.print("   Пол: ");
                System.out.println(FitTools.genderById(mesg.getGender()));
            }

            if (mesg.getWeight() != null) {
                System.out.print("   Вес [кг]: ");
                System.out.println(mesg.getWeight());
            }
            if ((mesg.getRestingHeartRate() != null)) {
                System.out.print("   Пульс покоя: ");
                System.out.println(mesg.getRestingHeartRate());
            }
            if ((mesg.getDefaultMaxHeartRate() != null)) {
                System.out.print("   Пульс максимальный: ");
                System.out.println(mesg.getDefaultMaxHeartRate());
            }
            if ((mesg.getDefaultMaxBikingHeartRate() != null)) {
                System.out.print("   Пульс максимальный (вело): ");
                System.out.println(mesg.getDefaultMaxBikingHeartRate());
            }
            if ((mesg.getDefaultMaxRunningHeartRate() != null)) {
                System.out.print("   Пульс максимальный (бег): ");
                System.out.println(mesg.getDefaultMaxRunningHeartRate());
            }
  /*        if ((mesg.getGlobalId() != null) ) {
              System.out.print("   GlobalId: ");
              System.out.println(mesg.getGlobalId().toString());
          }*/
            if ((mesg.getHeight() != null)) {
                System.out.print("   Рост [см]: ");
                System.out.println(mesg.getHeight() * 100);
            }
            if ((mesg.getHrSetting() != null)) {
                System.out.print("   Настройки зон пульса: ");
                System.out.println(FitTools.displayHeartById(mesg.getHrSetting()));
            }
            if ((mesg.getPowerSetting() != null)) {
                System.out.print("   Настройки зон мощности: ");
                System.out.println(FitTools.displayPowerById(mesg.getPowerSetting()));
            }
            if ((mesg.getLocalId() != null)) {
                System.out.print("   Локальный ID: ");
                System.out.println(mesg.getLocalId());
            }
            if ((mesg.getMessageIndex() != null)) {
                System.out.print("   MessageIndex: ");
                System.out.println(mesg.getMessageIndex());
            }
            if ((mesg.getNumGlobalId() != 0)) {
                System.out.print("   NumGlobalId(: ");
                System.out.println(mesg.getNumGlobalId());
            }
            if ((mesg.getPositionSetting() != null)) {
                System.out.print("   Настройки координат: ");
                System.out.println(FitTools.coordinateUnitById(mesg.getPositionSetting()));
            }
            if ((mesg.getDistSetting() != null)) {
                System.out.print("   Настройки расстояния: ");
                System.out.println(FitTools.unitsById(mesg.getDistSetting()));
            }
            if ((mesg.getElevSetting() != null)) {
                System.out.print("   Настройки высот: ");
                System.out.println(FitTools.unitsById(mesg.getElevSetting()));
            }
            if ((mesg.getHeightSetting() != null)) {
                System.out.print("   Настройки роста: ");
                System.out.println(FitTools.unitsById(mesg.getHeightSetting()));
            }
            if ((mesg.getSpeedSetting() != null)) {
                System.out.print("   Настройки скорости: ");
                System.out.println(FitTools.unitsById(mesg.getSpeedSetting()));
            }
            if ((mesg.getTemperatureSetting() != null)) {
                System.out.print("   Настройки температуры: ");
                System.out.println(FitTools.unitsById(mesg.getTemperatureSetting()));
            }
            if (mesg.getWeightSetting() != null) {
                System.out.print("   Настройки веса: ");
                System.out.println(FitTools.unitsById(mesg.getWeightSetting()));
            }
        }
    }

    private static class DeviceInfoListener implements DeviceInfoMesgListener {

        @Override
        public void onMesg(DeviceInfoMesg mesg) {
            System.out.println("Информация по устройству:");

            if (mesg.getTimestamp() != null) {
                System.out.print("   Время: ");
                System.out.println(mesg.getTimestamp());
            }
            if (mesg.getDeviceType() != null) {
                System.out.print("   Тип устройства: ");
                System.out.println(FitTools.deviceByType(mesg.getDeviceType()) + " [" + mesg.getDeviceType() + "]");
            }
            if (mesg.getProduct() != null) {
                System.out.print("   Устройство: ");
                System.out.println(FitTools.productById(mesg.getProduct()) + " [" + mesg.getProduct() + "]");
            }
            if (mesg.getManufacturer() != null) {
                System.out.print("   Производитель: ");
                System.out.println(FitTools.manufacturerById(mesg.getManufacturer()) + " [" + mesg.getManufacturer() + "]");
            }
            if (mesg.getSerialNumber() != null) {
                System.out.print("   Серийный номер: ");
                System.out.println(mesg.getSerialNumber());
            }
            if (mesg.getHardwareVersion() != null) {
                System.out.print("   Версия устройства: ");
                System.out.println(mesg.getHardwareVersion());
            }
            if (mesg.getSoftwareVersion() != null) {
                System.out.print("   Версия ПО: ");
                System.out.println(mesg.getSoftwareVersion());
            }
            if (mesg.getBatteryStatus() != null) {
                System.out.print("   Состояние батареи: ");
                System.out.println(FitTools.batteryStatusById(mesg.getBatteryStatus()));
            }
            if (mesg.getBatteryVoltage() != null) {
                System.out.print("   Напряжение батареи: ");
                System.out.println(mesg.getBatteryVoltage());
            }
            if (mesg.getDescriptor() != null) {
                System.out.print("   Описание: ");
                System.out.println(mesg.getDescriptor());
            }
            if (mesg.getDeviceIndex() != null) {
                System.out.print("   Индекс устройства: ");
                System.out.println(mesg.getDeviceIndex());
            }
            if (mesg.getSourceType() != null) {
                System.out.print("   Тип источника: ");
                System.out.println(FitTools.sourceTypeById(mesg.getSourceType()));
            }
            if (mesg.getSensorPosition() != null) {
                System.out.print("   SensorPosition: ");
                System.out.println(mesg.getSensorPosition());
            }
            if (mesg.getAntDeviceNumber() != null) {
                System.out.print("   Номер устройства ANT: ");
                System.out.println(mesg.getAntDeviceNumber());
            }
            if (mesg.getAntDeviceType() != null) {
                System.out.print("   Тип устройства ANT: ");
                System.out.println(mesg.getAntDeviceType());
            }
            if (mesg.getCumOperatingTime() != null) {
                System.out.print("   CumOperatingTime: ");
                System.out.println(mesg.getCumOperatingTime());
            }
            if (mesg.getAntNetwork() != null) {
                System.out.print("   Тип сети ANT: ");
                System.out.println(FitTools.antNetworkById(mesg.getAntNetwork()));
            }
            if (mesg.getAntplusDeviceType() != null) {
                System.out.print("   Тип устройства ANT: ");
                System.out.println(FitTools.deviceByType(mesg.getAntplusDeviceType()) + " [" + mesg.getAntplusDeviceType() + "]");
            }
            if (mesg.getAntTransmissionType() != null) {
                System.out.print("   Тип передачи ANT: ");
                System.out.println(mesg.getAntTransmissionType());
            }

        }
    }

    private static class MonitoringListener implements MonitoringMesgListener {

        @Override
        public void onMesg(MonitoringMesg mesg) {
            System.out.println("Monitoring:");

            if (mesg.getTimestamp() != null) {
                System.out.print("   Timestamp: ");
                System.out.println(mesg.getTimestamp());
            }

            if (mesg.getActivityType() != null) {
                System.out.print("   Activity Type: ");
                System.out.println(mesg.getActivityType());
            }

            // Depending on the ActivityType, there may be Steps, Strokes, or Cycles present in the file
            if (mesg.getSteps() != null) {
                System.out.print("   Steps: ");
                System.out.println(mesg.getSteps());
            } else if (mesg.getStrokes() != null) {
                System.out.print("   Strokes: ");
                System.out.println(mesg.getStrokes());
            } else if (mesg.getCycles() != null) {
                System.out.print("   Cycles: ");
                System.out.println(mesg.getCycles());
            }
        }
    }

    private static class EventListener implements EventMesgListener {
        @Override
        public void onMesg(EventMesg mesg) {
            System.out.println("Event:");

            if (mesg.getTimestamp() != null) {
                System.out.print("   Timestamp: ");
                System.out.println(mesg.getTimestamp());
            }
            if (mesg.getData() != null) {
                System.out.print("   Data: ");
                System.out.println(mesg.getData());
            }
            if (mesg.getData16() != null) {
                System.out.print("   Data16: ");
                System.out.println(mesg.getData16());
            }
            if (mesg.getEventType() != null) {
                System.out.print("   EventType: ");
                System.out.println(mesg.getEventType());
            }
            if (mesg.getEvent() != null) {
                System.out.print("   Event: ");
                System.out.println(mesg.getEvent());
            }
            if (mesg.getBatteryLevel() != null) {
                System.out.print("   BatteryLevel: ");
                System.out.println(mesg.getBatteryLevel());
            }
            if (mesg.getEventGroup() != null) {
                System.out.print("   EventGroup: ");
                System.out.println(mesg.getEventGroup());
            }
            if (mesg.getFrontGear() != null) {
                System.out.print("   FrontGear: ");
                System.out.println(mesg.getFrontGear());
            }
            if (mesg.getFrontGearNum() != null) {
                System.out.print("   FrontGear: ");
                System.out.println(mesg.getFrontGearNum());
            }
            if (mesg.getRearGear() != null) {
                System.out.print("   RearGear: ");
                System.out.println(mesg.getRearGear());
            }
            if (mesg.getRearGearNum() != null) {
                System.out.print("   RearGearNum: ");
                System.out.println(mesg.getRearGearNum());
            }
            if (mesg.getGearChangeData() != null) {
                System.out.print("   GearChangeData: ");
                System.out.println(mesg.getGearChangeData());
            }
            if (mesg.getRiderPosition() != null) {
                System.out.print("   RiderPosition: ");
                System.out.println(mesg.getRiderPosition());
            }
            if (mesg.getHrHighAlert() != null) {
                System.out.print("   HrHighAlert: ");
                System.out.println(mesg.getHrHighAlert());
            }
            if (mesg.getHrLowAlert() != null) {
                System.out.print("   HrLowAlert: ");
                System.out.println(mesg.getHrLowAlert());
            }
            if (mesg.getCadHighAlert() != null) {
                System.out.print("   CadHighAlert: ");
                System.out.println(mesg.getCadHighAlert());
            }
            if (mesg.getCadLowAlert() != null) {
                System.out.print("   CadLowAlert: ");
                System.out.println(mesg.getCadLowAlert());
            }
            if (mesg.getPowerHighAlert() != null) {
                System.out.print("   PowerHighAlert: ");
                System.out.println(mesg.getPowerHighAlert());
            }
            if (mesg.getPowerLowAlert() != null) {
                System.out.print("   PowerLowAlert: ");
                System.out.println(mesg.getPowerLowAlert());
            }
            if (mesg.getSpeedHighAlert() != null) {
                System.out.print("   SpeedHighAlert: ");
                System.out.println(mesg.getSpeedHighAlert());
            }
            if (mesg.getSpeedLowAlert() != null) {
                System.out.print("   SpeedLowAlert: ");
                System.out.println(mesg.getSpeedLowAlert());
            }
            if (mesg.getCalorieDurationAlert() != null) {
                System.out.print("   CalorieDurationAlert: ");
                System.out.println(mesg.getCalorieDurationAlert());
            }
            if (mesg.getDistanceDurationAlert() != null) {
                System.out.print("   DistanceDurationAlert: ");
                System.out.println(mesg.getDistanceDurationAlert());
            }
            if (mesg.getTimeDurationAlert() != null) {
                System.out.print("   TimeDurationAlert: ");
                System.out.println(mesg.getTimeDurationAlert());
            }
            if (mesg.getCoursePointIndex() != null) {
                System.out.print("   CoursePointIndex: ");
                System.out.println(mesg.getCoursePointIndex());
            }
            if (mesg.getFitnessEquipmentState() != null) {
                System.out.print("   FitnessEquipmentState: ");
                System.out.println(mesg.getFitnessEquipmentState());
            }
            if (mesg.getSportPoint() != null) {
                System.out.print("   SportPoint: ");
                System.out.println(mesg.getSportPoint());
            }
            if (mesg.getScore() != null) {
                System.out.print("   Score: ");
                System.out.println(mesg.getScore());
            }
            if (mesg.getOpponentScore() != null) {
                System.out.print("   OpponentScore: ");
                System.out.println(mesg.getOpponentScore());
            }
            if (mesg.getVirtualPartnerSpeed() != null) {
                System.out.print("   VirtualPartnerSpeed: ");
                System.out.println(mesg.getVirtualPartnerSpeed());
            }
            if (mesg.getTimerTrigger() != null) {
                System.out.print("   TimerTrigger: ");
                System.out.println(mesg.getTimerTrigger());
            }



        }
    }

    public static void main(String[] args) {
        com.garmin.fit.Decode decode = new com.garmin.fit.Decode();
        //decode.skipHeader();        // Use on streams with no header and footer (stream contains FIT defn and data messages only)
        //decode.incompleteStream();  // This suppresses exceptions with unexpected eof (also incorrect crc)
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);

        FileIdListener fileIdListener = new FileIdListener();
        UserProfileListener userProfileListener = new UserProfileListener();
        DeviceInfoListener deviceInfoListener = new DeviceInfoListener();
        MonitoringListener monitoringListener = new MonitoringListener();
        EventListener eventListener = new EventListener();

         FileInputStream in;

        if (args.length != 1) {
            System.out.println("Usage: java -cp fit2gpx.jar DebugDecode <filename>");
            return;
        }

        try {
            in = new FileInputStream(args[0]);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + args[0] + " [1]");
        }

        try {
            if (!com.garmin.fit.Decode.checkIntegrity((InputStream) in))
                throw new RuntimeException("FIT file integrity failed.");
        } catch (RuntimeException e) {
            System.err.print("Exception Checking File Integrity: ");
            System.err.println(e.getMessage());
            System.err.println("Trying to continue...");
        } finally {
            try {
                in.close();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            in = new FileInputStream(args[0]);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + args[0] + " [2]");
        }

        mesgBroadcaster.addListener(fileIdListener);
        mesgBroadcaster.addListener(userProfileListener);
        mesgBroadcaster.addListener(deviceInfoListener);
        mesgBroadcaster.addListener(eventListener);

        try {
            mesgBroadcaster.run(in);
        } catch (FitRuntimeException e) {
            System.err.print("Exception decoding file: ");
            System.err.println(e.getMessage());

            try {
                in.close();
            } catch (java.io.IOException f) {
                throw new RuntimeException(f);
            }

            return;
        }

        try {
            in.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println("Decoded FIT file " + args[0]);
    }
}
