package uk.gov.ons.ctp.response.action.service;

import java.util.List;
import java.util.Optional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry;
import uk.gov.ons.ctp.response.action.domain.repository.ActionEventPartialEntryRepository;

@Service
public class ProcessPartialEventService {
  private final ActionEventPartialEntryRepository actionEventPartialEntryRepository;
  private final ProcessEventService processEventService;

  public ProcessPartialEventService(
      ActionEventPartialEntryRepository actionEventPartialEntryRepository,
      ProcessEventService processEventService) {
    this.actionEventPartialEntryRepository = actionEventPartialEntryRepository;
    this.processEventService = processEventService;
  }

  @Async
  public void processPartialEvents() {
    List<ActionEventPartialEntry> partialEventEntries =
        actionEventPartialEntryRepository.findByStatus(
            ActionEventPartialEntry.ActionEventPartialProcessStatus.PARTIAL);
    partialEventEntries.parallelStream()
        .forEach(
            p ->
                processEventService.processEvents(
                    p.getCollectionExerciseId(), p.getEventTag(), Optional.of(p)));
  }
}
