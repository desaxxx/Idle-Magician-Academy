package org.nandayo;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.nandayo.Utils.HexUtil.parse;

public class PlaceholderManager extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "academy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "yoshii01";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        PlayerData pd = magicianAcademy.getData(player.getName());
        if(params.equalsIgnoreCase("rank_title")) {
            Rank rank = pd.getRank();
            return rank != null ? parse(rank.getTitle()) : null;
        }
        return null;
    }
}
