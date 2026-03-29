package master;

import common.Request;
import common.RequestType;
import common.Response;
import common.WorkerInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterDispatcher {
    private final HashRouter hashRouter;
    private final WorkerRegistry workerRegistry;
    private final WorkerClient workerClient;
    private final ReducerClient reducerClient;

    public MasterDispatcher(HashRouter hashRouter,
                            WorkerRegistry workerRegistry,
                            WorkerClient workerClient,
                            ReducerClient reducerClient) {
        this.hashRouter = hashRouter;
        this.workerRegistry = workerRegistry;
        this.workerClient = workerClient;
        this.reducerClient = reducerClient;
    }

    public Response dispatch(Request request) {
        if (request == null || request.getType() == null) {
            return new Response(false, "Invalid request");
        }

        return switch (request.getType()) {
            case ADD_GAME -> routeToGameOwner(request);
            case REMOVE_GAME -> routeByGameName(request.getGameName(), request);
            case UPDATE_GAME_RISK -> routeByGameName(request.getGameName(), request);
            case UPDATE_GAME_BET_LIMITS -> routeByGameName(request.getGameName(), request);
            case GET_PROVIDER_STATS -> reduceFromWorkers(request, RequestType.MAP_PROVIDER_STATS);
            case GET_PLAYER_STATS -> reduceFromWorkers(request, RequestType.MAP_PLAYER_STATS);
            case HEALTH_CHECK -> new Response(true, "MASTER_OK");
            default -> new Response(false, "Unsupported request type for master: " + request.getType());
        };
    }

    private Response routeToGameOwner(Request request) {
        if (request.getGameInfo() == null || request.getGameInfo().getGameName() == null) {
            return new Response(false, "Game info or game name is missing");
        }
        return routeByGameName(request.getGameInfo().getGameName(), request);
    }

    private Response routeByGameName(String gameName, Request request) {
        try {
            WorkerInfo targetWorker = hashRouter.routeByGameName(gameName);
            return workerClient.sendRequest(targetWorker, request);
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
    }


    private Response reduceFromWorkers(Request request, RequestType reduceType) {
        List<Response> mapResponses = workerClient.broadcast(workerRegistry.getWorkers(), request);
        Map<String, Double> mergedPartials = new LinkedHashMap<>();

        for (Response mapResponse : mapResponses) {
            if (!mapResponse.isSuccess()) {
                return new Response(false, "Worker map phase failed: " + mapResponse.getMessage());
            }
            accumulate(mergedPartials, mapResponse.getTotals());
        }

        Request reducerRequest = reduceType == RequestType.MAP_PROVIDER_STATS
                ? Request.providerMapPayload(request.getProviderName(), mergedPartials)
                : Request.playerMapPayload(request.getPlayerId(), mergedPartials);

        return reducerClient.reduce(reducerRequest);
    }

    private void accumulate(Map<String, Double> mergedPartials, Map<String, Double> totals) {
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            mergedPartials.merge(entry.getKey(), entry.getValue(), Double::sum);
        }
    }
}
