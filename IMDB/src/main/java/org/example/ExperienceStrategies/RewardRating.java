package org.example.ExperienceStrategies;

import org.example.ExperienceStrategy;

public class RewardRating implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        return 4;
    }
}
