import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import response.AssetDetailsDTO;

public class TradingSystem {
    final Map<Integer, User> users;
    final Map<Integer, Portfolio> portfolios;
    final Map<Integer, Integer> tradeCounts;
    private List<User> leaderBoard;
    private int lastTradeUserId;
    final Map<Integer, Integer> assetTradeCounts;

    final Map<Integer, Asset> assetsMap;

    private static final int PERCENT_RANGE = 2;


    public TradingSystem() {
        this.users = new HashMap<>();
        this.portfolios = new HashMap<>();
        this.tradeCounts = new HashMap<>();
        this.lastTradeUserId = 0;
        this.leaderBoard = new ArrayList<>();
        this.assetTradeCounts = new HashMap<>();
        this.assetsMap =  new HashMap<>();
    }

    public User createUser(String username) {
        User user = new User(username);
        users.put(user.getUserId(), user);
        portfolios.put(user.getUserId(), new Portfolio());
        tradeCounts.put(user.getUserId(), 0);
        return user;
    }

    public Portfolio getPortfolio(int userId) {
        return portfolios.get(userId);
    }

    public User getUser(int userId) {
        return users.get(userId);
    }

    public void tradeAsset(int userId, int assetId, int quantity, boolean isBuy) {

        User user = users.get(userId);

        if(user == null){
            throw new IllegalArgumentException("User does not exist.");
        }

        Asset asset = assetsMap.get(assetId);

        if(asset == null) {
            throw new IllegalArgumentException("Asset does not exist.");
        }

        Portfolio userPortfolio = portfolios.get(userId);
        Asset userAsset = userPortfolio.getAssets().stream()
            .filter(a -> a.getAssetId() == assetId).findFirst().orElse(null);

        BigDecimal totalCost = asset.getPrice().multiply(BigDecimal.valueOf(quantity));

        if (isBuy) {
            if (user.getBalance().compareTo(totalCost) >= 0) {
                user.setBalance(user.getBalance().subtract(totalCost));
                if (userAsset == null) {
                    userAsset = new Asset(assetId, asset.getName(), quantity, asset.getPrice());
                    userPortfolio.addAsset(userAsset);
                } else {
                    userAsset.setQuantity(userAsset.getQuantity() + quantity);
                }
            } else {
                throw new IllegalArgumentException("Insufficient balance to buy asset.");
            }
        } else {
            if (userAsset != null && userAsset.getQuantity() >= quantity) {
                userAsset.setQuantity(userAsset.getQuantity() - quantity);
                if (userAsset.getQuantity() == 0) {
                    userPortfolio.removeAsset(assetId);
                }
                BigDecimal totalSaleAmount = asset.getPrice().multiply(BigDecimal.valueOf(quantity));
                user.setBalance(user.getBalance().add(totalSaleAmount));
            } else {
                throw new IllegalArgumentException("Insufficient quantity to sell.");
            }
        }

        addGem(user, userId);

        assetTradeCounts.put(assetId, assetTradeCounts.getOrDefault(assetId, 0) + 1);
    }

    private void addGem(User user, Integer userId) {
        user.addGems(1);
        if(lastTradeUserId == userId) {
            user.incrementTradingStreak();
        } else {
            user.resetTradingStreak();
            user.incrementTradingStreak();
        }

        if (user.getTradingStreak() % 3 == 0) {
            user.addGems(3);
        }

        int tradeCount = tradeCounts.get(userId) + 1;
        tradeCounts.put(userId, tradeCount);

        if (tradeCount == 5) user.addGems(5);
        if (tradeCount == 10) user.addGems(10);

        lastTradeUserId = userId;
    }

    public void updateLeaderboard() {
        List<User> sortedUsers = new ArrayList<>(users.values());
        sortedUsers.sort((u1, u2) -> Integer.compare(u2.getGemCount(), u1.getGemCount()));

        int rank = 1;
        int previousGemCount = -1;
        for (int i = 0; i < sortedUsers.size(); i++) {
            User user = sortedUsers.get(i);
            if (user.getGemCount() != previousGemCount) {
                rank = i + 1;
            }
            user.setRank(rank);
            previousGemCount = user.getGemCount();
        }

        leaderBoard = sortedUsers;
    }

    public List<User> getLeaderboard(int topN) {
        return leaderBoard.subList(0, Math.min(topN, leaderBoard.size()));
    }

    public User getUserWithHighestPortfolioValue() {
        return users.values().stream()
            .max(Comparator.comparing(user -> portfolios.get(user.getUserId()).getPortfolioValue()))
            .orElse(null);
    }

    public List<AssetDetailsDTO> getMostTradedAssets(int topN) {
        return assetTradeCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(topN)
            .map(entry -> {
                Integer assetId = entry.getKey();
                Integer tradeCount = entry.getValue();
                Asset asset = assetsMap.get(assetId);
                return new AssetDetailsDTO(assetId, asset.getName(), tradeCount);
            })
            .toList();
    }

    public void updateAssetPrices() {
        portfolios.values().forEach(portfolio ->
            portfolio.getAssets().forEach(asset -> {
                BigDecimal price = asset.getPrice();
                BigDecimal randomFactor =
                    BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(PERCENT_RANGE).divide(BigDecimal.valueOf(100)))
                        .add(BigDecimal.valueOf(Math.random())
                            .multiply(BigDecimal.valueOf(PERCENT_RANGE * 2).divide(BigDecimal.valueOf(100))));
                BigDecimal newPrice = price.multiply(randomFactor);
                newPrice = newPrice.setScale(2, RoundingMode.FLOOR);
                asset.setPrice(newPrice);
            })
        );
    }
}
