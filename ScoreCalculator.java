package scholar;

public class ScoreCalculator {
    // Weight constants for different criteria
    private static final double GWA_WEIGHT = 0.5;  // 50% weight for GWA
    private static final double INCOME_WEIGHT = 0.3;  // 30% weight for family income
    private static final double STRAND_WEIGHT = 0.2;  // 20% weight for academic strand

    // Score ranges
    private static final double MAX_SCORE = 100.0;
    private static final double MIN_SCORE = 0.0;

    /**
     * Calculate the overall priority score for a scholarship applicant
     * @param user The user/applicant to calculate score for
     * @return A normalized score between 0 and 100
     */
    public static double calculatePriorityScore(User user) {
        double gwaScore = calculateGWAScore(user.getGpa());
        double incomeScore = calculateIncomeScore(user.getMonthlyIncome());
        double strandScore = calculateStrandScore(user.getTrackStrand());

        // Weighted sum of all scores
        double totalScore = (gwaScore * GWA_WEIGHT) +
                          (incomeScore * INCOME_WEIGHT) +
                          (strandScore * STRAND_WEIGHT);

        // Normalize to ensure score is between 0 and 100
        return Math.min(Math.max(totalScore, MIN_SCORE), MAX_SCORE);
    }

    /**
     * Calculate score based on GWA (Grade Weighted Average)
     * Assumes GWA is on a 100-point scale
     */
    private static double calculateGWAScore(double gwa) {
        // Direct mapping for GWA (assuming it's already on a 100-point scale)
        // Higher GWA = Higher Score
        return gwa;
    }

    /**
     * Calculate score based on monthly family income
     * Lower income gets higher score to prioritize need-based selection
     */
    private static double calculateIncomeScore(String monthlyIncome) {
        // Score mapping based on income brackets
        switch (monthlyIncome) {
            case "< ₱10,000":
                return 100.0;  // Highest priority for lowest income
            case "< ₱30,000":
                return 80.0;
            case "< ₱70,000":
                return 60.0;
            case "< ₱100,000":
                return 40.0;
            case "₱100,000 above":
                return 20.0;
            default:
                return 0.0;
        }
    }

    /**
     * Calculate score based on academic strand
     * Prioritizes certain strands based on scholarship requirements
     */
    private static double calculateStrandScore(String trackStrand) {
        // Example scoring for different strands
        // This can be customized based on specific scholarship requirements
        switch (trackStrand.toUpperCase()) {
            case "STEM":
                return 100.0;  // Highest priority for STEM
            case "ABM":
                return 90.0;
            case "HUMSS":
                return 85.0;
            case "GAS":
                return 80.0;
            case "TVL":
                return 75.0;
            default:
                return 70.0;
        }
    }

    /**
     * Get a qualitative assessment of the score
     */
    public static String getScoreAssessment(double score) {
        if (score >= 90) {
            return "Excellent Candidate";
        } else if (score >= 80) {
            return "Strong Candidate";
        } else if (score >= 70) {
            return "Good Candidate";
        } else if (score >= 60) {
            return "Fair Candidate";
        } else {
            return "Needs Review";
        }
    }
} 