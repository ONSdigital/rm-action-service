package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

@Component
@Qualifier("business")
public class BusinessActionRequestContextFactory implements ActionRequestContextFactory {
  private static final Logger log =
      LoggerFactory.getLogger(BusinessActionRequestContextFactory.class);

  private static final String NOTIFY = "Notify";

  private final PartySvcClientService partySvcClientService;

  private final DefaultActionRequestContextFactory defaultFactory;

  public BusinessActionRequestContextFactory(
      PartySvcClientService partySvcClientService,
      DefaultActionRequestContextFactory defaultFactory) {
    this.partySvcClientService = partySvcClientService;
    this.defaultFactory = defaultFactory;
  }

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = this.defaultFactory.getActionRequestDecoratorContext(action);

    if (isNotifyType(context)) {
      setPartiesForRespondent(context);
    } else {
      setPartiesForBusiness(context);
    }

    return context;
  }

  private boolean isNotifyType(ActionRequestContext context) {
    return context.getAction().getActionType().getHandler().equals(NOTIFY);
  }

  private void setPartiesForRespondent(ActionRequestContext context) {
    List<PartyDTO> respondentParties =
        Collections.singletonList(
            partySvcClientService.getParty(
                SampleUnitType.BI.name(), context.getAction().getPartyId()));
    context.setChildParties(respondentParties);

    UUID businessPartyId = context.getCaseDetails().getCaseGroup().getPartyId();
    PartyDTO businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitType.B.name(), businessPartyId, context.getSurvey().getId());
    context.setParentParty(businessParty);
  }

  private void setPartiesForBusiness(ActionRequestContext context) {
    PartyDTO businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitType.B.name(),
            context.getCaseDetails().getPartyId(),
            context.getSurvey().getId());
    context.setParentParty(businessParty);

    List<PartyDTO> respondentParties = getRespondentParties(businessParty);
    context.setChildParties(respondentParties);
  }

  public List<PartyDTO> getRespondentParties(final PartyDTO businessParty) {
    final List<String> respondentPartyIds =
        businessParty
            .getAssociations()
            .stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());
    return respondentPartyIds
        .stream()
        .map(id -> partySvcClientService.getParty(SampleUnitType.BI.toString(), id))
        .collect(Collectors.toList());
  }
}
