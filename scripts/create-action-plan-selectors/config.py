import os


class Config:
    DATABASE_URI = os.getenv('DATABASE_URI', 'postgres://postgres:postgres@localhost:6432/postgres')

    ACTION_URL = os.getenv('ACTION_URL', 'http://localhost:8151')
    ACTION_USERNAME = os.getenv('ACTION_USERNAME', 'admin')
    ACTION_PASSWORD = os.getenv('ACTION_PASSWORD', 'secret')
    ACTION_AUTH = (ACTION_USERNAME, ACTION_PASSWORD)

    SURVEY_URL = os.getenv('SURVEY_URL', 'http://localhost:8080')
    SURVEY_USERNAME = os.getenv('SURVEY_USERNAME', 'admin')
    SURVEY_PASSWORD = os.getenv('SURVEY_PASSWORD', 'secret')
    SURVEY_AUTH = (SURVEY_USERNAME, SURVEY_PASSWORD)
