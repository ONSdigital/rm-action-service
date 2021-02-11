package uk.gov.ons.ctp.response.action.representation.events;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionCaseParty {
  private PartyDTO parentParty;
  private List<PartyDTO> childParties;
}
