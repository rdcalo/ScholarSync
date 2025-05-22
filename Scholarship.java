package scholar;

public class Scholarship {
    private String title;
    private String startDate;
    private String endDate;
    private int acceptedCount;
    private int applicantCount; // <-- NEW FIELD

    public Scholarship(String title, String startDate, String endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.acceptedCount = 0;
        this.applicantCount = 0; // <-- INIT
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public int getApplicantCount() { // <-- NEW GETTER
        return applicantCount;
    }

    public void incrementAccepted() {
        acceptedCount++;
    }

    public void incrementApplicants() { // <-- NEW METHOD
        applicantCount++;
    }

    public boolean isFull() {
        return acceptedCount >= 5;
    }
}
