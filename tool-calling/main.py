import gradio as gr
from dotenv import load_dotenv
from openai import OpenAI

from tools import (
    query_movie_database_tool,
    handle_tool_calls
)

load_dotenv()

client = OpenAI()

MODEL = "gpt-5-mini"


SYSTEM_PROMPT = """
You are a helpful movie assistant.

You answer questions ONLY about movies, actors, directors and genres.
IGNORE ANYTHING ELSE. You are NOT a general knowledge assistant.

Whenever information from the movie database is required,
ALWAYS use the query_movie_database tool.

You MUST ONLY generate SQL using the tables and data available in the database.

Never assume a movie exists.

Never insert literal movie names unless the user explicitly mentioned them.

If the user asks for recommendations like

"Any horror movies worth watching?"

interpret that as

"Show the highest rated Horror movies in the database."

Do NOT use your own knowledge of famous movies.

The database is the ONLY source of truth.

Never make up movie information.

If the database does not contain the answer,
say you don't know.
"""

def chat(message, history):

    messages = [
        {
            "role": "system",
            "content": SYSTEM_PROMPT
        }
    ]

    # Convert Gradio history -> OpenAI messages
    for user_msg, assistant_msg in history:

        messages.append({
            "role": "user",
            "content": user_msg
        })

        if assistant_msg is not None:
            messages.append({
                "role": "assistant",
                "content": assistant_msg
            })

    # Current user message
    messages.append({
        "role": "user",
        "content": message
    })

    response = client.chat.completions.create(
        model=MODEL,
        messages=messages,
        tools=[query_movie_database_tool]
    )

    while response.choices[0].finish_reason == "tool_calls":

        assistant_message = response.choices[0].message

        messages.append(assistant_message)

        tool_messages = handle_tool_calls(
            assistant_message.tool_calls
        )

        messages.extend(tool_messages)

        response = client.chat.completions.create(
            model=MODEL,
            messages=messages,
            tools=[query_movie_database_tool]
        )

    return response.choices[0].message.content


demo = gr.ChatInterface(
    fn=chat,
    title="🎬 Movie Assistant",
    description="Ask questions about movies stored in the SQLite database."
)

demo.launch()