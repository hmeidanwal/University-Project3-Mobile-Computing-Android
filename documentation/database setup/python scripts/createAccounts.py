import psycopg2
import bcrypt
import os


# Define the location of the photos
photo_Location = "group-repository-2025-android-6\\documentation\\database setup\\users"
project_path = "C:\\Users\\Paul\\Documents\\Programmieren\\Fontys\\"
full_photo_path = os.path.join(project_path, photo_Location)

print("Full photo path:", full_photo_path)

# Database connection parameters
db_host="dpg-d4mn0q49c44c7385v2b0-a.oregon-postgres.render.com",
db_database="android6",
db_user="android6_user",
db_password="xwprC5Pq0TW3NCOuQCkJz2axkBHIv4G8",
db_sslmode="require"

# Setup needed variables
photos = []
full_names = []
roles = []
emails = []
hashed_passwords = []

# Find and read each jpg file and store its names in a list

for filename in os.listdir(full_photo_path):
    if filename.endswith(".jpg") or filename.endswith(".jpeg") or filename.endswith(".png"):
        photos.append(filename)
        
print("Found photos:", photos)

# database values are: email, password, role and full name
# Sample data for accounts to be created from collected photos
for i in photos:
    nametag_parts = i.rsplit('.', 1)[0].split('_')
    full_names.append(nametag_parts[0])
    if nametag_parts[1].__eq__("0"):
        roles.append("customer")
    else:
        roles.append("store owner")
    print(f"Processed file: {i}; Full name: {nametag_parts[0]}; Role: {roles[-1]}")

# Generate emails and hashed passwords for each user
for name in full_names:
    email = name.lower().replace(" ", ".") + "@exampe.com"
    emails.append(email)

    password = name.lower().replace(" ", "") + "123"
    hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt(10))
    hashed_passwords.append(hashed.decode('utf-8'))
print("Generated emails:", emails)
print("Generated hashed passwords:", hashed_passwords)

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

    # Insert each account into the database
    for i in range(len(full_names)):
        insert_query = """
        INSERT INTO account (email, password, role, full_name)
        VALUES (%s, %s, %s, %s)
        """

        cursor.execute(insert_query, (emails[i], hashed_passwords[i], roles[i], full_names[i]))
        connection.commit()
        print(f"Inserted account for: {full_names[i]}")
    cursor.close()
    connection.close()
    print("Database connection closed.")
except Exception as e:
    print("An error occurred:", e)