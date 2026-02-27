package me.themfcraft.rpengine.character;

import java.util.UUID;

public record CharacterData(
    UUID id,
    String firstName,
    String lastName,
    int age,
    String gender,
    String backstory
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
