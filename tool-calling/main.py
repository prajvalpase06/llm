import os
import sqlite3

import gradio as gr
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()
api_key=os.getenv("OPENAI_API_KEY")
client = OpenAI()
model = "gpt-5-mini"

def check_db_conn():
    try:
        conn = sqlite3.connect("movies.db")
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        return cursor.fetchall()
    except sqlite3.Error as e:
        return f"Database connection failed: {e}"
    finally:
        if conn:
            conn.close()

def generate_sql_prompt(user_input):
    system_prompt = """
        You are an expert SQLite SQL generator.

        Your job is to convert the user's natural language question into a valid SQLite SQL query.

        Database schema:

        genres(
            id INTEGER PRIMARY KEY,
            name TEXT
        )

        directors(
            id INTEGER PRIMARY KEY,
            name TEXT,
            country TEXT
        )

        actors(
            id INTEGER PRIMARY KEY,
            name TEXT,
            country TEXT
        )

        movies(
            id INTEGER PRIMARY KEY,
            title TEXT,
            release_year INTEGER,
            duration_minutes INTEGER,
            imdb_rating REAL,
            box_office_million REAL,
            genre_id INTEGER,
            director_id INTEGER,
            lead_actor_id INTEGER
        )

        Relationships:
        - movies.genre_id -> genres.id
        - movies.director_id -> directors.id
        - movies.lead_actor_id -> actors.id

        Rules:
        1. Return ONLY the SQL query.
        2. Do NOT include markdown.
        3. Do NOT include ```sql.
        4. Generate valid SQLite syntax.
        5. Use JOINs whenever information spans multiple tables.
        6. Never invent tables or columns.
        7. Only generate SELECT statements.
        """
    updated_system_prompt = f"The is asking: {user_input}\n\n{system_prompt}"
    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": updated_system_prompt},
            {"role": "user", "content": user_input}
        ]
    )
    sql = response.choices[0].message.content.strip()
    return sql


###  Testing the prompt generator 

# sql_query = generate_sql_prompt("Show me the top 5 highest-rated movies.")
# print(sql_query)

###


