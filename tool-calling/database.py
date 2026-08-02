import sqlite3


DATABASE_NAME = "movies.db"


def execute_sql_query(sql_query: str) -> dict:
    """
    Executes a read-only SQL query on the movies database.

    Returns:
    {
        "success": True,
        "columns": [...],
        "rows": [...]
    }

    OR

    {
        "success": False,
        "error": "..."
    }
    """

    conn = None

    try:
        # Safety check - only allow SELECT queries
        if not sql_query.strip().upper().startswith("SELECT"):
            return {
                "success": False,
                "error": "Only SELECT queries are allowed."
            }

        conn = sqlite3.connect(DATABASE_NAME)
        cursor = conn.cursor()

        cursor.execute(sql_query)

        columns = [column[0] for column in cursor.description]
        rows = cursor.fetchall()

        return {
            "success": True,
            "columns": columns,
            "rows": rows
        }

    except sqlite3.Error as e:
        return {
            "success": False,
            "error": str(e)
        }

    finally:
        if conn:
            conn.close()


def get_database_schema() -> str:
    """
    Returns the current SQLite schema as a formatted string.
    This will later be passed to the LLM so you don't have to
    hardcode the schema in prompts.
    """

    conn = sqlite3.connect(DATABASE_NAME)
    cursor = conn.cursor()

    cursor.execute("""
        SELECT name
        FROM sqlite_master
        WHERE type='table'
        ORDER BY name;
    """)

    tables = cursor.fetchall()

    schema = []

    for table_name, in tables:

        cursor.execute(f"PRAGMA table_info({table_name})")

        columns = cursor.fetchall()

        schema.append(f"{table_name}(")

        for column in columns:
            schema.append(
                f"    {column[1]} {column[2]}"
            )

        schema.append(")\n")

    conn.close()

    return "\n".join(schema)


if __name__ == "__main__":

    print("===== DATABASE SCHEMA =====")
    print(get_database_schema())

    print("\n===== TEST QUERY =====")

    result = execute_sql_query(
        """
        SELECT title, imdb_rating
        FROM movies
        ORDER BY imdb_rating DESC
        LIMIT 5;
        """
    )

    print(result)