package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.UUID;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.Address;

/** JPA repository for Address entities */
@Repository
public interface AddressRepository extends BaseRepository<Address, UUID> {}
