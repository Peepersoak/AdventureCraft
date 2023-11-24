package com.peepersoak.adventurecraftcore.openAI;

import org.bukkit.configuration.file.FileConfiguration;

public class QuestSetting {
    private final int commonDuration;
    private final int uncommonDuration;
    private final int rareDuration;
    private final int epicDuration;
    private final int legendaryDuration;
    private final int mythicalDuration;
    private final int fabledDuration;
    private final int godlikeDuration;
    private final int ascendedDuration;



    public QuestSetting(FileConfiguration config) {
        String startingPath = "Quest_Settings.";
        String durationPath = startingPath + "Rank_Requirement.";

        commonDuration = config.getInt(durationPath + "Common");
        uncommonDuration = config.getInt(durationPath + "UnCommon");
        rareDuration = config.getInt(durationPath + "Rare");
        epicDuration = config.getInt(durationPath + "Epic");
        legendaryDuration = config.getInt(durationPath + "Legendary");
        mythicalDuration = config.getInt(durationPath + "Mythical");
        fabledDuration = config.getInt(durationPath + "Fabled");
        godlikeDuration = config.getInt(durationPath + "Godlike");
        ascendedDuration = config.getInt(durationPath + "Ascended");
    }

    public int getCommonDuration() {
        return commonDuration;
    }

    public int getUncommonDuration() {
        return uncommonDuration;
    }

    public int getRareDuration() {
        return rareDuration;
    }

    public int getEpicDuration() {
        return epicDuration;
    }

    public int getLegendaryDuration() {
        return legendaryDuration;
    }

    public int getMythicalDuration() {
        return mythicalDuration;
    }

    public int getFabledDuration() {
        return fabledDuration;
    }

    public int getGodlikeDuration() {
        return godlikeDuration;
    }

    public int getAscendedDuration() {
        return ascendedDuration;
    }
}
