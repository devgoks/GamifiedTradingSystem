package response;

public class AssetDetailsDTO {
    private Integer assetId;
    private String assetName;

    private Integer tradeCount;

    public AssetDetailsDTO(Integer assetId, String assetName, Integer tradeCount) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.tradeCount = tradeCount;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Integer tradeCount) {
        this.tradeCount = tradeCount;
    }
}
