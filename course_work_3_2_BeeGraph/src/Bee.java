
public class Bee {
    public int status; // 0 - inactive; 1 - active; 2 - scout
    public Graph memorySituation;
    public int measureOfQuality;
    public int numberOfVisits;

        public Bee(int status, Graph memorySituation,
                   int measureOfQuality, int numberOfVisits) {
            this.status = status;
            this.memorySituation = memorySituation;
            this.measureOfQuality = measureOfQuality;
            this.numberOfVisits = numberOfVisits;
        }
        @Override
        public String toString() {
            String s = "";
            s += "Status = " + this.status + "\n";
            s += " Quality = " + this.measureOfQuality;
            s += " Number visits = " + this.numberOfVisits;
            return s;
        }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Graph getMemorySituation() {
        return memorySituation;
    }

    public void setMemorySituation(Graph memorySituation) {
        this.memorySituation = memorySituation;
    }

    public int getMeasureOfQuality() {
        return measureOfQuality;
    }

    public void setMeasureOfQuality(int measureOfQuality) {
        this.measureOfQuality = measureOfQuality;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

}
