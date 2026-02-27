package me.themfcraft.rpengine.job;

import java.util.Map;

public record Job(String id, String displayName, Map<String, Rank> ranks) {

    public Rank getRank(String rankId) {
        return ranks.get(rankId);
    }
}
