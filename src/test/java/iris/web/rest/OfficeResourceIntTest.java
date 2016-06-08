package iris.web.rest;

import iris.LeafNgApp;
import iris.domain.Office;
import iris.repository.OfficeRepository;
import iris.repository.search.OfficeSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the OfficeResource REST controller.
 *
 * @see OfficeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LeafNgApp.class)
@WebAppConfiguration
@IntegrationTest
public class OfficeResourceIntTest {
	static Geometry g1 = new GeometryFactory().createPolygon(new Coordinate[] { new Coordinate(0, 0),
			new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(0, 0) });
	static Geometry g2 = new GeometryFactory().createPolygon(new Coordinate[] { new Coordinate(0, 1),
			new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(0, 1) });

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final Geometry DEFAULT_LOCATION = g1;
    private static final Geometry UPDATED_LOCATION = g2;
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_PARENT_OFFICE = "AAAAA";
    private static final String UPDATED_PARENT_OFFICE = "BBBBB";

    @Inject
    private OfficeRepository officeRepository;

    @Inject
    private OfficeSearchRepository officeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOfficeMockMvc;

    private Office office;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OfficeResource officeResource = new OfficeResource();
        ReflectionTestUtils.setField(officeResource, "officeSearchRepository", officeSearchRepository);
        ReflectionTestUtils.setField(officeResource, "officeRepository", officeRepository);
        this.restOfficeMockMvc = MockMvcBuilders.standaloneSetup(officeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        officeSearchRepository.deleteAll();
        office = new Office();
        office.setName(DEFAULT_NAME);
        office.setLocation(DEFAULT_LOCATION);
        office.setType(DEFAULT_TYPE);
        office.setParentOffice(DEFAULT_PARENT_OFFICE);
    }

    @Test
    @Transactional
    public void createOffice() throws Exception {
        int databaseSizeBeforeCreate = officeRepository.findAll().size();

        // Create the Office

        restOfficeMockMvc.perform(post("/api/offices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(office)))
                .andExpect(status().isCreated());

        // Validate the Office in the database
        List<Office> offices = officeRepository.findAll();
        assertThat(offices).hasSize(databaseSizeBeforeCreate + 1);
        Office testOffice = offices.get(offices.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOffice.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testOffice.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testOffice.getParentOffice()).isEqualTo(DEFAULT_PARENT_OFFICE);

        // Validate the Office in ElasticSearch
        Office officeEs = officeSearchRepository.findOne(testOffice.getId());
        assertThat(officeEs).isEqualToComparingFieldByField(testOffice);
    }

    @Test
    @Transactional
    public void getAllOffices() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        // Get all the offices
        restOfficeMockMvc.perform(get("/api/offices?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(office.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
             //   .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].parentOffice").value(hasItem(DEFAULT_PARENT_OFFICE.toString())));
    }

    @Test
    @Transactional
    public void getOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        // Get the office
        restOfficeMockMvc.perform(get("/api/offices/{id}", office.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(office.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
           // .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.parentOffice").value(DEFAULT_PARENT_OFFICE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOffice() throws Exception {
        // Get the office
        restOfficeMockMvc.perform(get("/api/offices/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);
        officeSearchRepository.save(office);
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();

        // Update the office
        Office updatedOffice = new Office();
        updatedOffice.setId(office.getId());
        updatedOffice.setName(UPDATED_NAME);
      //  updatedOffice.setLocation(UPDATED_LOCATION);
        updatedOffice.setType(UPDATED_TYPE);
        updatedOffice.setParentOffice(UPDATED_PARENT_OFFICE);

        restOfficeMockMvc.perform(put("/api/offices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOffice)))
                .andExpect(status().isOk());

        // Validate the Office in the database
        List<Office> offices = officeRepository.findAll();
        assertThat(offices).hasSize(databaseSizeBeforeUpdate);
        Office testOffice = offices.get(offices.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(UPDATED_NAME);
       // assertThat(testOffice.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testOffice.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testOffice.getParentOffice()).isEqualTo(UPDATED_PARENT_OFFICE);

        // Validate the Office in ElasticSearch
        Office officeEs = officeSearchRepository.findOne(testOffice.getId());
        assertThat(officeEs).isEqualToComparingFieldByField(testOffice);
    }

    @Test
    @Transactional
    public void deleteOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);
        officeSearchRepository.save(office);
        int databaseSizeBeforeDelete = officeRepository.findAll().size();

        // Get the office
        restOfficeMockMvc.perform(delete("/api/offices/{id}", office.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean officeExistsInEs = officeSearchRepository.exists(office.getId());
        assertThat(officeExistsInEs).isFalse();

        // Validate the database is empty
        List<Office> offices = officeRepository.findAll();
        assertThat(offices).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);
        officeSearchRepository.save(office);

        // Search the office
        restOfficeMockMvc.perform(get("/api/_search/offices?query=id:" + office.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(office.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            //.andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].parentOffice").value(hasItem(DEFAULT_PARENT_OFFICE.toString())));
    }
}
