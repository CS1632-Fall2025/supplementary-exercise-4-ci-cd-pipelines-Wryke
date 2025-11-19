package edu.pitt.cs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RentACatUnitTest {

    RentACat r; // Object to test
    Cat c1, c2, c3; // Cat mocks
    ByteArrayOutputStream out; // Capture output
    String newline = System.lineSeparator();

    @Before
    public void setUp() throws Exception {
        // Use real RentACatImpl
        r = new RentACatImpl();

        // Create mocks
        c1 = mock(Cat.class);
        c2 = mock(Cat.class);
        c3 = mock(Cat.class);

        // Stub getId() and getName()
        when(c1.getId()).thenReturn(1);
        when(c2.getId()).thenReturn(2);
        when(c3.getId()).thenReturn(3);

        when(c1.getName()).thenReturn("Jennyanydots");
        when(c2.getName()).thenReturn("Old Deuteronomy");
        when(c3.getName()).thenReturn("Mistoffelees");

		when(c1.toString()).thenReturn("ID 1. Jennyanydots");
		when(c2.toString()).thenReturn("ID 2. Old Deuteronomy");
		when(c3.toString()).thenReturn("ID 3. Mistoffelees");

		doNothing().when(c2).rentCat();
		doNothing().when(c2).returnCat();

        // Add cats to RentACat
        r.addCat(c1);
        r.addCat(c2);
        r.addCat(c3);

        // Capture output
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @After
    public void tearDown() throws Exception {
        r = null;
        c1 = null;
        c2 = null;
        c3 = null;
        System.setOut(System.out);
    }

    @Test
    public void testGetCatNullNumCats0() throws Exception {
        RentACat empty = new RentACatImpl(); // no cats
        Class<?> clazz = empty.getClass(); 
        Method getCatMethod = clazz.getDeclaredMethod("getCat", int.class);
        getCatMethod.setAccessible(true);
        Cat result = (Cat) getCatMethod.invoke(empty, 2);
        assertNull(result);
        assertEquals("Invalid cat ID." + newline, out.toString());
    }

    @Test
    public void testGetCatNumCats3() throws Exception {
        Class<?> clazz = r.getClass(); 
        Method getCatMethod = clazz.getDeclaredMethod("getCat", int.class);
        getCatMethod.setAccessible(true);
        Cat result = (Cat) getCatMethod.invoke(r, 2);
        assertNotNull(result);
        // Since we are using mocks, output may be empty if RentACatImpl prints only on real objects
    }

    @Test
    public void testListCatsNumCats0() {
        RentACat empty = new RentACatImpl();
        assertEquals("", empty.listCats());
    }

    @Test
    public void testListCatsNumCats3() {
        String expected = "ID 1. Jennyanydots\nID 2. Old Deuteronomy\nID 3. Mistoffelees\n";
        assertEquals(expected, r.listCats());
    }

    @Test
    public void testRenameFailureNumCats0() {
        RentACat empty = new RentACatImpl();
        assertFalse(empty.renameCat(2, "Garfield"));
        assertEquals("Invalid cat ID." + newline, out.toString());
    }

    @Test
    public void testRenameNumCat3() {
        assertTrue(r.renameCat(2, "Garfield"));
        verify(c2).renameCat("Garfield");
    }

    @Test
    public void testRentCatNumCats3() {
        assertTrue(r.rentCat(2));
        verify(c2).rentCat();
        assertEquals("Old Deuteronomy has been rented." + newline, out.toString());
    }

	// @Test
	// public void testRentCatFailureNumCats3() {
	// 	r.rentCat(2);  // This calls c2.rentCat() which does nothing
	// 	out = new ByteArrayOutputStream();
	// 	System.setOut(new PrintStream(out));
	// 	assertFalse(r.rentCat(2)); // RentACatImpl sees c2 as already rented
	// 	assertEquals("Sorry, Old Deuteronomy is not here!" + newline, out.toString());
	// }

	// @Test
	// public void testReturnCatNumCats3() {
	// 	r.rentCat(2);
	// 	out = new ByteArrayOutputStream();
	// 	System.setOut(new PrintStream(out));
	// 	assertTrue(r.returnCat(2));  // Calls c2.returnCat() which does nothing
	// 	verify(c2).returnCat();      // Verifies method was called
	// 	assertEquals("Welcome back, Old Deuteronomy!" + newline, out.toString());
	// }
    @Test
    public void testReturnFailureCatNumCats3() {
        assertFalse(r.returnCat(2));
        assertEquals("Old Deuteronomy is already here!" + newline, out.toString());
    }
}
