package com.elan_droid.elandroid.database.data.prepopulate;

import com.elan_droid.elandroid.database.data.embedded.Request;
import com.elan_droid.elandroid.database.data.embedded.Response;
import com.elan_droid.elandroid.database.data.entity.ParameterFormatted;
import com.elan_droid.elandroid.database.data.entity.Message;
import com.elan_droid.elandroid.database.data.entity.Parameter;
import com.elan_droid.elandroid.database.data.entity.ParameterBitwise8;
import com.elan_droid.elandroid.database.data.entity.Vehicle;
import com.elan_droid.elandroid.database.data.relation.Command;

/**
 * Created by Peter Smith
 */

public class LotusElanS2 implements PrepopulateModel {

    public static final long VEHICLE_ID = 1;
    public static final String VEHICLE_MAKE = "Lotus";
    public static final String VEHICLE_MODEL = "Elan S2";

    public static Vehicle VEHICLE = new VehicleModel();

    public static Command[] COMMANDS = new Command[] {
        new DiagnosticCommand()
    };

    @Override
    public Vehicle getVehicle() {
        return VEHICLE;
    }

    @Override
    public Command[] getCommands() {
        return COMMANDS;
    }

    private static class VehicleModel extends Vehicle {

        public VehicleModel () {
            super (VEHICLE_ID, DiagnosticCommand.MESSAGE_ID, VEHICLE_MAKE, VEHICLE_MODEL);
        }

    }

    private static class DiagnosticCommand extends Command {

        private static final int MESSAGE_ID = 1;
        private static final byte[] TRIGGER = new byte[]{
                (byte) 244, (byte) 86, (byte) 1, (byte) 181
        };

        private static final int RAW_LENGTH = 71;
        private static final int VALIDATE_FROM = 0;
        private static final int VALIDATE_TO = 66;
        private static final int PAYLOAD_FROM = 7;
        private static final int PAYLOAD_TO = 70;


        private static DiagnosticMessage MESSAGE = new DiagnosticMessage();

        public DiagnosticCommand() {
            super(MESSAGE, PARAMETERS);
        }

        private static class DiagnosticMessage extends Message {

            private static final Request REQUEST = new Request(TRIGGER);
            private static final Response RESPONSE = new Response(RAW_LENGTH, VALIDATE_FROM, VALIDATE_TO, PAYLOAD_FROM, PAYLOAD_TO);

            public DiagnosticMessage() {
                super(MESSAGE_ID, VEHICLE_ID, REQUEST, RESPONSE);
            }

        }

        private static final Parameter[] PARAMETERS = {
                new ParameterFormatted(MESSAGE_ID, "PROM_ID", "Prom ID", 0, 2, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%d", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FAULTS_1", "Faults", 2, "VSS", "MAT low", "TPS low", "TPS high", "CTS low", "CTS high", "Oxygen sensor", "No ref. pulses"),
                new ParameterBitwise8(MESSAGE_ID, "FAULTS_2", "Faults", 3, "EST monitor error", "CAS fault", "IAC error", "MAP low", "MAP high", "EGR", "Wastegate overboost", "MAT high"),
                new ParameterBitwise8(MESSAGE_ID, "FAULTS_3", "Faults", 4, "ADV error", "CO pot circuit", "High battery voltage", "Fuel injector circuit", "PROM error", "Oxygen sensor rich", "Oxygen sensor lean", "ESC failure"),
                new ParameterFormatted(MESSAGE_ID, "COOL_TEMP", "Coolant temp", 5, 1, 0.75, -40.0, "%5.1f", Parameter.UNIT_DEGREE_C, false),
                new ParameterFormatted(MESSAGE_ID, "COOL_RAW", "Coolant raw", 6, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                //old UnusedParameter(MESSAGE_ID, 7, 1),
                new ParameterFormatted(MESSAGE_ID, "TPS_VOLTS", "TPS voltage", 8, 1, 0.02, Parameter.OFFSET_DEFAULT, "%5.2f", Parameter.UNIT_VOLTS, false),
                new ParameterFormatted(MESSAGE_ID, "THROTTLE", "Throttle", 9, 1, 0.392157, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_PERCENT, false),
                new ParameterFormatted(MESSAGE_ID, "ENGINE_SPEED", "Engine speed", 10, 1, 25.0, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_RPM, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_12", "Flag 12", 11, ParameterBitwise8.BIT_1, "Secondary throttle", ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, "EGR", ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "DRP_INTERVAL", "DRP interval", 12, 2, 0.01526, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_MSEC, false),
                new ParameterFormatted(MESSAGE_ID, "SPARK_ADVANCE", "Spark advance", 14, 2, 0.351562, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_DEGREES, true),  //SIGNED
                new ParameterFormatted(MESSAGE_ID, "SPEED", "Speed", 16, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_MPH, false),
                new ParameterFormatted(MESSAGE_ID, "CO_POTENTIOMETER", "CO potentiometer", 17, 1, 0.019608, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_VOLTS, false), //TO BE DETERMINTED
                new ParameterFormatted(MESSAGE_ID, "OXYGEN_SENSOR", "Oxygen sensor (integrator)", 18, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%d", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_20", "Flag 20", 19, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "IAC_POSITION", "IAC position", 20, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterFormatted(MESSAGE_ID, "TARGET_IDLE", "Target idleTimeout", 21, 1, 12.5, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_RPM, false),
                new ParameterFormatted(MESSAGE_ID, "BAROMETER", "Barometer", 22, 1, 0.00781, 0.08, "%5.2f", Parameter.UNIT_BAR, false),
                new ParameterFormatted(MESSAGE_ID, "MANIFOLD_PRESSURE_ABS", "Manifold press (ABS)", 23, 1, 0.00781, 0.08, "%5.2f", Parameter.UNIT_BAR, false),
                new ParameterFormatted(MESSAGE_ID, "FLAG_25", "Flag 25", 24, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterFormatted(MESSAGE_ID, "BATTERY_VOLTAGE", "Battery voltage", 25, 1, 0.1, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_VOLTS, false),
                new ParameterFormatted(MESSAGE_ID, "INJECTOR_PULSE_WIDTH", "Injector pulse width", 26, 2, 0.007629, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_MSEC, false),
                new ParameterFormatted(MESSAGE_ID, "OXYGEN_SENSOR_INTEGRATOR", "Oxygen sensor (integrator)", 28, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%d", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_30", "Flag 30", 29, "Throttle > 0", ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, "Throttle = 0", ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, "At idleTimeout"),
                new ParameterFormatted(MESSAGE_ID, "ENGINE_RUN_TIME", "Engine run time", 30, 2, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_SECONDS, false),
                new ParameterFormatted(MESSAGE_ID, "AIR_FUEL_RATIO", "Air/fuel ratio", 32, 1, 0.1, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_34", "Flag 34", 33, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_35", "Flag 35", 34, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "INPUT_FLAG", "Input flags", 35, ParameterBitwise8.BIT_1, "Steering load normal", ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, "CAS pulse", ParameterBitwise8.BIT_7, "AC clutch"),
                new ParameterFormatted(MESSAGE_ID, "VOLTAGE_COMP", "Voltage comp???", 36, 1, 0.05, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_VOLTS, false),
                new ParameterBitwise8(MESSAGE_ID, "OPERATION_MODE_FLAGS", "Operation mode flags", 37, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, "Deceleration mode", "Acceleration mode", ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_39", "Flag 39", 38, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, "Air control sol.?", "Overboost", ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "AIR_FUEL_MODE", "Air fuel mode", 39, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, "Engine running", "Cut fuel", ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "WASTEGATE_DUTY_CYCLE", "Wastegate duty cycle", 40, 1, 0.390625, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_PERCENT, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_42", "Flag 42", 41, "Closed loop", ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, "Overrun fuel cut", ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_34", "Flag 34", 42, ParameterBitwise8.BIT_1, "Fuel cut", "Engine running???", "Start mode?", ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, "Start mode?", ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "KNOCK_COUNTER", "Knock counter", 43, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterFormatted(MESSAGE_ID, "KNOCK_RETARD", "Knock retard", 44, 1, 0.176470588235294, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_DEGREES, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_46", "Flag 46", 45, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "MAT", "MAT", 46, 1, -0.549, 130.0, "%5.1f", Parameter.UNIT_DEGREE_C, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_48", "Flag 48", 47, ParameterBitwise8.BIT_1, "0% Throttle", ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, "Stalled", ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_49", "Flag 49", 48, ParameterBitwise8.BIT_1, "Coolant over 50°C", ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_50", "Flag 50", 49, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_51", "Flag 51", 50, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_52", "Flag 52", 51, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, "MAT disconnected", ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "FLAG_53", "Flag 53", 52, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_54", "Flag 54", 53, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_55", "Flag 55", 54, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "FLAG_56", "Flag 56", 55, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_NONE, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_57", "Flag 57", 56, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_58", "Flag 58", 57, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_59", "Flag 59", 58, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_60", "Flag 60", 59, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterFormatted(MESSAGE_ID, "OP_TEMP", "Operation temp", 60, 1, Parameter.MULTIPLIER_DEFAULT, Parameter.OFFSET_DEFAULT, "%5.1f", Parameter.UNIT_DEGREE_C, false),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_62", "Flag 46", 61, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
                new ParameterBitwise8(MESSAGE_ID, "FLAG_63", "Flag 46", 62, ParameterBitwise8.BIT_1, ParameterBitwise8.BIT_2, ParameterBitwise8.BIT_3, ParameterBitwise8.BIT_4, ParameterBitwise8.BIT_5, ParameterBitwise8.BIT_6, ParameterBitwise8.BIT_7, ParameterBitwise8.BIT_8),
        };




    }


}
