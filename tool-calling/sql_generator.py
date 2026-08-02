import os

from dotenv import load_dotenv
from openai import OpenAI

from database import get_database_schema

load_dotenv()

client = OpenAI()
MODEL = "gpt-5-mini"


def generate_sql(user_question: str) -> str:
    """
    Converts a natural language question into a SQLite SELECT query.
    """

    schema = get_database_schema()

    system_prompt = f"""
You are an expert SQLite SQL generator.

Your task is to convert the user's question into a valid SQLite query.

Database Schema:

{schema}

Rules:

1. Return ONLY the SQL query.
2. Do NOT wrap the query in markdown.
3. Do NOT explain the query.
4. Only generate SELECT statements.
5. Never invent tables.
6. Never invent columns.
7. Use JOINs when required.
8. Generate valid SQLite syntax.
9. If the question cannot be answered using the schema,
   return exactly:

CANNOT_GENERATE_SQL
"""

    response = client.chat.completions.create(
        model=MODEL,
        messages=[
            {
                "role": "system",
                "content": system_prompt
            },
            {
                "role": "user",
                "content": user_question
            }
        ],
    )

    sql = response.choices[0].message.content.strip()

    return sql


if __name__ == "__main__":

    while True:

        question = input("\nAsk a question: ")

        if question.lower() == "exit":
            break

        sql = generate_sql(question)

        print("\nGenerated SQL:\n")
        print(sql)