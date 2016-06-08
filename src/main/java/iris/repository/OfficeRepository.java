package iris.repository;

import iris.domain.Office;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Office entity.
 */
@SuppressWarnings("unused")
public interface OfficeRepository extends JpaRepository<Office,Long> {

}
