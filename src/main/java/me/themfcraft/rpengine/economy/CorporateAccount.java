package me.themfcraft.rpengine.economy;

import java.util.List;
import java.util.UUID;

public class CorporateAccount {

    private final int id;
    private final String name;
    private double balance;
    private final UUID ownerUuid;
    private final List<UUID> members;

    public CorporateAccount(int id, String name, double balance, UUID ownerUuid, List<UUID> members) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.ownerUuid = ownerUuid;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid) {
        if (!members.contains(uuid)) {
            members.add(uuid);
        }
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isMember(UUID uuid) {
        return ownerUuid.equals(uuid) || members.contains(uuid);
    }

    public String getMembersString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            sb.append(members.get(i).toString());
            if (i < members.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
