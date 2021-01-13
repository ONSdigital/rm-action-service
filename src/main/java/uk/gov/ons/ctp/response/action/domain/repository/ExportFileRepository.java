package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ExportFile;

@Repository
public interface ExportFileRepository extends JpaRepository<ExportFile, UUID> {

  List<ExportFile> findAllByExportJobId(UUID filename);

  boolean existsByFilename(String filename);
}