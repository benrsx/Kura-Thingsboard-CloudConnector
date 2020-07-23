package fr.edf.rd.kura.cloudconnection.thingsboard.cloud;

public enum Qos {

    QOS0(0),
    QOS1(1),
    QOS2(2);

    private final int value;

    private Qos(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Qos valueOf(final int i) {
        if (i == 0) {
            return QOS0;
        } else if (i == 1) {
            return QOS1;
        } else if (i == 2) {
            return QOS2;
        }

        throw new IllegalArgumentException();
    }

}
