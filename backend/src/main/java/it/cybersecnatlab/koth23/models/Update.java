package it.cybersecnatlab.koth23.models;

import org.json.JSONObject;

public interface Update {
    JSONObject getUpdateData();

    Type getUpdateType();

    enum Type {
        Tick("tick"),
        Move("move"),
        UseAbility("ability"),
        VMEntityDeath("vmDeath"),
        SummonerDeath("summonerDeath"),
        SummonerSummon("summon"),
        TakeDamage("damage"),
        LifeRecovery("lifeRecovery"),
        ManaUsage("manaUsage"),
        ManaRecovery("manaRecovery"),
        ZoneReduce("zoneReduce"),
        Scoreboard("scoreboard"),
        BackgroundTileChange("bgTile");

        public final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
