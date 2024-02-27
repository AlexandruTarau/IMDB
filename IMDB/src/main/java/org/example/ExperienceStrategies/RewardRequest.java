package org.example.ExperienceStrategies;

import org.example.ExperienceStrategy;

public class RewardRequest implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        return 7;
    }
}