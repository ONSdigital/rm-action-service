package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry.ActionEventPartialProcessStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActionEventPartialEntryRepository extends JpaRepository<ActionEventPartialEntry, Integer> {

  ActionEventPartialEntry findByCollectionExerciseIdAndEventTag (UUID collectionExerciseId, String eventTag);

  List<ActionEventPartialEntry> findByStaus (ActionEventPartialProcessStatus status);
}
