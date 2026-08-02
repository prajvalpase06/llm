import random
import sqlite3

random.seed(42)

conn = sqlite3.connect("movies.db")
cursor = conn.cursor()

cursor.execute("PRAGMA foreign_keys = ON")

# -----------------------------
# Drop Tables
# -----------------------------
cursor.executescript("""
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS genres;
""")

# -----------------------------
# Create Tables
# -----------------------------
cursor.executescript("""
CREATE TABLE genres(
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE directors(
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    country TEXT NOT NULL
);

CREATE TABLE actors(
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    country TEXT NOT NULL
);

CREATE TABLE movies(
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    release_year INTEGER,
    duration_minutes INTEGER,
    imdb_rating REAL,
    box_office_million REAL,

    genre_id INTEGER,
    director_id INTEGER,
    lead_actor_id INTEGER,

    FOREIGN KEY(genre_id) REFERENCES genres(id),
    FOREIGN KEY(director_id) REFERENCES directors(id),
    FOREIGN KEY(lead_actor_id) REFERENCES actors(id)
);
""")

# -----------------------------
# Genres
# -----------------------------
genres = [
    "Action",
    "Comedy",
    "Drama",
    "Thriller",
    "Sci-Fi",
    "Horror",
    "Fantasy",
    "Adventure",
    "Crime",
    "Romance"
]

for genre in genres:
    cursor.execute(
        "INSERT INTO genres(name) VALUES (?)",
        (genre,)
    )

# -----------------------------
# Directors
# -----------------------------
director_first = [
    "James","David","Chris","Steven","Michael",
    "Alex","Daniel","Ryan","John","Martin",
    "Sarah","Emma","Olivia","Sophia","Rachel"
]

director_last = [
    "Carter","Miller","Stone","Hayes","Brooks",
    "Turner","Walker","Evans","Scott","Cooper"
]

countries = [
    "USA",
    "UK",
    "Canada",
    "Australia",
    "India",
    "France",
    "Germany"
]

for i in range(20):
    cursor.execute(
        "INSERT INTO directors(name,country) VALUES (?,?)",
        (
            f"{random.choice(director_first)} {random.choice(director_last)}",
            random.choice(countries)
        )
    )

# -----------------------------
# Actors
# -----------------------------
actor_first = [
    "Liam","Noah","Emma","Olivia","Ava",
    "Sophia","Mason","Lucas","Ethan","Logan",
    "Isabella","Mia","Charlotte","Amelia","Harper"
]

actor_last = [
    "Johnson","Brown","Wilson","Taylor",
    "Thomas","White","Hall","Allen",
    "Young","King"
]

for i in range(60):
    cursor.execute(
        "INSERT INTO actors(name,country) VALUES (?,?)",
        (
            f"{random.choice(actor_first)} {random.choice(actor_last)}",
            random.choice(countries)
        )
    )

# -----------------------------
# Movies
# -----------------------------
adjectives = [
    "Silent","Dark","Lost","Golden","Final",
    "Hidden","Broken","Last","Crimson","Infinite",
    "Midnight","Frozen","Burning","Secret","Forgotten"
]

nouns = [
    "Empire","Journey","Shadow","Legacy",
    "Protocol","Dream","Storm","Odyssey",
    "Galaxy","Code","Signal","Frontier",
    "Hunter","Destiny","Horizon"
]

used_titles = set()

for _ in range(100):

    title = f"{random.choice(adjectives)} {random.choice(nouns)}"

    while title in used_titles:
        title = f"{random.choice(adjectives)} {random.choice(nouns)}"

    used_titles.add(title)

    cursor.execute("""
        INSERT INTO movies(
            title,
            release_year,
            duration_minutes,
            imdb_rating,
            box_office_million,
            genre_id,
            director_id,
            lead_actor_id
        )
        VALUES (?,?,?,?,?,?,?,?)
    """,
    (
        title,
        random.randint(1995,2025),
        random.randint(85,190),
        round(random.uniform(5.5,9.5),1),
        round(random.uniform(15,1800),1),
        random.randint(1,10),
        random.randint(1,20),
        random.randint(1,60)
    ))

conn.commit()
conn.close()

print("Database created successfully!")