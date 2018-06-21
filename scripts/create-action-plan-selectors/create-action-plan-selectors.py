import logging
import sys

import requests
from sqlalchemy import create_engine
from structlog import wrap_logger

from config import Config


logger = wrap_logger(logging.getLogger(__name__))


def get_casetypeoverrides():
    logger.debug('Retrieving casetypeoverrides')
    engine = create_engine(Config.DATABASE_URI)
    connection = engine.connect()

    sql = "SELECT o.sampleunittypefk, o.actionplanid, c.exerciseref, c.survey_uuid " \
          "FROM collectionexercise.casetypeoverride o " \
          "INNER JOIN collectionexercise.collectionexercise c ON o.exercisefk = c.exercisepk"

    trans = connection.begin()
    response = connection.execute(sql)
    trans.commit()
    logging.debug('Successfully retrieved casetypeoverrides')
    return [row for row in response]


def get_survey_ref(survey_id):
    logger.debug('Retrieving survey ref', survey_id=survey_id)
    url = f'{Config.SURVEY_URL}/surveys/{survey_id}'
    response = requests.get(url=url, auth=Config.SURVEY_AUTH)
    response.raise_for_status()
    survey_ref = response.json()['surveyRef']
    logger.debug('Successfully retrieved surveyRef', survey_id=survey_id, survey_ref=survey_ref)
    return survey_ref


def update_action_plan_selectors(casetypeoverride):
    action_plan_id = casetypeoverride['actionplanid']
    exercise_ref = casetypeoverride['exerciseref']
    sample_unit_type = casetypeoverride["sampleunittypefk"]
    logger.info('Updating action plan selectors',
                action_plan_id=action_plan_id, exercise_ref=exercise_ref, sample_unit_type=sample_unit_type)

    url = f'{Config.ACTION_URL}/actionplans/{action_plan_id}'

    survey_ref = get_survey_ref(casetypeoverride['survey_uuid'])
    try:

        active_enrolment = {
            "B": "false",
            "BI": "true"
        }[sample_unit_type]
    except KeyError:
        logger.error('Invalid sample unit type',
                     action_plan_id=action_plan_id, exercise_ref=exercise_ref, sample_unit_type=sample_unit_type)
        sys.exit(1)
    data = {
        "selectors": {
            "surveyRef": survey_ref,
            "exerciseRef": exercise_ref,
            "activeEnrolment": active_enrolment
        }
    }
    response = requests.put(url, json=data, auth=Config.ACTION_AUTH)
    response.raise_for_status()
    logger.info('Successfully updated action plan selectors', action_plan_id=action_plan_id,
                exercise_ref=exercise_ref, survey_ref=survey_ref, sample_unit_type=sample_unit_type)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    casetypeoverrides = get_casetypeoverrides()
    for casetypeoverride in casetypeoverrides:
        update_action_plan_selectors(casetypeoverride)
