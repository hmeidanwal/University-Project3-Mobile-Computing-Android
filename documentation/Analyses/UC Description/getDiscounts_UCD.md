| **Name**          | Get Discounts |
|:----|:--------|
| **Actor**         | User, StoreManager |
| **Description**   | Retrieve a list of all available discounts. |
| **Pre-condition** | System is running and connected to the database. |
| **Scenario**      | 1. Actor requests all discounts via the system <br> 2. System fetches discount data from the repository and database.<br> 3. System returns the list of discounts to the actor. |
| **Result**        | System displays all available discounts successfully |
| **Exceptions**    | - Database is unavailable.<br>- No discounts exist in the system. |
