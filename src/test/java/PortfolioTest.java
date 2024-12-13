import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    private Portfolio portfolio;
    private Asset asset1;
    private Asset asset2;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        asset1 = new Asset(1, "Stock", 10, BigDecimal.valueOf(100));
        asset2 = new Asset(2, "Bond", 5, BigDecimal.valueOf(200));
    }

    @Test
    void testAddAsset() {
        portfolio.addAsset(asset1);
        assertEquals(1, portfolio.getAssets().size());
        assertTrue(portfolio.getAssets().contains(asset1));
    }

    @Test
    void testRemoveAsset() {
        portfolio.addAsset(asset1);
        portfolio.addAsset(asset2);
        portfolio.removeAsset(1);

        assertEquals(1, portfolio.getAssets().size());
        assertFalse(portfolio.getAssets().contains(asset1));
        assertTrue(portfolio.getAssets().contains(asset2));
    }

    @Test
    void testGetPortfolioValue() {
        portfolio.addAsset(asset1);
        portfolio.addAsset(asset2);
        BigDecimal expectedValue = BigDecimal.valueOf(2000).setScale(2, BigDecimal.ROUND_FLOOR);
        assertEquals(expectedValue, portfolio.getPortfolioValue());
    }

    @Test
    void testPortfolioValueWithEmptyPortfolio() {
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_FLOOR);
        assertEquals(expectedValue, portfolio.getPortfolioValue());
    }
}
