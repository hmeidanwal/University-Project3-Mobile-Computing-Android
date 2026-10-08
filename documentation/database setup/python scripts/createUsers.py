import os
import base64
import psycopg2
import bcrypt

# Define the location of the photos
photo_Location = "group-repository-2025-android-6\\documentation\\database setup\\users"
project_path = "C:\\Users\\Paul\\Documents\\Programmieren\\Fontys\\"
full_photo_path = os.path.join(project_path, photo_Location)

full_photo_path = os.path.join(project_path, photo_Location)

print("Full photo path:", full_photo_path)

# Database connection parameters
db_host="dpg-d4mn0q49c44c7385v2b0-a.oregon-postgres.render.com",
db_database="android6",
db_user="android6_user",
db_password="xwprC5Pq0TW3NCOuQCkJz2axkBHIv4G8",
db_sslmode="require"

# Find and read each jpg file and store its names in a list
photos = []
for filename in os.listdir(full_photo_path):
    if filename.endswith(".jpg") or filename.endswith(".jpeg") or filename.endswith(".png"):
        photos.append(filename)
        
print("Found photos:", photos)

# Convert each photo to base64
photo_data = []
for photo in photos:
    with open(os.path.join(full_photo_path, photo), "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read()).decode('utf-8')
        photo_data.append(encoded_string)
        print(f"Encoded photo: {encoded_string[:30]}...")  # Print first 30 chars of encoded string for verification
        print(f"Size of encoded photo: {len(encoded_string)} characters")
print("Encoded photo data for", len(photo_data), "photos.")