// src/VotingManagementSystem.java
import java.io.*;
import java.util.HashMap;
import java.util.Map;



public class VotingManagementSystem {
    private Map<String, Voter> voters;
    private Map<String, Candidate> candidates;
    private final String VOTERS_FILE = "voters.txt";
    private final String CANDIDATES_FILE = "candidates.txt";
    private final int MIN_VOTING_AGE = 18;

    public VotingManagementSystem() {
        this.voters = new HashMap<>();
        this.candidates = new HashMap<>();
        loadData();
    }

    // New methods for file I/O
    private void loadData() {
        loadVoters();
        loadCandidates();
    }

    public void saveAllData() {
        saveVoters();
        saveCandidates();
    }
    
    private void saveVoters() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTERS_FILE))) {
            for (Voter voter : voters.values()) {
                writer.println(voter.getVoterId() + "," + voter.getName() + "," + voter.getAge() + "," + voter.hasVoted());
            }
            System.out.println("Voter data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving voter data: " + e.getMessage());
        }
    }

    private void loadVoters() {
        voters.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(VOTERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String voterId = parts[0];
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    boolean hasVoted = Boolean.parseBoolean(parts[3]);

                    Voter voter = new Voter(voterId, name, age);
                    voter.setHasVoted(hasVoted);
                    voters.put(voterId, voter);
                }
            }
            System.out.println("Voter data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Voters file not found. Starting with no voters.");
        } catch (IOException e) {
            System.err.println("Error loading voter data: " + e.getMessage());
        }
    }

    private void saveCandidates() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATES_FILE))) {
            for (Candidate candidate : candidates.values()) {
                writer.println(candidate.getCandidateId() + "," + candidate.getName() + "," + candidate.getVoteCount());
            }
            System.out.println("Candidate data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving candidate data: " + e.getMessage());
        }
    }

    private void loadCandidates() {
        candidates.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String candidateId = parts[0];
                    String name = parts[1];
                    int voteCount = Integer.parseInt(parts[2]);

                    Candidate candidate = new Candidate(candidateId, name);
                    for (int i = 0; i < voteCount; i++) {
                         candidate.incrementVoteCount();
                    }
                    
                    candidates.put(candidateId, candidate);
                }
            }
            System.out.println("Candidate data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Candidates file not found. Starting with no candidates.");
        } catch (IOException e) {
            System.err.println("Error loading candidate data: " + e.getMessage());
        }
    }
    
    // ----- Voter Management -----
    public boolean registerVoter(String voterId, String name, int age) {
        if (voters.containsKey(voterId)) {
            return false;
        }
        if (age < MIN_VOTING_AGE) {
            return false;
        }
        Voter newVoter = new Voter(voterId, name, age);
        voters.put(voterId, newVoter);
        saveVoters();
        return true;
    }

    // ----- Candidate Management -----
    public boolean registerCandidate(String candidateId, String name) {
        if (candidates.containsKey(candidateId)) {
            return false;
        }
        Candidate newCandidate = new Candidate(candidateId, name);
        candidates.put(candidateId, newCandidate);
        saveCandidates();
        return true;
    }

    // ----- Voting Logic -----
    public boolean castVote(String voterId, String candidateId) {
        Voter voter = voters.get(voterId);
        Candidate candidate = candidates.get(candidateId);

        if (voter == null || candidate == null || voter.hasVoted()) {
            return false;
        }

        candidate.incrementVoteCount();
        voter.setHasVoted(true);
        saveAllData();
        return true;
    }

    // ----- Result Display -----
    public Map<String, Candidate> getCandidates() {
        return candidates;
    }
    
    public Map<String, Voter> getVoters() {
        return voters;
    }
}