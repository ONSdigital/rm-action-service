package uk.gov.ons.ctp.response.action.service;

import java.util.UUID;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/** A Service which utilises the CollectionexerciseSvc via RESTful client calls */
public interface CollectionExerciseClientService {

  /**
   * Find CollectionExerciseDTO entity by specified collection id.
   *
   * @param collectionId This is the collection exercise id
   * @return CollectionExerciseDTO Returns the CollectionExerciseDTO for the specified action id.
   */
  CollectionExerciseDTO getCollectionExercise(UUID collectionId);
}
