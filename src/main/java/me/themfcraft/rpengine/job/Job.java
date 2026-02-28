package me.themfcraft.rpengine.job;

import java.util.List;

public record Job(String id, String name, List<Rank> ranks) {

    public Rank getRank(String rankId) {
        return ranks.stream().filter(r -> r.id().equals(rankId)).findFirst().orElse(null);
    }
}
