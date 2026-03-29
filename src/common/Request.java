package common;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request implements Serializable {
    private final RequestType type;
    private final GameInfo gameInfo;
    private final String gameName;
    private final String providerName;
    private final String playerId;
    private final String riskLevel;
    private final Map<String, Double> partialTotals;

    public Request(RequestType type, GameInfo gameInfo, String gameName, String providerName,
                   String playerId, String riskLevel, Map<String, Double> partialTotals) {
        this.type = type;
        this.gameInfo = gameInfo;
        this.gameName = gameName;
        this.providerName = providerName;
        this.playerId = playerId;
        this.riskLevel = riskLevel;
        this.partialTotals = partialTotals == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(partialTotals));
    }

    public static Request addGame(GameInfo gameInfo) {
        return new Request(RequestType.ADD_GAME, gameInfo, null, null, null, null, null);
    }

    public static Request removeGame(String gameName) {
        return new Request(RequestType.REMOVE_GAME, null, gameName, null, null, null, null);
    }

    public static Request updateGameRisk(String gameName, String riskLevel) {
        return new Request(RequestType.UPDATE_GAME_RISK, null, gameName, null, null, riskLevel, null);
    }

    public static Request providerStats(String providerName) {
        return new Request(RequestType.GET_PROVIDER_STATS, null, null, providerName, null, null, null);
    }

    public static Request playerStats(String playerId) {
        return new Request(RequestType.GET_PLAYER_STATS, null, null, null, playerId, null, null);
    }

    public static Request providerMapPayload(String providerName, Map<String, Double> partialTotals) {
        return new Request(RequestType.MAP_PROVIDER_STATS, null, null, providerName, null, null, partialTotals);
    }

    public static Request playerMapPayload(String playerId, Map<String, Double> partialTotals) {
        return new Request(RequestType.MAP_PLAYER_STATS, null, null, null, playerId, null, partialTotals);
    }

    public static Request healthCheck() {
        return new Request(RequestType.HEALTH_CHECK, null, null, null, null, null, null);
    }

    public RequestType getType() {
        return type;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public String getGameName() {
        return gameName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public Map<String, Double> getPartialTotals() {
        return partialTotals;
    }
}
