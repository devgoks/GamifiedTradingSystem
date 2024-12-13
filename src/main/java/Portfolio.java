import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private final List<Asset> assets;

    public Portfolio() {
        this.assets = new ArrayList<>();
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public void removeAsset(int assetId) {
        assets.removeIf(asset -> asset.getAssetId() == assetId);
    }

    public BigDecimal getPortfolioValue() {
        return assets.stream()
            .map(Asset::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.FLOOR);
    }
}
