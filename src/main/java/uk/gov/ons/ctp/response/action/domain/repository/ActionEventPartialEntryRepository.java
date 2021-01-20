package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry.ActionEventPartialProcessStatus;

@Repository
@Transactional(readOnly = true)
public interface ActionEventPartialEntryRepository
    extends JpaRepository<ActionEventPartialEntry, Integer> {

  ActionEventPartialEntry findByCollectionExerciseIdAndEventTag(
      UUID collectionExerciseId, String eventTag);

  List<ActionEventPartialEntry> findByStatus(ActionEventPartialProcessStatus status);
}
