import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class VotingGUI extends JFrame {
    private final VotingManagementSystem votingSystem;
    private final JTabbedPane tabbedPane;

    // Components for Voter Registration
    private JTextField voterIdField;
    private JTextField voterNameField;
    private JTextField voterAgeField;

    // Components for Candidate Registration
    private JTextField candidateIdField;
    private JTextField candidateNameField;

    // Components for Voting
    private JTextField voteVoterIdField;
    private JComboBox<Candidate> candidateComboBox;

    // Components for Results
    private JTextArea resultsTextArea;
    private JButton refreshResultsButton;
    
    // Store the panels to use for tab change detection
    private final JPanel votingPanel;
    private final JPanel resultsPanel;
    private final JPanel candidateRegistrationPanel;
    private final JPanel voterRegistrationPanel;

    public VotingGUI(VotingManagementSystem system) {
        this.votingSystem = system;

        setTitle("Voting Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Initialize panels
        voterRegistrationPanel = createVoterRegistrationPanel();
        candidateRegistrationPanel = createCandidateRegistrationPanel();
        votingPanel = createVotingPanel();
        resultsPanel = createResultsPanel();

        tabbedPane.addTab("Voter Registration", voterRegistrationPanel);
        tabbedPane.addTab("Candidate Registration", candidateRegistrationPanel);
        tabbedPane.addTab("Cast a Vote", votingPanel);
        tabbedPane.addTab("View Results", resultsPanel);

        // Add a ChangeListener to the tabbed pane to handle dynamic updates
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == votingPanel) {
                // Refresh the candidate list when the voting tab is selected
                populateCandidateComboBox();
            }
            if (tabbedPane.getSelectedComponent() == resultsPanel) {
                // Refresh the results when the results tab is selected
                displayResults();
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createVoterRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Voter ID:"));
        voterIdField = new JTextField();
        panel.add(voterIdField);

        panel.add(new JLabel("Name:"));
        voterNameField = new JTextField();
        panel.add(voterNameField);

        panel.add(new JLabel("Age:"));
        voterAgeField = new JTextField();
        panel.add(voterAgeField);

        JButton registerVoterButton = new JButton("Register Voter");
        registerVoterButton.addActionListener(e -> {
            try {
                String id = voterIdField.getText();
                String name = voterNameField.getText();
                String ageText = voterAgeField.getText();

                if (id.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int age = Integer.parseInt(ageText);

                if (votingSystem.registerVoter(id, name, age)) {
                    JOptionPane.showMessageDialog(null, "Voter registered successfully.");
                    voterIdField.setText("");
                    voterNameField.setText("");
                    voterAgeField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to register voter. Check ID uniqueness or age.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(new JLabel()); // Empty cell for spacing
        panel.add(registerVoterButton);

        return panel;
    }
    
    private JPanel createCandidateRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Candidate ID:"));
        candidateIdField = new JTextField();
        panel.add(candidateIdField);

        panel.add(new JLabel("Name:"));
        candidateNameField = new JTextField();
        panel.add(candidateNameField);

        JButton registerCandidateButton = new JButton("Register Candidate");
        registerCandidateButton.addActionListener(e -> {
            String id = candidateIdField.getText();
            String name = candidateNameField.getText();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (votingSystem.registerCandidate(id, name)) {
                JOptionPane.showMessageDialog(null, "Candidate registered successfully.");
                candidateIdField.setText("");
                candidateNameField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to register candidate. Check ID uniqueness.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(new JLabel());
        panel.add(registerCandidateButton);

        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Voter ID:"));
        voteVoterIdField = new JTextField();
        panel.add(voteVoterIdField);

        panel.add(new JLabel("Select Candidate:"));
        candidateComboBox = new JComboBox<>();
        populateCandidateComboBox(); // Initial population
        panel.add(candidateComboBox);

        JButton castVoteButton = new JButton("Cast Vote");
        castVoteButton.addActionListener(e -> {
            String voterId = voteVoterIdField.getText();
            Candidate selectedCandidate = (Candidate) candidateComboBox.getSelectedItem();

            if (voterId.isEmpty()) {
                 JOptionPane.showMessageDialog(null, "Please enter a Voter ID.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            if (selectedCandidate == null) {
                JOptionPane.showMessageDialog(null, "No candidate selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (votingSystem.castVote(voterId, selectedCandidate.getCandidateId())) {
                JOptionPane.showMessageDialog(null, "Vote cast successfully.");
                voteVoterIdField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to cast vote. Check your Voter ID or if you have already voted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(new JLabel());
        panel.add(castVoteButton);

        return panel;
    }
    
    private void populateCandidateComboBox() {
        candidateComboBox.removeAllItems();
        for (Candidate c : votingSystem.getCandidates().values()) {
            candidateComboBox.addItem(c);
        }
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshResultsButton = new JButton("Refresh Results");
        refreshResultsButton.addActionListener(e -> displayResults());
        panel.add(refreshResultsButton, BorderLayout.SOUTH);

        displayResults(); // Initial display
        return panel;
    }

    private void displayResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Voting Results ---\n");
        int maxVotes = -1;
        String winnerName = "No winner (tie)";
        Map<String, Candidate> candidates = votingSystem.getCandidates();

        if (candidates.isEmpty()) {
            sb.append("No candidates have been registered yet.\n");
        } else {
            for (Candidate candidate : candidates.values()) {
                sb.append(String.format("%s: %d votes\n", candidate.getName(), candidate.getVoteCount()));
                if (candidate.getVoteCount() > maxVotes) {
                    maxVotes = candidate.getVoteCount();
                    winnerName = candidate.getName();
                } else if (candidate.getVoteCount() == maxVotes) {
                    winnerName = "Tie";
                }
            }
        }

        sb.append("\n----------------------\n");
        if (maxVotes > 0) {
            sb.append("Winner: ").append(winnerName).append("\n");
        } else if (!candidates.isEmpty()) {
             sb.append("No votes have been cast yet.\n");
        }
        
        sb.append("\nTotal Registered Voters: ").append(votingSystem.getVoters().size()).append("\n");

        resultsTextArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VotingManagementSystem system = new VotingManagementSystem();
            // Optional: Register some initial candidates for demonstration
            // system.registerCandidate("C1", "John Doe");
            // system.registerCandidate("C2", "Jane Smith");
            new VotingGUI(system);
        });
    }
}