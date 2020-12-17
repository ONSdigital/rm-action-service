package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ExportJob;

@Repository
public interface ExportJobRepository extends JpaRepository<ExportJob, UUID> {}
