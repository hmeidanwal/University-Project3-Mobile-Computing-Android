| **Name**          | Add Discount |
|:----|:--------|
| **Actor**         | StoreManager |
| **Description**   | Add a new discount to the system. |
| **Pre-condition** | System is running and connected to the database.<br> Actor is authenticated as StoreManager. |
| **Scenario**      | 1. Actor submits discount details.<br> 2. System validates the input.<br> 3. System calls the repository to create a new discount.<br> 4. Repository inserts the discount into the database.<br> 5. System returns the created discount object to the actor. |
| **Result**        | Discount is successfully added and stored in the system. |
| **Exceptions**    | - Required fields are missing or invalid.<br>- Database insertion fails. |
