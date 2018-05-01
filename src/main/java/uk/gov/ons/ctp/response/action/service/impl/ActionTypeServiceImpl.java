package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;



/**
 * Implementation
 */
@Service
@Slf4j
public class ActionTypeServiceImpl implements ActionTypeService {


  @Autowired
  private ActionTypeRepository actionTypeRepo;

  @CoverageIgnore
  @Override
  public ActionType findActionType(final Integer actionTypeKey) {
    log.debug("Entering findActionRules");
    return actionTypeRepo.findOne(actionTypeKey);
  }
}
