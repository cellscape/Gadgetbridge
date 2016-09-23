package nodomain.freeyourgadget.gadgetbridge.devices.angel;

import java.util.UUID;

public class AngelService {

    public static final UUID UUID_SERVICE_HEALTH_JOURNAL = UUID.fromString("87ef07ff-4739-4527-b38f-b0e228de6ed3");

    public static final UUID UUID_CHARACTERISTIC_HEALTH_JOURNAL_ENTRY = UUID.fromString("8b713a94-070a-4743-a695-fc58cb3f236b");
    public static final UUID UUID_CHARACTERISTIC_HEALTH_JOURNAL_CONTROL_POINT = UUID.fromString("5ae61782-4a65-4202-a4da-db73406e38e8");

    public static final byte OP_CODE_QUERY = 1;
    public static final byte OP_CODE_DELETE = 2;
    public static final byte OP_CODE_ABORT = 3;
    public static final byte OP_CODE_QUERY_NUMBER_OF_ENTRIES = 4;

    public static final byte OPERATOR_ALL_ENTRIES = 0;
    public static final byte OPERATOR_FIRST_ENTRY = 1;
    public static final byte OPERATOR_LAST_ENTRY = 2;
    public static final byte OPERATOR_LESS_EQUAL = 3;
    public static final byte OPERATOR_GREATER_EQUAL = 4;
    public static final byte OPERATOR_WITHIN_RANGE = 5;
}
