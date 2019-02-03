package com.seva.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.seva.dao.PersonDAO;
import com.seva.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@TransactionConfiguration
@Transactional
public class PersonDAOTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	final Logger logger = LoggerFactory.getLogger(PersonDAOTest.class);
    
    protected static int SIZE = 1;
    protected static Long ID = new Long(1);
    protected static String NAME = "Narayan";
    protected static String COUNTRY = "Nepal";
    protected static String CHANGED_NAME = "Prabhat";

    @Autowired
    protected PersonDAO personDAO = null;
    
    /**
     * Tests that the size and first record match what is expected 
     * before the transaction.
     */
    @BeforeTransaction
    public void beforeTransaction() {
        testPerson(true, COUNTRY);
    }

    /**
     * Tests person table and changes the first records name.
     */
    @Test
    public void testPersonDAO() throws SQLException {
        assertNotNull("Person DAO is null.", personDAO);
        
        Collection<Person> lPersons = personDAO.listPersons();
        
        assertNotNull("Person list is null.", lPersons);
        assertEquals("Number of persons should be " + SIZE + ".", SIZE, lPersons.size());
        
        for (Person person : lPersons) {
            assertNotNull("Person is null.", person);
                        
            if (ID.equals(person.getId())) {                
                assertEquals("Person name should be " + NAME + ".", NAME, person.getName());
                assertEquals("Person country should be " + COUNTRY + ".", COUNTRY, person.getCountry());
                
                person.setName(CHANGED_NAME);
                
                personDAO.addPerson(person);
            }
        }
    }

    /**
     * Tests that the size and first record match what is expected 
     * after the transaction.
     */
    @AfterTransaction
    public void afterTransaction() {
        testPerson(false, COUNTRY);
    }

    /**
     * Tests person table.
     */
    protected void testPerson(boolean beforeTransaction, String matchCountry) {
        List<Map<String, Object>> lPersonMaps = jdbcTemplate.queryForList("SELECT * FROM PERSON");

        assertNotNull("Person list is null.", lPersonMaps);
        assertEquals("Number of persons should be " + SIZE + ".", SIZE, lPersonMaps.size());

        Map<String, Object> hPerson = lPersonMaps.get(0);

        logger.debug((beforeTransaction ? "Before" : "After") + " transaction.  " + hPerson.toString());
            
        Long id = (Long)hPerson.get("id");
        String name = (String)hPerson.get("name");
        String country = (String)hPerson.get("country");
        
        if (ID.equals(id)) {                
            assertEquals("Person name should be " + NAME + ".", NAME, name);
            assertEquals("Person country should be " + matchCountry + ".", matchCountry, country);
        }
    }

}
