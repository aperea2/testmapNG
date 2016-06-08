package iris.repository.search;

import iris.domain.Office;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Office entity.
 */
public interface OfficeSearchRepository extends ElasticsearchRepository<Office, Long> {
}
