import java.math.BigDecimal;

public class User {
    private static int idCounter = 1;
    private final int userId;
    private final String username;
    private int gemCount;
    private int rank;
    private int tradingStreak;
    private BigDecimal balance;

    public User(String username) {
        this.userId = idCounter++;
        this.username = username;
        this.gemCount = 0;
        this.rank = 0;
        this.tradingStreak = 0;
        this.balance = BigDecimal.ZERO;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getGemCount() {
        return gemCount;
    }

    public void addGems(int gems) {
        this.gemCount += gems;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTradingStreak() {
        return tradingStreak;
    }

    public void incrementTradingStreak() {
        this.tradingStreak++;
    }

    public void resetTradingStreak() {
        this.tradingStreak = 0;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", gemCount=" + gemCount +
            ", rank=" + rank +
            ", tradingStreak=" + tradingStreak +
            ", balance=" + balance +
            '}';
    }
}