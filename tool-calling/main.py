import os
import sqlite3

import gradio as gr
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()
api_key=os.getenv("OPENAI_API_KEY")
client = OpenAI()
