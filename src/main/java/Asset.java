import java.math.BigDecimal;

public class Asset {
    private final int assetId;
    private final String name;
    private int quantity;
    private BigDecimal price;

    public Asset(int assetId, String name, int quantity, BigDecimal price) {
        this.assetId = assetId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getAssetId() {
        return assetId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void setPriceFromDouble(double price) {
        this.price = BigDecimal.valueOf(price);
    }
}
