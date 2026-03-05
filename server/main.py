import os
from pathlib import Path

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from fastapi.routing import json

load_dotenv()

app = FastAPI()


def send_response(data):
    return {
        'status': 'success',
        'version': '1.0.0',
        'data': data,
    }


def from_log_file():
    LOG_FILE = os.environ.get('LOG_FILE_PATH')

    if LOG_FILE is None:
        raise HTTPException(500, 'log file not properly configured')

    log_path = Path(LOG_FILE).expanduser()

    with open(log_path, 'r') as file:
        data = [json.loads(line) for line in file if line.strip()]

    return data


@app.get('/', tags=['Health'])
def get_index():
    """
    API root path. Use this to confirm the server's availability.
    """
    return send_response('Remote API is up and running')


@app.get('/data', tags=['Core'])
def get_session_data():
    """
    This returns TomatoBar session data from the machine's local directory.
    """
    return send_response(from_log_file())
