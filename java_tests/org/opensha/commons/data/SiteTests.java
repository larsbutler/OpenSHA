package org.opensha.commons.data;

import static org.junit.Assert.assertThat;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.matchers.JUnitMatchers.*;

import static org.hamcrest.CoreMatchers.*;

import org.opensha.commons.data.Site;
import org.opensha.commons.geo.Location;

public class SiteTests {

    /**
     * Check that sites equality works as expected
     */
    @Test
    public void checkSitesEquality() {
        Object s21_1  = new Site(new Location(2.0, 1.0));
        Object s21_2  = new Site(new Location(2.0, 1.0));
        Object s12_3a = new Site(new Location(1.0, 2.0), "a");
        Object s12_1  = new Site(new Location(1.0, 2.0));
        Object s12_2a = new Site(new Location(1.0, 2.0), "a");
        Object s12_3b = new Site(new Location(1.0, 2.0), "b");
        Object s12_4a = new Site(new Location(1.0, 2.0), "a");

        // same coordinates, null names
        assertThat(s21_1,  is(equalTo(s21_1)));
        assertThat(s21_1,  is(equalTo(s21_2)));
        // different coordinates, null names
        assertThat(s21_1,  is(not(equalTo(s12_1))));
        assertThat(s12_1,  is(not(equalTo(s21_1))));
        // same coordinates, same name
        assertThat(s12_2a, is(equalTo(s12_4a)));
        assertThat(s12_4a, is(equalTo(s12_2a)));
        // same coordinates, different names
        assertThat(s12_2a, is(not(equalTo(s12_3b))));
        assertThat(s12_3b, is(not(equalTo(s12_2a))));
        // one name null, the other not null
        assertThat(s12_2a, is(not(equalTo(s12_1))));
        assertThat(s12_1,  is(not(equalTo(s12_2a))));
    }

    @Test
    public void checkSitesHashing() {
        Object s21_1  = new Site(new Location(2.0, 1.0));
        Object s21_2  = new Site(new Location(2.0, 1.0));
        Object s12_1  = new Site(new Location(1.0, 2.0));
        Object s12_2  = new Site(new Location(1.0, 2.0));
        Object s12_2a = new Site(new Location(1.0, 2.0), "a");
        Object s12_3a = new Site(new Location(1.0, 2.0), "a");

        // this is more a sanity test than a real test
        assertThat(s21_1.hashCode(),  is(equalTo(s21_2.hashCode())));
        assertThat(s12_1.hashCode(),  is(equalTo(s12_2.hashCode())));
        assertThat(s12_2a.hashCode(), is(equalTo(s12_3a.hashCode())));
    }

    @Test
    public void checkHashMap() {
        HashMap<Site, Integer> map = new HashMap<Site, Integer>();

        for (int i = -90, k = 0; i < 90; ++i)
            for (int j = -90; j < 90; ++j, ++k)
                map.put(new Site(new Location(i, j)), k);

        for (int i = -90, k = 0; i < 90; ++i)
            for (int j = -90; j < 90; ++j, ++k)
            {
                int value = map.remove(new Site(new Location(i, j)));

                assertThat(k, is(equalTo(value)));
            }
    }
}
