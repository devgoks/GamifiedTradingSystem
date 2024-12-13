import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import response.AssetDetailsDTO;

import static org.junit.jupiter.api.Assertions.*;

class TradingSystemTest {

    private TradingSystem tradingSystem;

    @BeforeEach
    void setUp() {
        tradingSystem = new TradingSystem();
    }

    @Test
    void testCreateUser() {
        User user = tradingSystem.createUser("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testTradeAssetBuy() {
        User user = tradingSystem.createUser("buyer");
        user.setBalance(BigDecimal.valueOf(1000));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        tradingSystem.tradeAsset(user.getUserId(), 1, 5, true);

        Portfolio portfolio = tradingSystem.getPortfolio(user.getUserId());
        assertEquals(1, portfolio.getAssets().size());
        assertEquals(5, portfolio.getAssets().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(500), user.getBalance());
    }

    @Test
    void testTradeAssetSell() {
        User user = tradingSystem.createUser("seller");
        user.setBalance(BigDecimal.valueOf(1000));

        Asset asset = new Asset(1, "Stock", 10, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);
        Portfolio portfolio = tradingSystem.getPortfolio(user.getUserId());
        portfolio.addAsset(asset);

        tradingSystem.tradeAsset(user.getUserId(), 1, 5, false);

        assertEquals(5, asset.getQuantity());
        assertEquals(BigDecimal.valueOf(1500), user.getBalance());
    }

    @Test
    void testUpdateLeaderboard() {
        User user1 = tradingSystem.createUser("user1");
        User user2 = tradingSystem.createUser("user2");
        User user3 = tradingSystem.createUser("user3");
        User user4 = tradingSystem.createUser("user4");

        user1.addGems(20);
        user2.addGems(15);
        user3.addGems(15);
        user4.addGems(10);

        tradingSystem.updateLeaderboard();

        List<User> leaderboard = tradingSystem.getLeaderboard(4);

        System.out.println(leaderboard.toString());

        assertEquals(4, leaderboard.size());
        assertEquals("user1", leaderboard.get(0).getUsername());
        assertEquals("user2", leaderboard.get(1).getUsername());

        assertEquals(1, tradingSystem.getUser(user1.getUserId()).getRank());
        assertEquals(2, tradingSystem.getUser(user2.getUserId()).getRank());
        assertEquals(2, tradingSystem.getUser(user3.getUserId()).getRank());
        assertEquals(4, tradingSystem.getUser(user4.getUserId()).getRank());
    }

    @Test
    void testGetUserWithHighestPortfolioValue() {
        User user1 = tradingSystem.createUser("user1");
        User user2 = tradingSystem.createUser("user2");

        Portfolio portfolio1 = tradingSystem.getPortfolio(user1.getUserId());
        Portfolio portfolio2 = tradingSystem.getPortfolio(user2.getUserId());

        portfolio1.addAsset(new Asset(1, "Stock", 10, BigDecimal.valueOf(100)));
        portfolio2.addAsset(new Asset(2, "Bond", 5, BigDecimal.valueOf(300)));

        User highestUser = tradingSystem.getUserWithHighestPortfolioValue();
        assertEquals("user2", highestUser.getUsername());
    }

    @Test
    void testGetMostTradedAssets() {
        tradingSystem.assetsMap.put(1, new Asset(1, "Stock", 0, BigDecimal.valueOf(100)));
        tradingSystem.assetsMap.put(2, new Asset(2, "Bond", 0, BigDecimal.valueOf(200)));

        User user = tradingSystem.createUser("trader");
        user.setBalance(BigDecimal.valueOf(2000));

        tradingSystem.tradeAsset(user.getUserId(), 1, 5, true);
        tradingSystem.tradeAsset(user.getUserId(), 1,5, true);
        tradingSystem.tradeAsset(user.getUserId(), 2,  2, true);

        List<AssetDetailsDTO> mostTraded = tradingSystem.getMostTradedAssets(2);
        assertEquals(2, mostTraded.size());
        assertEquals(1, mostTraded.get(0).getAssetId());
        assertEquals(2, mostTraded.get(1).getAssetId());
    }

    @Test
    void testUpdateAssetPrices() {
        Asset asset = new Asset(1, "Stock", 10, BigDecimal.valueOf(100));
        Portfolio portfolio = new Portfolio();
        portfolio.addAsset(asset);
        tradingSystem.portfolios.put(1, portfolio);

        tradingSystem.updateAssetPrices();

        assertNotEquals(BigDecimal.valueOf(100), asset.getPrice(), "Asset price should be updated");
    }

    @Test
    void testTradeAssetUserDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {
            tradingSystem.tradeAsset(999, 1, 5, true);
        });
    }

    @Test
    void testTradeAssetAssetDoesNotExist() {
        User user = tradingSystem.createUser("buyer");
        user.setBalance(BigDecimal.valueOf(1000));

        assertThrows(IllegalArgumentException.class, () -> {
            tradingSystem.tradeAsset(user.getUserId(), 999, 5, true);
        });
    }

    @Test
    void testTradeAssetInsufficientBalanceToBuy() {
        User user = tradingSystem.createUser("buyer");
        user.setBalance(BigDecimal.valueOf(10));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        assertThrows(IllegalArgumentException.class, () -> {
            tradingSystem.tradeAsset(user.getUserId(), 1, 5, true);
        });
    }

    @Test
    void testTradeAssetInsufficientQuantityToSell() {
        User user = tradingSystem.createUser("seller");
        user.setBalance(BigDecimal.valueOf(1000));

        Asset asset = new Asset(1, "Stock", 2, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);
        Portfolio portfolio = tradingSystem.getPortfolio(user.getUserId());
        portfolio.addAsset(asset);

        assertThrows(IllegalArgumentException.class, () -> {
            tradingSystem.tradeAsset(user.getUserId(), 1, 5, false);
        });
    }

    @Test
    void testAddGemPerTrade() {
        User user = tradingSystem.createUser("trader");
        user.setBalance(BigDecimal.valueOf(2000));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        tradingSystem.tradeAsset(user.getUserId(), 1, 5, true);
        assertEquals(1, user.getGemCount());
    }

    @Test
    void testAddGemForThreeConsecutiveTrades() {
        User user = tradingSystem.createUser("trader");
        user.setBalance(BigDecimal.valueOf(2000));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        for (int i = 1; i <= 3; i++) {
            tradingSystem.tradeAsset(user.getUserId(), 1, 5, true);
        }
        assertEquals(6, user.getGemCount());
    }

    @Test
    void testAddGemForFiveTrades() {
        User user = tradingSystem.createUser("trader");
        user.setBalance(BigDecimal.valueOf(2000));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        for (int i = 0; i < 5; i++) {
            tradingSystem.tradeAsset(user.getUserId(), 1, 1, true);
        }
        assertEquals(13, user.getGemCount());
    }

    @Test
    void testAddGemForTenTrades() {
        User user = tradingSystem.createUser("trader");
        user.setBalance(BigDecimal.valueOf(4000));

        Asset asset = new Asset(1, "Stock", 0, BigDecimal.valueOf(100));
        tradingSystem.assetsMap.put(1, asset);

        for (int i = 0; i < 10; i++) {
            tradingSystem.tradeAsset(user.getUserId(), 1, 1, true);
        }

        assertEquals(34, user.getGemCount());
    }
}
