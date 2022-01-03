package camunda;

public class BigObject {
    private String bigBigBigValue = "I am too big to fail! ".repeat(1000);

    public String getBigBigBigValue() {
        return bigBigBigValue;
    }

    public void setBigBigBigValue(String bigBigBigValue) {
        this.bigBigBigValue = bigBigBigValue;
    }
}
