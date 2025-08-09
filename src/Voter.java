// src/Voter.java
public class Voter {
    private String voterId;
    private String name;
    private int age;
    private boolean hasVoted;

    public Voter(String voterId, String name, int age) {
        this.voterId = voterId;
        this.name = name;
        this.age = age;
        this.hasVoted = false;
    }

    // Getters
    public String getVoterId() {
        return voterId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    // Setter to mark that the voter has voted
    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}