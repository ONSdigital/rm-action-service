package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO.Handler;

@Repository
@Transactional(readOnly = true)
public interface ActionEventRepository extends JpaRepository<ActionEvent, Integer> {
  ActionEvent findByCaseIdAndTypeAndHandlerAndTagAndStatus(
      UUID caseId, String type, Handler handler, String tag, ActionEvent.ActionEventStatus status);

  List<ActionEvent> findByStatus(ActionEvent.ActionEventStatus status);
}
