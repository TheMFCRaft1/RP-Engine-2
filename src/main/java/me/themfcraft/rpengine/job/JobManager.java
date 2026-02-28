package me.themfcraft.rpengine.job;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.server.level.ServerPlayer;

public class JobManager {

    private final Map<String, Job> registeredJobs = new HashMap<>();
    private final Map<UUID, PlayerJobData> playerJobs = new HashMap<>();

    public void registerJob(Job job) {
        registeredJobs.put(job.id(), job);
    }

    public synchronized void createJob(String id, String name) {
        Job job = new Job(id, name, new ArrayList<>());
        registerJob(job);
        saveJob(job);
    }

    public synchronized void addRank(String jobId, String rankId, String rankName, int salary) {
        Job job = getJob(jobId);
        if (job != null) {
            Rank rank = new Rank(rankId, rankName, salary);
            job.ranks().add(rank);
            saveRank(jobId, rank);
        }
    }

    public Job getJob(String id) {
        return registeredJobs.get(id);
    }

    public void setPlayerJob(UUID uuid, String jobId, String rankId) {
        Job job = getJob(jobId);
        if (job == null) {
            return;
        }
        Rank rank = job.getRank(rankId);
        if (rank == null) {
            return;
        }

        playerJobs.put(uuid, new PlayerJobData(jobId, rankId, false));
    }

    public PlayerJobData getPlayerJobData(UUID uuid) {
        return playerJobs.get(uuid);
    }

    public boolean toggleDuty(ServerPlayer player) {
        PlayerJobData data = playerJobs.get(player.getUUID());
        if (data == null) {
            return false;
        }

        data.onDuty = !data.onDuty;
        return data.onDuty;
    }

    public CompletableFuture<Void> savePlayerJob(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            PlayerJobData data = playerJobs.get(uuid);
            if (data == null) {
                return;
            }

            String sql = "INSERT OR REPLACE INTO player_jobs (uuid, job_id, rank_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setString(2, data.jobId);
                pstmt.setString(3, data.rankId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> loadPlayerJob(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            String sql = "SELECT job_id, rank_id FROM player_jobs WHERE uuid = ?";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    setPlayerJob(uuid, rs.getString("job_id"), rs.getString("rank_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveJob(Job job) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO jobs (id, name) VALUES (?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, job.id());
                pstmt.setString(2, job.name());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveRank(String jobId, Rank rank) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO job_ranks (job_id, rank_id, name, salary) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, jobId);
                pstmt.setString(2, rank.id());
                pstmt.setString(3, rank.name());
                pstmt.setInt(4, rank.salary());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadAllJobs() {
        try (PreparedStatement pstmtJobs = RPEngine.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM jobs");
             ResultSet rsJobs = pstmtJobs.executeQuery()) {
            while (rsJobs.next()) {
                String id = rsJobs.getString("id");
                String name = rsJobs.getString("name");
                Job job = new Job(id, name, new ArrayList<>());
                
                try (PreparedStatement pstmtRanks = RPEngine.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM job_ranks WHERE job_id = ?")) {
                    pstmtRanks.setString(1, id);
                    ResultSet rsRanks = pstmtRanks.executeQuery();
                    while (rsRanks.next()) {
                        job.ranks().add(new Rank(
                            rsRanks.getString("rank_id"),
                            rsRanks.getString("name"),
                            rsRanks.getInt("salary")
                        ));
                    }
                }
                registerJob(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Job> getRegisteredJobs() {
        return registeredJobs;
    }

    public static class PlayerJobData {

        public String jobId;
        public String rankId;
        public boolean onDuty;

        public PlayerJobData(String jobId, String rankId, boolean onDuty) {
            this.jobId = jobId;
            this.rankId = rankId;
            this.onDuty = onDuty;
        }
    }
}
