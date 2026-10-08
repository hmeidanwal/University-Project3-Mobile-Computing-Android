import os
import base64
import psycopg2
from PIL import Image
import io

# Define the location of the photos
photo_Location = "group-repository-2025-android-6\\documentation\\database setup\\products"
project_path = "C:\\Users\\Paul\\Documents\\Programmieren\\Fontys\\"

full_photo_path = os.path.join(project_path, photo_Location)
existing_product_names = []

print("Full photo path:", full_photo_path)

# Database connection parameters
# URL: DATABASE_URL=postgresql://android6_user:Le2JTxuXlzL56kOm9dMr3arMivnYGHvs@dpg-d5baqlfgi27c738oql5g-a.oregon-postgres.render.com:5432/android6_m9ww?sslmode=require


db_host = "dpg-d5baqlfgi27c738oql5g-a.oregon-postgres.render.com"
db_database = "android6_m9ww"
db_user = "android6_user"
db_password = "Le2JTxuXlzL56kOm9dMr3arMivnYGHvs"
db_sslmode = "require"

# Find and read each jpg file and store its names in a list
photos = []
for filename in os.listdir(full_photo_path):
    if filename.endswith(".jpg") or filename.endswith(".jpeg") or filename.endswith(".png"):
        photos.append(filename)
        
print("Found photos:", photos)

# get all existing products from the database to avoid duplicates
try:
    connection = psycopg2.connect(
        host=db_host,
        database=db_database,
        user=db_user,
        password=db_password,
        sslmode=db_sslmode
    )
    cursor = connection.cursor()
    cursor.execute("SELECT name FROM product;")
    existing_products = cursor.fetchall()
    existing_product_names = [product[0] for product in existing_products]
    
    # Remove duplicates from existing product names
    existing_product_names = list(set(existing_product_names))
    print("Existing product names after removing duplicates:", existing_product_names)

    # For each product, check if a photo exist and insert it into the database
    for existing_product in existing_product_names:
        photo_filename = existing_product + ".jpg"
        if photo_filename in photos:
            photo_path = os.path.join(full_photo_path, photo_filename)
            try:
                # Open and optimize the image
                with Image.open(photo_path) as img:
                    # Convert to RGB if needed (handles PNGs with transparency)
                    if img.mode != 'RGB':
                        img = img.convert('RGB')
                    
                    # Resize maintaining aspect ratio (max 800x800)
                    img.thumbnail((800, 800), Image.Resampling.LANCZOS)
                    
                    # Save to buffer with compression
                    buffer = io.BytesIO()
                    img.save(buffer, format='JPEG', quality=85, optimize=True)
                    
                    # Encode with proper data URI prefix
                    encoded_string = base64.b64encode(buffer.getvalue()).decode('utf-8')
                    data_uri = f"data:image/jpeg;base64,{encoded_string}"
                    
                # Insert the product with photo into the database
                insert_query = """
                UPDATE product
                SET image = %s
                WHERE name = %s;
                """
                cursor.execute(insert_query, (data_uri, existing_product))
                connection.commit()
                print(f"Inserted photo for product: {existing_product}")
            except Exception as e:
                print(f"Error processing product {existing_product}: {e}")
        else:
            print(f"No photo found for product: {existing_product}")
    
    cursor.close()
    connection.close()
except Exception as e:
    print("Error connecting to database:", e)
    existing_product_names = []