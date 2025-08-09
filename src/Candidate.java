public class Candidate {
    private String candidateId;
    private String name;
    private int voteCount;

    public Candidate(String candidateId, String name) {
        this.candidateId = candidateId;
        this.name = name;
        this.voteCount = 0;
    }

    // Getters
    public String getCandidateId() {
        return candidateId;
    }

    public String getName() {
        return name;
    }

    public int getVoteCount() {
        return voteCount;
    }

    // Method to increment the vote count
    public void incrementVoteCount() {
        this.voteCount++;
    }

    // --- NEW: Override the toString() method ---
    @Override
    public String toString() {
        return this.name; // This will make the JComboBox display the name
    }
}