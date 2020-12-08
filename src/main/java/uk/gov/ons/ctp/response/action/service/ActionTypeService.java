package uk.gov.ons.ctp.response.action.service;

import java.util.List;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;

/** Implementation */
@Service
public class ActionTypeService {
  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;

  @Autowired private ActionTypeRepository actionTypeRepo;

  @CoverageIgnore
  public ActionType findActionType(final Integer actionTypeKey) {
    return actionTypeRepo.findOne(actionTypeKey);
  }

  @CoverageIgnore
  public ActionType findActionTypeByName(final String name) {
    return actionTypeRepo.findByName(name);
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public List<ActionType> findByHandler(final String handler) {
    return actionTypeRepo.findByHandler(handler);
  }
}
