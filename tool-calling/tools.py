import json

from database import execute_sql_query
from sql_generator import generate_sql


def query_movie_database(user_question: str) -> dict:
    """
    End-to-end database tool.

    Flow:
        User Question
              ↓
        Generate SQL
              ↓
        Execute SQL
              ↓
        Return structured results
    """

    sql = generate_sql(user_question)

    # Model couldn't generate SQL
    if sql == "CANNOT_GENERATE_SQL":
        return {
            "success": False,
            "error": "Unable to generate a SQL query for this question."
        }

    print("\n========== GENERATED SQL ==========")
    print(sql)
    print("===================================\n")

    result = execute_sql_query(sql)

    return {
        "generated_sql": sql,
        "query_result": result
    }


# -------------------------------------------------------
# OpenAI Tool Definition
# -------------------------------------------------------

query_movie_database_tool = {
    "type": "function",
    "function": {
        "name": "query_movie_database",
        "description": (
            "Query the movie database to answer questions about "
            "movies, directors, genres and actors."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "user_question": {
                    "type": "string",
                    "description": "The user's question."
                }
            },
            "required": ["user_question"],
            "additionalProperties": False
        }
    }
}


# -------------------------------------------------------
# Handle Tool Calls
# -------------------------------------------------------

def handle_tool_calls(tool_calls):
    """
    Executes tool calls requested by the LLM.
    Returns a list of tool response messages.
    """

    responses = []

    for tool_call in tool_calls:

        function_name = tool_call.function.name
        arguments = json.loads(tool_call.function.arguments)

        if function_name == "query_movie_database":

            result = query_movie_database(
                arguments["user_question"]
            )

            responses.append({
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": json.dumps(result)
            })

    return responses


# -------------------------------------------------------
# Local testing
# -------------------------------------------------------

if __name__ == "__main__":

    result = query_movie_database(
        "Show me the top 5 highest rated movies."
    )

    print(json.dumps(result, indent=2))