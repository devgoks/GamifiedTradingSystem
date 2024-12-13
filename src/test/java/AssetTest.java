import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset(1, "Stock", 10, BigDecimal.valueOf(100.50));
    }

    @Test
    void testGetAssetId() {
        assertEquals(1, asset.getAssetId(), "Asset ID should be 1");
    }

    @Test
    void testGetName() {
        assertEquals("Stock", asset.getName(), "Asset name should be 'Stock'");
    }

    @Test
    void testGetQuantity() {
        assertEquals(10, asset.getQuantity(), "Quantity should be 10");
    }

    @Test
    void testSetQuantity() {
        asset.setQuantity(20);
        assertEquals(20, asset.getQuantity(), "Quantity should be updated to 20");
    }

    @Test
    void testGetPrice() {
        assertEquals(BigDecimal.valueOf(100.50), asset.getPrice(), "Price should be 100.50");
    }

    @Test
    void testSetPrice() {
        BigDecimal newPrice = BigDecimal.valueOf(150.75);
        asset.setPrice(newPrice);
        assertEquals(newPrice, asset.getPrice(), "Price should be updated to 150.75");
    }

    @Test
    void testGetValue() {
        assertEquals(BigDecimal.valueOf(1005.00), asset.getValue(), "Value should be 1005.00");
    }

    @Test
    void testSetPriceFromDouble() {
        asset.setPriceFromDouble(200.25);
        assertEquals(BigDecimal.valueOf(200.25), asset.getPrice(), "Price should be updated to 200.25 using setPriceFromDouble");
    }
}