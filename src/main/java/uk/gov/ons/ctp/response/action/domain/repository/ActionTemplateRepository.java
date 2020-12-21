package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;

@Repository
@Transactional(readOnly = true)
public interface ActionTemplateRepository extends JpaRepository<ActionTemplate, Integer> {

  ActionTemplate findByTagAndHandler(String tag, ActionTemplateDTO.Handler handler);

  ActionTemplate findByType(String type);
}
