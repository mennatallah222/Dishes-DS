package com.dishes.dtos;
import java.util.List;

public class CompanyCreationResult {

    private List<CompanyDTO> createdReps;
    private List<String> skippedMessages;

    public CompanyCreationResult(List<CompanyDTO> createdReps, List<String> skippedMessages) {
        this.createdReps = createdReps;
        this.skippedMessages = skippedMessages;
    }

    public List<CompanyDTO> getCreatedReps() {
        return createdReps;
    }

    public void setCreatedReps(List<CompanyDTO> createdReps) {
        this.createdReps = createdReps;
    }

    public List<String> getSkippedMessages() {
        return skippedMessages;
    }

    public void setSkippedMessages(List<String> skippedMessages) {
        this.skippedMessages = skippedMessages;
    }
}