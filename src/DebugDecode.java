////////////////////////////////////////////////////////////////////////////////
// Copyright 2015 Dynastream Innovations Inc.
// Copyright © 2015 by Maks Vasilev
//
////////////////////////////////////////////////////////////////////////////////


//package com.garmin.fit.examples;

import com.garmin.fit.*;

import java.io.FileInputStream;

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
            System.out.println("Событие:");

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

    private static class LapListener implements LapMesgListener {
        @Override
        public void onMesg(LapMesg mesg) {
            System.out.println("Круг:");

            if (mesg.getStartTime() != null) {
                System.out.print("   StartTime: ");
                System.out.println(mesg.getStartTime());
            }
            if (mesg.getTimestamp() != null) {
                System.out.print("   Timestamp: ");
                System.out.println(mesg.getTimestamp());
            }
            if (mesg.getEvent() != null) {
                System.out.print("   Event: ");
                System.out.println(mesg.getEvent());
            }
            if (mesg.getEventType() != null) {
                System.out.print("   EventType: ");
                System.out.println(mesg.getEventType());
            }
            if (mesg.getEventGroup() != null) {
                System.out.print("   EventGroup: ");
                System.out.println(mesg.getEventGroup());
            }
            if (mesg.getSport() != null) {
                System.out.print("   Sport: ");
                System.out.println(mesg.getSport());
            }
            if (mesg.getSubSport() != null) {
                System.out.print("   SubSport: ");
                System.out.println(mesg.getSubSport());
            }
            if (mesg.getAvgAltitude() != null) {
                System.out.print("   AvgAltitude: ");
                System.out.println(mesg.getAvgAltitude());
            }
             if (mesg.getMinAltitude() != null) {
                System.out.print("   MinAltitude: ");
                System.out.println(mesg.getMinAltitude());
            }
            if (mesg.getMaxAltitude() != null) {
                System.out.print("   MaxAltitude: ");
                System.out.println(mesg.getMaxAltitude());
            }
            if (mesg.getEnhancedAvgAltitude() != null) {
                System.out.print("   EnhancedAvgAltitude: ");
                System.out.println(mesg.getEnhancedAvgAltitude());
            }
            if (mesg.getEnhancedMinAltitude() != null) {
                System.out.print("   EnhancedMinAltitude: ");
                System.out.println(mesg.getEnhancedMinAltitude());
            }
            if (mesg.getEnhancedMaxAltitude() != null) {
                System.out.print("   EnhancedMaxAltitude: ");
                System.out.println(mesg.getEnhancedMaxAltitude());
            }
            if (mesg.getAvgHeartRate() != null) {
                System.out.print("   AvgHeartRate: ");
                System.out.println(mesg.getAvgHeartRate());
            }
            if (mesg.getMinHeartRate() != null) {
                System.out.print("   MinHeartRate: ");
                System.out.println(mesg.getMinHeartRate());
            }
            if (mesg.getMaxHeartRate() != null) {
                System.out.print("   MaxHeartRate: ");
                System.out.println(mesg.getMaxHeartRate());
            }
            if (mesg.getAvgCadence() != null) {
                System.out.print("   AvgCadence: ");
                System.out.println(mesg.getAvgCadence());
            }
            if (mesg.getMaxCadence() != null) {
                System.out.print("   MaxCadence: ");
                System.out.println(mesg.getMaxCadence());
            }
            if (mesg.getAvgFractionalCadence() != null) {
                System.out.print("   AvgFractionalCadence: ");
                System.out.println(mesg.getAvgFractionalCadence());
            }
            if (mesg.getMaxFractionalCadence() != null) {
                System.out.print("   MaxFractionalCadence: ");
                System.out.println(mesg.getMaxFractionalCadence());
            }
            if (mesg.getAvgRunningCadence() != null) {
                System.out.print("   AvgRunningCadence: ");
                System.out.println(mesg.getAvgRunningCadence());
            }
            if (mesg.getMaxRunningCadence() != null) {
                System.out.print("   MaxRunningCadence: ");
                System.out.println(mesg.getMaxRunningCadence());
            }
            if (mesg.getAvgSpeed() != null) {
                System.out.print("   AvgSpeed (км/час): ");
                System.out.println(mesg.getAvgSpeed()*3.6);
            }
            if (mesg.getMaxSpeed() != null) {
                System.out.print("   MaxSpeed (км/час): ");
                System.out.println(mesg.getMaxSpeed()*3.6);
            }
            if (mesg.getEnhancedAvgSpeed() != null) {
                System.out.print("   EnhancedAvgSpeed (км/час): ");
                System.out.println(mesg.getEnhancedAvgSpeed()*3.6);
            }
            if (mesg.getEnhancedMaxSpeed() != null) {
                System.out.print("   EnhancedMaxSpeed (км/час): ");
                System.out.println(mesg.getEnhancedMaxSpeed()*3.6);
            }
            if (mesg.getAvgPower() != null) {
                System.out.print("   AvgPower: ");
                System.out.println(mesg.getAvgPower());
            }
            if (mesg.getMaxPower() != null) {
                System.out.print("   MaxPower: ");
                System.out.println(mesg.getMaxPower());
            }
            if (mesg.getNormalizedPower() != null) {
                System.out.print("   NormalizedPower: ");
                System.out.println(mesg.getNormalizedPower());
            }
            if (mesg.getAvgTemperature() != null) {
                System.out.print("   AvgTemperature: ");
                System.out.println(mesg.getAvgTemperature());
            }
            if (mesg.getMaxTemperature() != null) {
                System.out.print("   MaxTemperature: ");
                System.out.println(mesg.getMaxTemperature());
            }
            if (mesg.getAvgGrade() != null) {
                System.out.print("   AvgGrade (%): ");
                System.out.println(mesg.getAvgGrade());
            }
            if (mesg.getAvgPosGrade() != null) {
                System.out.print("   AvgPosGrade (%): ");
                System.out.println(mesg.getAvgPosGrade());
            }
            if (mesg.getMaxPosGrade() != null) {
                System.out.print("   MaxPosGrade (%): ");
                System.out.println(mesg.getMaxPosGrade());
            }
            if (mesg.getAvgNegGrade() != null) {
                System.out.print("   AvgNegGrade (%): ");
                System.out.println(mesg.getAvgNegGrade());
            }
            if (mesg.getMaxNegGrade() != null) {
                System.out.print("   MaxNegGrade (%): ");
                System.out.println(mesg.getMaxNegGrade());
            }
            if (mesg.getAvgPosVerticalSpeed() != null) {
                System.out.print("   AvgPosVerticalSpeed (м/с): ");
                System.out.println(mesg.getAvgPosVerticalSpeed());
            }
            if (mesg.getMaxPosVerticalSpeed() != null) {
                System.out.print("   MaxPosVerticalSpeed (м/с): ");
                System.out.println(mesg.getMaxPosVerticalSpeed());
            }
            if (mesg.getAvgNegVerticalSpeed() != null) {
                System.out.print("   AvgNegVerticalSpeed (м/с): ");
                System.out.println(mesg.getAvgNegVerticalSpeed());
            }
            if (mesg.getMaxNegVerticalSpeed() != null) {
                System.out.print("   MaxNegVerticalSpeed (м/с): ");
                System.out.println(mesg.getMaxNegVerticalSpeed());
            }
            if (mesg.getTotalAscent() != null) {
                System.out.print("   TotalAscent (м): ");
                System.out.println(mesg.getTotalAscent());
            }
            if (mesg.getTotalDescent() != null) {
                System.out.print("   TotalDescent (м): ");
                System.out.println(mesg.getTotalDescent());
            }
            if (mesg.getTotalDistance() != null) {
                System.out.print("   TotalDistance (км): ");
                System.out.println(mesg.getTotalDistance()/1000.0);
            }
            if (mesg.getTotalMovingTime() != null) {
                System.out.print("   TotalMovingTime (мин): ");
                System.out.println(mesg.getTotalMovingTime()/60);
            }
            if (mesg.getTotalTimerTime() != null) {
                System.out.print("   TotalTimerTime (мин): ");
                System.out.println(mesg.getTotalTimerTime()/60);
            }
            if (mesg.getTotalElapsedTime() != null) {
                System.out.print("   TotalElapsedTime (мин): ");
                System.out.println(mesg.getTotalElapsedTime()/60);
            }
              if (mesg.getTotalCalories() != null) {
                System.out.print("   TotalCalories (ккал): ");
                System.out.println(mesg.getTotalCalories());
            }
            if (mesg.getTotalFatCalories() != null) {
                System.out.print("   TotalFatCalories (ккал): ");
                System.out.println(mesg.getTotalFatCalories());
            }
            if (mesg.getTotalCycles() != null) {
                System.out.print("   TotalCycles: ");
                System.out.println(mesg.getTotalCycles());
            }
            if (mesg.getTotalFractionalCycles() != null) {
                System.out.print("   TotalFractionalCycles: ");
                System.out.println(mesg.getTotalFractionalCycles());
            }
            if (mesg.getTotalStrides() != null) {
                System.out.print("   TotalStrides: ");
                System.out.println(mesg.getTotalStrides());
            }
            if (mesg.getTotalWork() != null) {
                System.out.print("   TotalWork: ");
                System.out.println(mesg.getTotalWork());
            }
            if (mesg.getTimeStanding() != null) {
                System.out.print("   TimeStanding: ");
                System.out.println(mesg.getTimeStanding());
            }
            if (mesg.getStandCount() != null) {
                System.out.print("   StandCount: ");
                System.out.println(mesg.getStandCount());
            }
            if (mesg.getAvgStanceTime() != null) {
                System.out.print("   AvgStanceTime: ");
                System.out.println(mesg.getAvgStanceTime());
            }
            if (mesg.getAvgStanceTimePercent() != null) {
                System.out.print("   AvgStanceTimePercent: ");
                System.out.println(mesg.getAvgStanceTimePercent());
            }
            if (mesg.getGpsAccuracy() != null) {
                System.out.print("   GpsAccuracy: ");
                System.out.println(mesg.getGpsAccuracy());
            }
            if (mesg.getStartPositionLat() != null) {
                System.out.print("   StartPositionLat: ");
                System.out.println(mesg.getStartPositionLat());
            }
            if (mesg.getStartPositionLong() != null) {
                System.out.print("   StartPositionLong: ");
                System.out.println(mesg.getStartPositionLong());
            }
            if (mesg.getEndPositionLat() != null) {
                System.out.print("   EndPositionLat: ");
                System.out.println(mesg.getEndPositionLat());
            }
            if (mesg.getEndPositionLong() != null) {
                System.out.print("   EndPositionLong: ");
                System.out.println(mesg.getEndPositionLong());
            }
            if (mesg.getAvgVerticalOscillation() != null) {
                System.out.print("   AvgVerticalOscillation: ");
                System.out.println(mesg.getAvgVerticalOscillation());
            }
            if (mesg.getLeftRightBalance() != null) {
                System.out.print("   LeftRightBalance: ");
                System.out.println(mesg.getLeftRightBalance());
            }
            if (mesg.getAvgLeftPco() != null) {
                System.out.print("   AvgLeftPco: ");
                System.out.println(mesg.getAvgLeftPco());
            }
            if (mesg.getAvgRightPco() != null) {
                System.out.print("   AvgRightPco: ");
                System.out.println(mesg.getAvgRightPco());
            }
            if (mesg.getAvgLeftPedalSmoothness() != null) {
                System.out.print("   AvgLeftPedalSmoothness: ");
                System.out.println(mesg.getAvgLeftPedalSmoothness());
            }
            if (mesg.getAvgRightPedalSmoothness() != null) {
                System.out.print("   AvgRightPedalSmoothness: ");
                System.out.println(mesg.getAvgRightPedalSmoothness());
            }
            if (mesg.getAvgLeftTorqueEffectiveness() != null) {
                System.out.print("   AvgLeftTorqueEffectiveness: ");
                System.out.println(mesg.getAvgLeftTorqueEffectiveness());
            }
            if (mesg.getAvgRightTorqueEffectiveness() != null) {
                System.out.print("   AvgRightTorqueEffectiveness: ");
                System.out.println(mesg.getAvgRightTorqueEffectiveness());
            }
            if (mesg.getNumLengths() != null) {
                System.out.print("   NumLengths: ");
                System.out.println(mesg.getNumLengths());
            }
            if (mesg.getNumActiveLengths() != null) {
                System.out.print("   NumActiveLengths: ");
                System.out.println(mesg.getNumActiveLengths());
            }
            if (mesg.getFirstLengthIndex() != null) {
                System.out.print("   FirstLengthIndex: ");
                System.out.println(mesg.getFirstLengthIndex());
            }
            if (mesg.getSwimStroke() != null) {
                System.out.print("   SwimStroke: ");
                System.out.println(mesg.getSwimStroke());
            }
            if (mesg.getAvgStrokeDistance() != null) {
                System.out.print("   AvgStrokeDistance: ");
                System.out.println(mesg.getAvgStrokeDistance());
            }
            if (mesg.getNumStrokeCount() != 0) {
                System.out.print("   NumStrokeCount: ");
                System.out.println(mesg.getNumStrokeCount());
            }
            if (mesg.getNumTimeInHrZone() != 0) {
                System.out.print("   NumTimeInHrZone: ");
                System.out.println(mesg.getNumTimeInHrZone());
            }
            if (mesg.getTimeInHrZone(0) != null) {
                System.out.print("   TimeInHrZone0: ");
                System.out.println(mesg.getTimeInHrZone(0));
            }
            if (mesg.getTimeInHrZone(1) != null) {
                System.out.print("   TimeInHrZone1: ");
                System.out.println(mesg.getTimeInHrZone(1));
            }
            if (mesg.getTimeInHrZone(2) != null) {
                System.out.print("   TimeInHrZone2: ");
                System.out.println(mesg.getTimeInHrZone(2));
            }
            if (mesg.getTimeInHrZone(3) != null) {
                System.out.print("   TimeInHrZone3: ");
                System.out.println(mesg.getTimeInHrZone(3));
            }
            if (mesg.getTimeInHrZone(4) != null) {
                System.out.print("   TimeInHrZone4: ");
                System.out.println(mesg.getTimeInHrZone(4));
            }
            if (mesg.getTimeInHrZone(5) != null) {
                System.out.print("   TimeInHrZone5: ");
                System.out.println(mesg.getTimeInHrZone(5));
            }
            if (mesg.getNumTimeInCadenceZone() != 0) {
                System.out.print("   NumTimeInCadenceZone: ");
                System.out.println(mesg.getNumTimeInCadenceZone());
            }
            if (mesg.getTimeInCadenceZone(0) != null) {
                System.out.print("   TimeInCadenceZone0: ");
                System.out.println(mesg.getTimeInCadenceZone(0));
            }
            if (mesg.getTimeInCadenceZone(1) != null) {
                System.out.print("   TimeInCadenceZone1: ");
                System.out.println(mesg.getTimeInCadenceZone(1));
            }
            if (mesg.getTimeInCadenceZone(2) != null) {
                System.out.print("   TimeInCadenceZone2: ");
                System.out.println(mesg.getTimeInCadenceZone(2));
            }
            if (mesg.getTimeInCadenceZone(3) != null) {
                System.out.print("   TimeInCadenceZone3: ");
                System.out.println(mesg.getTimeInCadenceZone(3));
            }
            if (mesg.getTimeInCadenceZone(4) != null) {
                System.out.print("   TimeInCadenceZone4: ");
                System.out.println(mesg.getTimeInHrZone(4));
            }
            if (mesg.getTimeInCadenceZone(5) != null) {
                System.out.print("   TimeInCadenceZone5: ");
                System.out.println(mesg.getTimeInCadenceZone(5));
            }
            if (mesg.getNumTimeInSpeedZone() != 0) {
                System.out.print("   NumTimeInSpeedZone: ");
                System.out.println(mesg.getNumTimeInSpeedZone());
            }
            if (mesg.getTimeInSpeedZone(0) != null) {
                System.out.print("   TimeInSpeedZone0: ");
                System.out.println(mesg.getTimeInSpeedZone(0));
            }
            if (mesg.getTimeInSpeedZone(1) != null) {
                System.out.print("   TimeInSpeedZone1: ");
                System.out.println(mesg.getTimeInSpeedZone(1));
            }
            if (mesg.getTimeInSpeedZone(2) != null) {
                System.out.print("   TimeInSpeedZone2: ");
                System.out.println(mesg.getTimeInSpeedZone(2));
            }
            if (mesg.getTimeInSpeedZone(3) != null) {
                System.out.print("   TimeInSpeedZone3: ");
                System.out.println(mesg.getTimeInSpeedZone(3));
            }
            if (mesg.getTimeInSpeedZone(4) != null) {
                System.out.print("   TimeInSpeedZone4: ");
                System.out.println(mesg.getTimeInSpeedZone(4));
            }
            if (mesg.getTimeInSpeedZone(5) != null) {
                System.out.print("   TimeInSpeedZone5: ");
                System.out.println(mesg.getTimeInSpeedZone(5));
            }
            if (mesg.getNumTimeInPowerZone() != 0) {
                System.out.print("   NumTimeInPowerZone: ");
                System.out.println(mesg.getNumTimeInPowerZone());
            }
            if (mesg.getTimeInPowerZone(0) != null) {
                System.out.print("   TimeInPowerZone0: ");
                System.out.println(mesg.getTimeInPowerZone(0));
            }
            if (mesg.getTimeInPowerZone(1) != null) {
                System.out.print("   TimeInPowerZone1: ");
                System.out.println(mesg.getTimeInPowerZone(1));
            }
            if (mesg.getTimeInPowerZone(2) != null) {
                System.out.print("   TimeInPowerZone2: ");
                System.out.println(mesg.getTimeInPowerZone(2));
            }
            if (mesg.getTimeInPowerZone(3) != null) {
                System.out.print("   TimeInPowerZone3: ");
                System.out.println(mesg.getTimeInPowerZone(3));
            }
            if (mesg.getTimeInPowerZone(4) != null) {
                System.out.print("   TimeInPowerZone4: ");
                System.out.println(mesg.getTimeInPowerZone(4));
            }
            if (mesg.getTimeInPowerZone(5) != null) {
                System.out.print("   TimeInPowerZone5: ");
                System.out.println(mesg.getTimeInPowerZone(5));
            }



        }
    }
    
    private static class TotalsListener implements TotalsMesgListener { // оно вообще встречается в природе?

        @Override
        public void onMesg(TotalsMesg mesg) {
            System.out.println("Всего:");

            if (mesg.getTimestamp() != null) {
                System.out.print("   Timestamp: ");
                System.out.println(mesg.getTimestamp());
            }
            if (mesg.getSport() != null) {
                System.out.print("   Sport: ");
                System.out.println(mesg.getSport());
            }
            if (mesg.getActiveTime() != null) {
                System.out.print("   ActiveTime: ");
                System.out.println(mesg.getActiveTime());
            }
            if (mesg.getTimerTime() != null) {
                System.out.print("   TimerTime: ");
                System.out.println(mesg.getTimerTime());
            }
            if (mesg.getElapsedTime() != null) {
                System.out.print("   ElapsedTime: ");
                System.out.println(mesg.getElapsedTime());
            }
            if (mesg.getSessions() != null) {
                System.out.print("   Sessions: ");
                System.out.println(mesg.getSessions());
            }
            if (mesg.getDistance() != null) {
                System.out.print("   Distance: ");
                System.out.println(mesg.getDistance());
            }
            if (mesg.getCalories() != null) {
                System.out.print("   Calories: ");
                System.out.println(mesg.getCalories());
            }
            if (mesg.getMessageIndex() != null) {
                System.out.print("   MessageIndex: ");
                System.out.println(mesg.getMessageIndex());
            }


        }
    }

    private static class SessionListener implements SessionMesgListener {

        @Override
        public void onMesg(SessionMesg mesg) {
            System.out.println("Сессия:");

            if (mesg.getStartTime() != null) {
                System.out.print("   StartTime: ");
                System.out.println(mesg.getStartTime());
            }
            if (mesg.getTimestamp() != null) {
                System.out.print("   Timestamp: ");
                System.out.println(mesg.getTimestamp());
            }
            if (mesg.getSport() != null) {
                System.out.print("   Sport: ");
                System.out.println(mesg.getSport());
            }
            if (mesg.getSubSport() != null) {
                System.out.print("   SubSport: ");
                System.out.println(mesg.getSubSport());
            }
            if (mesg.getSportIndex() != null) {
                System.out.print("   SportIndex: ");
                System.out.println(mesg.getSportIndex());
            }
            if (mesg.getAvgLapTime() != null) {
                System.out.print("   AvgLapTime: ");
                System.out.println(mesg.getAvgLapTime());
            }
            if (mesg.getGpsAccuracy() != null) {
                System.out.print("   GpsAccuracy: ");
                System.out.println(mesg.getGpsAccuracy());
            }
            if (mesg.getEvent() != null) {
                System.out.print("   Event: ");
                System.out.println(mesg.getEvent());
            }
            if (mesg.getEventType() != null) {
                System.out.print("   EventType: ");
                System.out.println(mesg.getEventType());
            }
            if (mesg.getEventGroup() != null) {
                System.out.print("   EventGroup: ");
                System.out.println(mesg.getEventGroup());
            }
            if (mesg.getAvgAltitude() != null) {
                System.out.print("   AvgAltitude: ");
                System.out.println(mesg.getAvgAltitude());
            }
            if (mesg.getMinAltitude() != null) {
                System.out.print("   MinAltitude: ");
                System.out.println(mesg.getMinAltitude());
            }
            if (mesg.getMaxAltitude() != null) {
                System.out.print("   MaxAltitude: ");
                System.out.println(mesg.getMaxAltitude());
            }
            if (mesg.getEnhancedAvgAltitude() != null) {
                System.out.print("   EnhancedAvgAltitude: ");
                System.out.println(mesg.getEnhancedAvgAltitude());
            }
            if (mesg.getEnhancedMinAltitude() != null) {
                System.out.print("   EnhancedMinAltitude: ");
                System.out.println(mesg.getEnhancedMinAltitude());
            }
            if (mesg.getEnhancedMaxAltitude() != null) {
                System.out.print("   EnhancedMaxAltitude: ");
                System.out.println(mesg.getEnhancedMaxAltitude());
            }
            if (mesg.getAvgHeartRate() != null) {
                System.out.print("   AvgHeartRate: ");
                System.out.println(mesg.getAvgHeartRate());
            }
            if (mesg.getMinHeartRate() != null) {
                System.out.print("   MinHeartRate: ");
                System.out.println(mesg.getMinHeartRate());
            }
            if (mesg.getMaxHeartRate() != null) {
                System.out.print("   MaxHeartRate: ");
                System.out.println(mesg.getMaxHeartRate());
            }
            if (mesg.getAvgCadence() != null) {
                System.out.print("   AvgCadence: ");
                System.out.println(mesg.getAvgCadence());
            }
            if (mesg.getMaxCadence() != null) {
                System.out.print("   MaxCadence: ");
                System.out.println(mesg.getMaxCadence());
            }
            if (mesg.getAvgFractionalCadence() != null) {
                System.out.print("   AvgFractionalCadence: ");
                System.out.println(mesg.getAvgFractionalCadence());
            }
            if (mesg.getMaxFractionalCadence() != null) {
                System.out.print("   MaxFractionalCadence: ");
                System.out.println(mesg.getMaxFractionalCadence());
            }
            if (mesg.getAvgRunningCadence() != null) {
                System.out.print("   AvgRunningCadence: ");
                System.out.println(mesg.getAvgRunningCadence());
            }
            if (mesg.getMaxRunningCadence() != null) {
                System.out.print("   MaxRunningCadence: ");
                System.out.println(mesg.getMaxRunningCadence());
            }
            if (mesg.getAvgSpeed() != null) {
                System.out.print("   AvgSpeed (км/час): ");
                System.out.println(mesg.getAvgSpeed()*3.6);
            }
            if (mesg.getMaxSpeed() != null) {
                System.out.print("   MaxSpeed (км/час): ");
                System.out.println(mesg.getMaxSpeed()*3.6);
            }
            if (mesg.getEnhancedAvgSpeed() != null) {
                System.out.print("   EnhancedAvgSpeed (км/час): ");
                System.out.println(mesg.getEnhancedAvgSpeed()*3.6);
            }
            if (mesg.getEnhancedMaxSpeed() != null) {
                System.out.print("   EnhancedMaxSpeed (км/час): ");
                System.out.println(mesg.getEnhancedMaxSpeed()*3.6);
            }
            if (mesg.getAvgPower() != null) {
                System.out.print("   AvgPower: ");
                System.out.println(mesg.getAvgPower());
            }
            if (mesg.getMaxPower() != null) {
                System.out.print("   MaxPower: ");
                System.out.println(mesg.getMaxPower());
            }
            if (mesg.getNormalizedPower() != null) {
                System.out.print("   NormalizedPower: ");
                System.out.println(mesg.getNormalizedPower());
            }
            if (mesg.getAvgTemperature() != null) {
                System.out.print("   AvgTemperature: ");
                System.out.println(mesg.getAvgTemperature());
            }
            if (mesg.getMaxTemperature() != null) {
                System.out.print("   MaxTemperature: ");
                System.out.println(mesg.getMaxTemperature());
            }
            if (mesg.getAvgGrade() != null) {
                System.out.print("   AvgGrade (%): ");
                System.out.println(mesg.getAvgGrade());
            }
            if (mesg.getAvgPosGrade() != null) {
                System.out.print("   AvgPosGrade (%): ");
                System.out.println(mesg.getAvgPosGrade());
            }
            if (mesg.getMaxPosGrade() != null) {
                System.out.print("   MaxPosGrade (%): ");
                System.out.println(mesg.getMaxPosGrade());
            }
            if (mesg.getAvgNegGrade() != null) {
                System.out.print("   AvgNegGrade (%): ");
                System.out.println(mesg.getAvgNegGrade());
            }
            if (mesg.getMaxNegGrade() != null) {
                System.out.print("   MaxNegGrade (%): ");
                System.out.println(mesg.getMaxNegGrade());
            }
            if (mesg.getAvgPosVerticalSpeed() != null) {
                System.out.print("   AvgPosVerticalSpeed (м/с): ");
                System.out.println(mesg.getAvgPosVerticalSpeed());
            }
            if (mesg.getMaxPosVerticalSpeed() != null) {
                System.out.print("   MaxPosVerticalSpeed (м/с): ");
                System.out.println(mesg.getMaxPosVerticalSpeed());
            }
            if (mesg.getAvgNegVerticalSpeed() != null) {
                System.out.print("   AvgNegVerticalSpeed (м/с): ");
                System.out.println(mesg.getAvgNegVerticalSpeed());
            }
            if (mesg.getMaxNegVerticalSpeed() != null) {
                System.out.print("   MaxNegVerticalSpeed (м/с): ");
                System.out.println(mesg.getMaxNegVerticalSpeed());
            }
            if (mesg.getTotalAscent() != null) {
                System.out.print("   TotalAscent (м): ");
                System.out.println(mesg.getTotalAscent());
            }
            if (mesg.getTotalDescent() != null) {
                System.out.print("   TotalDescent (м): ");
                System.out.println(mesg.getTotalDescent());
            }
            if (mesg.getTotalDistance() != null) {
                System.out.print("   TotalDistance (км): ");
                System.out.println(mesg.getTotalDistance()/1000.0);
            }
            if (mesg.getTotalMovingTime() != null) {
                System.out.print("   TotalMovingTime (мин): ");
                System.out.println(mesg.getTotalMovingTime()/60);
            }
            if (mesg.getTotalTimerTime() != null) {
                System.out.print("   TotalTimerTime (мин): ");
                System.out.println(mesg.getTotalTimerTime()/60);
            }
            if (mesg.getTotalElapsedTime() != null) {
                System.out.print("   TotalElapsedTime (мин): ");
                System.out.println(mesg.getTotalElapsedTime()/60);
            }
            if (mesg.getTotalCalories() != null) {
                System.out.print("   TotalCalories (ккал): ");
                System.out.println(mesg.getTotalCalories());
            }
            if (mesg.getTotalFatCalories() != null) {
                System.out.print("   TotalFatCalories (ккал): ");
                System.out.println(mesg.getTotalFatCalories());
            }
            if (mesg.getTotalCycles() != null) {
                System.out.print("   TotalCycles: ");
                System.out.println(mesg.getTotalCycles());
            }
            if (mesg.getTotalFractionalCycles() != null) {
                System.out.print("   TotalFractionalCycles: ");
                System.out.println(mesg.getTotalFractionalCycles());
            }
            if (mesg.getTotalStrides() != null) {
                System.out.print("   TotalStrides: ");
                System.out.println(mesg.getTotalStrides());
            }
            if (mesg.getTotalWork() != null) {
                System.out.print("   TotalWork: ");
                System.out.println(mesg.getTotalWork());
            }
            if (mesg.getTimeStanding() != null) {
                System.out.print("   TimeStanding: ");
                System.out.println(mesg.getTimeStanding());
            }
            if (mesg.getStandCount() != null) {
                System.out.print("   StandCount: ");
                System.out.println(mesg.getStandCount());
            }
            if (mesg.getAvgStanceTime() != null) {
                System.out.print("   AvgStanceTime: ");
                System.out.println(mesg.getAvgStanceTime());
            }
            if (mesg.getAvgStanceTimePercent() != null) {
                System.out.print("   AvgStanceTimePercent: ");
                System.out.println(mesg.getAvgStanceTimePercent());
            }
            if (mesg.getGpsAccuracy() != null) {
                System.out.print("   GpsAccuracy: ");
                System.out.println(mesg.getGpsAccuracy());
            }
            if (mesg.getStartPositionLat() != null) {
                System.out.print("   StartPositionLat: ");
                System.out.println(mesg.getStartPositionLat());
            }
            if (mesg.getStartPositionLong() != null) {
                System.out.print("   StartPositionLong: ");
                System.out.println(mesg.getStartPositionLong());
            }
             if (mesg.getNecLong() != null) {
                System.out.print("   NecLong: ");
                System.out.println(mesg.getNecLong());
             }
            if (mesg.getAvgVerticalOscillation() != null) {
                System.out.print("   AvgVerticalOscillation: ");
                System.out.println(mesg.getAvgVerticalOscillation());
            }
            if (mesg.getLeftRightBalance() != null) {
                System.out.print("   LeftRightBalance: ");
                System.out.println(mesg.getLeftRightBalance());
            }
            if (mesg.getAvgLeftPco() != null) {
                System.out.print("   AvgLeftPco: ");
                System.out.println(mesg.getAvgLeftPco());
            }
            if (mesg.getAvgRightPco() != null) {
                System.out.print("   AvgRightPco: ");
                System.out.println(mesg.getAvgRightPco());
            }
            if (mesg.getAvgLeftPedalSmoothness() != null) {
                System.out.print("   AvgLeftPedalSmoothness: ");
                System.out.println(mesg.getAvgLeftPedalSmoothness());
            }
            if (mesg.getAvgRightPedalSmoothness() != null) {
                System.out.print("   AvgRightPedalSmoothness: ");
                System.out.println(mesg.getAvgRightPedalSmoothness());
            }
            if (mesg.getAvgLeftTorqueEffectiveness() != null) {
                System.out.print("   AvgLeftTorqueEffectiveness: ");
                System.out.println(mesg.getAvgLeftTorqueEffectiveness());
            }
            if (mesg.getAvgRightTorqueEffectiveness() != null) {
                System.out.print("   AvgRightTorqueEffectiveness: ");
                System.out.println(mesg.getAvgRightTorqueEffectiveness());
            }
            if (mesg.getNumActiveLengths() != null) {
                System.out.print("   NumActiveLengths: ");
                System.out.println(mesg.getNumActiveLengths());
            }
            if (mesg.getPoolLength() != null) {
                System.out.print("   PoolLength: ");
                System.out.println(mesg.getPoolLength());
            }
            if (mesg.getPoolLengthUnit() != null) {
                System.out.print("   PoolLengthUnit: ");
                System.out.println(mesg.getPoolLengthUnit());
            }
            if (mesg.getSwimStroke() != null) {
                System.out.print("   SwimStroke: ");
                System.out.println(mesg.getSwimStroke());
            }
            if (mesg.getAvgStrokeDistance() != null) {
                System.out.print("   AvgStrokeDistance: ");
                System.out.println(mesg.getAvgStrokeDistance());
            }
            if (mesg.getNumStrokeCount() != 0) {
                System.out.print("   NumStrokeCount: ");
                System.out.println(mesg.getNumStrokeCount());
            }
            if (mesg.getNumTimeInHrZone() != 0) {
                System.out.print("   NumTimeInHrZone: ");
                System.out.println(mesg.getNumTimeInHrZone());
            }
            if (mesg.getTimeInHrZone(0) != null) {
                System.out.print("   TimeInHrZone0: ");
                System.out.println(mesg.getTimeInHrZone(0));
            }
            if (mesg.getTimeInHrZone(1) != null) {
                System.out.print("   TimeInHrZone1: ");
                System.out.println(mesg.getTimeInHrZone(1));
            }
            if (mesg.getTimeInHrZone(2) != null) {
                System.out.print("   TimeInHrZone2: ");
                System.out.println(mesg.getTimeInHrZone(2));
            }
            if (mesg.getTimeInHrZone(3) != null) {
                System.out.print("   TimeInHrZone3: ");
                System.out.println(mesg.getTimeInHrZone(3));
            }
            if (mesg.getTimeInHrZone(4) != null) {
                System.out.print("   TimeInHrZone4: ");
                System.out.println(mesg.getTimeInHrZone(4));
            }
            if (mesg.getTimeInHrZone(5) != null) {
                System.out.print("   TimeInHrZone5: ");
                System.out.println(mesg.getTimeInHrZone(5));
            }
            if (mesg.getNumTimeInCadenceZone() != 0) {
                System.out.print("   NumTimeInCadenceZone: ");
                System.out.println(mesg.getNumTimeInCadenceZone());
            }
            if (mesg.getTimeInCadenceZone(0) != null) {
                System.out.print("   TimeInCadenceZone0: ");
                System.out.println(mesg.getTimeInCadenceZone(0));
            }
            if (mesg.getTimeInCadenceZone(1) != null) {
                System.out.print("   TimeInCadenceZone1: ");
                System.out.println(mesg.getTimeInCadenceZone(1));
            }
            if (mesg.getTimeInCadenceZone(2) != null) {
                System.out.print("   TimeInCadenceZone2: ");
                System.out.println(mesg.getTimeInCadenceZone(2));
            }
            if (mesg.getTimeInCadenceZone(3) != null) {
                System.out.print("   TimeInCadenceZone3: ");
                System.out.println(mesg.getTimeInCadenceZone(3));
            }
            if (mesg.getTimeInCadenceZone(4) != null) {
                System.out.print("   TimeInCadenceZone4: ");
                System.out.println(mesg.getTimeInHrZone(4));
            }
            if (mesg.getTimeInCadenceZone(5) != null) {
                System.out.print("   TimeInCadenceZone5: ");
                System.out.println(mesg.getTimeInCadenceZone(5));
            }
            if (mesg.getNumTimeInSpeedZone() != 0) {
                System.out.print("   NumTimeInSpeedZone: ");
                System.out.println(mesg.getNumTimeInSpeedZone());
            }
            if (mesg.getTimeInSpeedZone(0) != null) {
                System.out.print("   TimeInSpeedZone0: ");
                System.out.println(mesg.getTimeInSpeedZone(0));
            }
            if (mesg.getTimeInSpeedZone(1) != null) {
                System.out.print("   TimeInSpeedZone1: ");
                System.out.println(mesg.getTimeInSpeedZone(1));
            }
            if (mesg.getTimeInSpeedZone(2) != null) {
                System.out.print("   TimeInSpeedZone2: ");
                System.out.println(mesg.getTimeInSpeedZone(2));
            }
            if (mesg.getTimeInSpeedZone(3) != null) {
                System.out.print("   TimeInSpeedZone3: ");
                System.out.println(mesg.getTimeInSpeedZone(3));
            }
            if (mesg.getTimeInSpeedZone(4) != null) {
                System.out.print("   TimeInSpeedZone4: ");
                System.out.println(mesg.getTimeInSpeedZone(4));
            }
            if (mesg.getTimeInSpeedZone(5) != null) {
                System.out.print("   TimeInSpeedZone5: ");
                System.out.println(mesg.getTimeInSpeedZone(5));
            }
            if (mesg.getNumTimeInPowerZone() != 0) {
                System.out.print("   NumTimeInPowerZone: ");
                System.out.println(mesg.getNumTimeInPowerZone());
            }
            if (mesg.getTimeInPowerZone(0) != null) {
                System.out.print("   TimeInPowerZone0: ");
                System.out.println(mesg.getTimeInPowerZone(0));
            }
            if (mesg.getTimeInPowerZone(1) != null) {
                System.out.print("   TimeInPowerZone1: ");
                System.out.println(mesg.getTimeInPowerZone(1));
            }
            if (mesg.getTimeInPowerZone(2) != null) {
                System.out.print("   TimeInPowerZone2: ");
                System.out.println(mesg.getTimeInPowerZone(2));
            }
            if (mesg.getTimeInPowerZone(3) != null) {
                System.out.print("   TimeInPowerZone3: ");
                System.out.println(mesg.getTimeInPowerZone(3));
            }
            if (mesg.getTimeInPowerZone(4) != null) {
                System.out.print("   TimeInPowerZone4: ");
                System.out.println(mesg.getTimeInPowerZone(4));
            }
            if (mesg.getTimeInPowerZone(5) != null) {
                System.out.print("   TimeInPowerZone5: ");
                System.out.println(mesg.getTimeInPowerZone(5));
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
        LapListener lapListener = new LapListener();
        TotalsListener totalsListener = new TotalsListener();
        SessionListener sessionListener = new SessionListener();    

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
            if (!com.garmin.fit.Decode.checkIntegrity(in))
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
        mesgBroadcaster.addListener(lapListener);
        mesgBroadcaster.addListener(totalsListener);
        mesgBroadcaster.addListener(sessionListener);

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
