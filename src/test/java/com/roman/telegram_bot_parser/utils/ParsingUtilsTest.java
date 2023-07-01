package com.roman.telegram_bot_parser.utils;

import com.roman.telegram_bot_parser.dao.Ad;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParsingUtilsTest {

    @Test
    void testParseToList() {

        String sampleHtml = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(sampleHtml);

        List<Ad> adList = ParsingUtils.parseToList(testDocument);

        // Assert that the size of the returned list matches the number of items in the test document
        assertEquals(2, adList.size());

        // Add more assertions to check specific properties of the Ad objects
        // For example, check the data-item-id, URL, price, and date
        Ad ad1 = adList.get(0);
        assertEquals(1, ad1.getId());
        assertEquals("100", ad1.getPrice());
        assertEquals("2023-07-01", ad1.getDate());

        Ad ad2 = adList.get(1);
        assertEquals(2, ad2.getId());
        assertEquals("200", ad2.getPrice());
        assertEquals("2023-07-02", ad2.getDate());
    }


    @Test
    void testParseToMap() {
        String sampleHtml = "<div class=\"items-items-kAJAg\" data-marker=\"catalog-serp\">" +
                "    <div data-marker=\"item\" data-item-id=\"1\">" +
                "        <a href=\"/item/1\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"100\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-01</span>" +
                "    </div>" +
                "    <div data-marker=\"item\" data-item-id=\"2\">" +
                "        <a href=\"/item/2\" itemprop=\"url\"></a>" +
                "        <span itemprop=\"price\" content=\"200\"></span>" +
                "        <span data-marker=\"item-date\">2023-07-02</span>" +
                "    </div>" +
                "</div>";
        Document testDocument = Jsoup.parse(sampleHtml);

        HashMap<String, Ad> entityHashMap = ParsingUtils.parseToMap(testDocument);

        // Assert that the size of the returned map matches the number of items in the test document
        assertEquals(2, entityHashMap.size());

        // Add more assertions to check specific properties of the Ad objects in the map
        Ad ad1 = entityHashMap.get("1");
        assertNotNull(ad1);
        assertEquals("https://www.avito.ru/item/1", ad1.getLink());
        assertEquals("100", ad1.getPrice());
        assertEquals("2023-07-01", ad1.getDate());

        Ad ad2 = entityHashMap.get("2");
        assertNotNull(ad2);
        assertEquals("https://www.avito.ru/item/2", ad2.getLink());
        assertEquals("200", ad2.getPrice());
        assertEquals("2023-07-02", ad2.getDate());
    }


}
