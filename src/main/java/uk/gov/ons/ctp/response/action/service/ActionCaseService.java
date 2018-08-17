package uk.gov.ons.ctp.response.action.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;

/**
 * An ActionService implementation which encapsulates all business logic operating on the Action
 * entity model.
 */
@Service
public class ActionCaseService {

  @Autowired private ActionCaseRepository actionCaseRepo;

  public ActionCase findActionCase(final UUID caseId) {
    return actionCaseRepo.findById(caseId);
  }
}
