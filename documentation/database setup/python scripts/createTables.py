import psycopg2

# Database connection parameters
db_host="dpg-d4mn0q49c44c7385v2b0-a.oregon-postgres.render.com",
db_database="android6",
db_user="android6_user",
db_password="xwprC5Pq0TW3NCOuQCkJz2axkBHIv4G8",
db_sslmode="require"

# Connect to the PostgreSQL database
try:
    connection = psycopg2.connect(
        host=db_host,
        database=db_database,
        user=db_user,
        password=db_password,
        sslmode=db_sslmode
    )
    cursor = connection.cursor()
    print("Database connection established.")

    # SQL command to create the accounts table
    create_table_query = '''
    DROP TABLE IF EXISTS account;
    DROP TYPE IF EXISTS user;
    DROP TYPE IF EXISTS store_owner;
    DROP TYPE IF EXISTS product;
    DROP TYPE IF EXISTS discount;

    CREATE TABLE account (
        email VARCHAR(255) PRIMARY KEY NOT NULL,
        password VARCHAR(255) NOT NULL,
        role VARCHAR(50) NOT NULL,
        full_name VARCHAR(255) NOT NULL
    );

    CREATE TABLE user (
        fcm_token VARCHAR(255) NOT NULL,
        notifications BOOLEAN NOT NULL,
        picture VARCHAR(5000000),
        account_email VARCHAR(255),
        FOREIGN KEY (account_email) REFERENCES account(email) ON DELETE CASCADE
    );

    CREATE TABLE store_owner (
        id SERIAL PRIMARY KEY,
        picture VARCHAR(5000000),
        address VARCHAR(255),
        account_email VARCHAR(255),
        FOREIGN KEY (account_email) REFERENCES account(email) ON DELETE CASCADE
    );

    CREATE TABLE product (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        image VARCHAR(5000000),
        price DECIMAL(10, 2) NOT NULL,
        package_size VARCHAR(100) NOT NULL,
        measurement VARCHAR(50) NOT NULL,
        store INT NOT NULL,
        FOREIGN KEY (store) REFERENCES store_owner(id) ON DELETE CASCADE
    );

    CREATE TABLE discount (
        price DECIMAL(10, 2) NOT NULL,
        start_date TIMESTAMP NOT NULL,
        end_date TIMESTAMP NOT NULL,
        product_id INT NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
    );
    '''

    # Execute the SQL command
    cursor.execute(create_table_query)
    connection.commit()
    print("Accounts table created successfully.")
    cursor.close()
    connection.close()
    print("Database connection closed.")
except Exception as e:
    print("An error occurred:", e)