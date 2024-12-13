## Gamified Trading System

### How to Run and Test The Application

- You can run the main app from the Main class src/main/java/Main.java
- You can run the test classes in src/test/java folder

### Postman Collection

- I created a postman collection which you can import from the file.
- "Trove Finance Gamified Trading System.postman_collection.json".
- This will aid easier testing of APIs created.

### **API Endpoints**

1. **POST /user**  
   Create a new user with a provided username. (A portfolio is created for the user too).

2. **GET /user/:userId**  
   Retrieve user details by ID.

3. **POST /user/fund-balance**  
   Add funds to a user’s balance.

4. **POST /portfolio/add-asset**  
   Add an asset to a user's portfolio.

5. **POST /portfolio/remove-asset**  
   Remove an asset from a user's portfolio.

6. **GET /portfolio/:userId**  
   Retrieve the portfolio of a user by user ID.

7. **POST /trade**  
   Execute a trade (buy/sell) for an asset.

8. **GET /leaderboard**  
   Retrieve the top N users from the leaderboard.

9. **GET /insights**  
   Get insights on top user and most traded assets.

### **Background Tasks**

- **Asset Price Updates**  
  Runs every 60 seconds to update asset prices.

- **Leaderboard Updates**  
  Runs every 5 seconds to update the leaderboard.  



