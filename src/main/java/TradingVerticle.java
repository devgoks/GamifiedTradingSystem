import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.JsonObject;
import java.math.BigDecimal;
import java.util.*;

public class TradingVerticle extends AbstractVerticle {

    private final TradingSystem tradingSystem = new TradingSystem();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ctx -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.next();
        });

        router.post("/user").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            String username = body.getString("username");
            User user = tradingSystem.createUser(username);
            ctx.response().end(JsonObject.mapFrom(user).encode());
        });

        router.post("/portfolio/add-asset").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            int userId = body.getInteger("userId");
            int assetId = body.getInteger("assetId");
            String name = body.getString("name");
            int quantity = body.getInteger("quantity");
            double price = body.getDouble("price");

            BigDecimal priceBigDecimal = BigDecimal.valueOf(price);

            Portfolio portfolio = tradingSystem.getPortfolio(userId);
            if(portfolio==null){
                ctx.response().setStatusCode(400).end("User does not exist.");
                return;
            }
            Asset existingAsset = portfolio.getAssets().stream()
                .filter(asset -> asset.getAssetId() == assetId)
                .findFirst()
                .orElse(null);

            if (existingAsset == null) {
                Asset newAsset = new Asset(assetId, name, quantity, priceBigDecimal);
                tradingSystem.assetsMap.put(assetId, newAsset);
                portfolio.addAsset(newAsset);
                ctx.response().setStatusCode(201).end("Asset added successfully.");
            } else {
                ctx.response().setStatusCode(400).end("Asset already exists. Use trade to update it.");
            }
        });

        router.post("/portfolio/remove-asset").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            int userId = body.getInteger("userId");
            int assetId = body.getInteger("assetId");

            Portfolio portfolio = tradingSystem.getPortfolio(userId);
            if(portfolio==null){
                ctx.response().setStatusCode(400).end("User does not exist.");
                return;
            }
            Asset asset = portfolio.getAssets().stream()
                .filter(a -> a.getAssetId() == assetId)
                .findFirst()
                .orElse(null);

            if (asset != null) {
                portfolio.removeAsset(assetId);
                ctx.response().setStatusCode(200).end("Asset removed successfully.");
            } else {
                ctx.response().setStatusCode(404).end("Asset not found in the portfolio.");
            }
        });

        router.get("/user/:userId").handler(ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            User user = tradingSystem.getUser(userId);
            ctx.response().end(JsonObject.mapFrom(user).encode());
        });

        router.post("/user/fund-balance").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            int userId = body.getInteger("userId");
            double amount = body.getDouble("amount");

            User user = tradingSystem.getUser(userId);

            if(user==null){
                ctx.response().setStatusCode(400).end("User does not exist.");
                return;
            }

            BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);
            user.setBalance(amountBigDecimal);

            ctx.response().setStatusCode(200).end("Balance funded successfully.");
        });

        router.get("/portfolio/:userId").handler(ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            Portfolio portfolio = tradingSystem.getPortfolio(userId);
            if(portfolio==null){
                ctx.response().setStatusCode(400).end("User does not exist.");
                return;
            }
            ctx.response().end(JsonObject.mapFrom(portfolio).encode());
        });


        router.post("/trade").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            int userId = body.getInteger("userId");
            int assetId = body.getInteger("assetId");
            int quantity = body.getInteger("quantity");
            boolean isBuy = body.getBoolean("isBuy");
            try {
                tradingSystem.tradeAsset(userId, assetId, quantity, isBuy);
                ctx.response().setStatusCode(200).end("Trade successful");
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).end(e.getMessage());
            }
        });

        router.get("/leaderboard").handler(ctx -> {
            int topN = Integer.parseInt(ctx.queryParam("topN").get(0));
            List<User> leaderboard = tradingSystem.getLeaderboard(topN);
            ctx.response().end(Json.encode(leaderboard));
        });

        router.get("/insights").handler(ctx -> {
            User topUser = tradingSystem.getUserWithHighestPortfolioValue();
            JsonObject insights = new JsonObject()
                .put("highestPortfolioValueUser", topUser != null ? topUser.getUsername() : "None")
                .put("mostTradedAsset", tradingSystem.getMostTradedAssets(10));
            ctx.response().end(insights.encode());
        });

        vertx.setPeriodic(60000, id -> {
            tradingSystem.updateAssetPrices();
            System.out.println("Assets Prices updated periodically.");
        });

        vertx.setPeriodic(5000, id -> {
            tradingSystem.updateLeaderboard();
            System.out.println("Leaderboard updated periodically.");
        });

        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }
}