package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;

/** Implementation */
@Service
public class ActionTypeService {
  private static final Logger log = LoggerFactory.getLogger(ActionTypeService.class);

  @Autowired private ActionTypeRepository actionTypeRepo;

  @CoverageIgnore
  public ActionType findActionType(final Integer actionTypeKey) {
    return actionTypeRepo.findOne(actionTypeKey);
  }

  @CoverageIgnore
  public ActionType findActionTypeByName(final String name) {
    return actionTypeRepo.findByName(name);
  }
}
