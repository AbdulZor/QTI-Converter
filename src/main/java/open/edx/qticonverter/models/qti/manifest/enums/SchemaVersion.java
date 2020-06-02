package open.edx.qticonverter.models.qti.manifest.enums;

public enum SchemaVersion {
    V10(1.0), V11(1.1);

    private double numVal;

    SchemaVersion(double numVal) {
        this.numVal = numVal;
    }

    public double getNumVal() {
        return numVal;
    }
}
